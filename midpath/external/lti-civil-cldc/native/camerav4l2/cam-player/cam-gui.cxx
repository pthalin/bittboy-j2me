/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
///////////////////////////////////////////////////////////////////////////
//
// Desc: Example application using camerav4l2 player module and library (gui widgets)
//       It requires fltk libs
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
///////////////////////////////////////////////////////////////////////////
#include <stdio.h>
#include <assert.h>
#include <string.h>
#include <unistd.h>
#include <sys/time.h>

#include <FL/Fl.H>
#include <FL/Fl_Button.H>
#include <FL/Fl_Check_Button.H>
#include <FL/Fl_Window.H>
#include <FL/Fl_Value_Slider.H>
#include <FL/Fl_Slider.H>
#include <FL/Fl_Choice.H>
#include <FL/Fl_Output.H>

#include <FL/fl_draw.H>
#include <FL/fl_message.H>
#include <FL/fl_file_chooser.H>
#include <FL/fl_ask.H>
#include <FL/Fl_Int_Input.H>

#include "capturecam.h"

static int width=640;
static int height=480;

static int max_width=800;
static int max_height=600;

static int h_size = 400;

static CaptureCam *cam=0;


static Fl_Window *window=0;

static Fl_Check_Button *b_preview;
static Fl_Button *b_ppm;
static Fl_Button *b_about;
static Fl_Button *b_quit;
static Fl_Button *b_resize;
static Fl_Output *o_fps_label;
static Fl_Int_Input *o_w_label;
static Fl_Int_Input *o_h_label;
static Fl_Output *o_inLabel;
static Fl_Output *o_formatLabel;


void updateSize();

class CamView : public Fl_Widget
{
    int id;
    public:
        CamView(): Fl_Widget(0, 0, width,height,"camview")
        {
        }
        ~CamView() {}
        void draw(void)
        {
            assert(cam);
            unsigned char *pixels = cam->GetImage();
            assert(pixels);

            if ((width != cam->getWidth()) || (height != cam->getHeight()) ) {
                updateSize();
            } else {
                if (cam->isGreyFrame())
                    fl_draw_image_mono(pixels, 0, 0, width, height);
                else
                    fl_draw_image(pixels, 0, 0, width, height, cam->getDepth()/8);
            }


        }


};
class ImageWindow;
ImageWindow *imgWindow[2];

static CamView *cv;

class ImageWindow: public Fl_Double_Window {
    public:
        ImageWindow(char *name, int x, int y, int w, int h):Fl_Double_Window(x,y,w,h,name){
            {
                char buf[10];
                sprintf(buf, "%d", width);
                o_w_label->value(buf);
                sprintf(buf, "%d", height);
                o_h_label->value(buf);

                cv = new CamView();
            }
            this->end();
        }

        void hide(){
            Fl_Double_Window::hide();
            cam->StopCapture();
            b_preview->value(false);
        }
        void show(){
            updateSize();
            Fl_Double_Window::show();
            cam->StartCapture();
            b_preview->value(true);
        }
};



void updateSize(){
    char buf[10];
    width = cam->getWidth();
    height = cam->getHeight();
    if (imgWindow[0])
        imgWindow[0]->resize(imgWindow[0]->x(), imgWindow[0]->y(), width, height);

    printf("resizeUpdate %d %d\n", width, height);
    sprintf(buf, "%d", width);
    o_w_label->value(buf);
    sprintf(buf, "%d", height);
    o_h_label->value(buf);
}

static char fps_Buffer[128];
static char stat_Buffer[128];
static int frames_counter=0;
struct timeval tv1, tv2;
long timers;


static void idle_cb(void *)
{
    if (cam->Retrieve())
    {
        frames_counter++;
        //Time update?
        gettimeofday(&tv2,0);
        timers = (tv2.tv_sec - tv1.tv_sec)*1000000 + (tv2.tv_usec - tv1.tv_usec);
        if (timers > 1000000){
            // update fps every second
            tv1 = tv2;
            float fps = (float)(frames_counter*1000000)/timers;
            float eache = (float)timers/(frames_counter*1000000);
            sprintf(fps_Buffer,"%.2f f/s, %.3f s/f, %.2f kB/s", fps, eache, fps*cam->crp->imageSize/1024);
            o_fps_label->value(fps_Buffer);
            sprintf(stat_Buffer,"%dx%d @%db, size: %dB",
                    cam->getWidth(),
                    cam->getHeight(),
                    cam->crp->depth,
                    cam->crp->imageSize
                    );
            o_formatLabel->value(stat_Buffer);
            frames_counter = 0;
        }

        if(b_preview->value()) {
            if (cam->isField(0))
                imgWindow[0]->redraw();
        }

    }
    else
    {
        usleep(50000);
//       schedule();
    }
}


static void about_cb(Fl_Widget *)
{
    fl_message
            (
            "Simple capture program using new v4l2 driver\n"
            "for player serwer, by Marcin Rudowski (2006)\n"
            );
}



static void quit_cb(Fl_Widget *)
{
    delete window;
    delete cam;
    printf("quiting\n");
    exit(0);
}



static void sliders_cb(Fl_Widget *o)
{
    Fl_Value_Slider *s = (Fl_Value_Slider *)o;
    cam->setValue(s->label(), s->value());
    usleep(20000);
    double d = cam->getValue(s->label());
    if (d>-0.01)
        s->value(d);
    s->redraw();
}

static void fileds_cb(Fl_Widget *o)
{
    Fl_Choice *c = (Fl_Choice*)o;
    if (cam->selectField(c->text())!=0)
        c->value(cam->getField());
}

static void datamodes_cb(Fl_Widget *o)
{
    Fl_Choice *c = (Fl_Choice*)o;
    cam->setPulMode(c->value());
}

static void input_cb(Fl_Widget *o)
{
    Fl_Choice *c = (Fl_Choice*)o;
    if (cam->selectSource(c->value())==0)
        o_inLabel->value(cam->getSource());
}


static void resize_cb(Fl_Widget *)
{
    int tw=-1, th=-1;
    sscanf(o_w_label->value(), "%d", &tw);
    sscanf(o_h_label->value(), "%d", &th);

    if (tw<0 || tw>max_width)
        tw = 640;
    if (th<0 || th>max_height)
        th = 480;

    cam->resize(tw, th);
}


static void grey_cb(Fl_Widget *o)
{
    //Fl_Check_Button *b = (Fl_Check_Button*)o;
    //cam->SetGrey(b->value());
    Fl_Choice *c = (Fl_Choice*)o;
    cam->setMode(c->text());
    c->value(cam->getMode());

}

static void preview_cb(Fl_Widget *)
{
    if (!b_preview->value()) {
        imgWindow[0]->hide();
    } else {
        imgWindow[0]->show();
    }
}

static void swap_cb(Fl_Widget *o)
{
    Fl_Check_Button *b = (Fl_Check_Button*)o;
    cam->doSwapRB(b->value());
}


int counter = 0;
static void captureppm_cb(Fl_Widget *)
{
    int sz=width*height*3;
    unsigned char *img = new unsigned char[sz];
    memcpy(img, cam->GetImage(), sz);

    char *fname = "./screen.ppm"; //fl_file_chooser("Capture to PPM", 0,0);
    char buff[1024];
    sprintf(buff,"./screen%4d.ppm",counter++);
    if (!fname)
    {
        delete [] img;
        return;
    }
    FILE *f=fopen(buff,"wb");
    if (!f)
    {
        fl_alert("Cannot open file %s for writing", fname);
        delete [] img;
        return;
    }
    fprintf(f,"P6 %d %d %d\n", width, height, 255);
    int rv=fwrite(img, sz, 1, f);
    assert(rv==1);
    fclose(f);

    delete [] img;
}


int main(int argc, char *argv[])
{
    char *ip = "127.0.0.1";
    int port=-1;

    gettimeofday(&tv1,0);
    Fl::scheme("plastic");

    fprintf(stderr,"Usage: %s [ip port [640 480] ]\n", argv[0]);

    if (argc>1)
        ip = argv[1];
    if (argc>2)
        sscanf(argv[2], "%d", &port);
    if (argc==5)
    {
        width=atoi(argv[3]);
        height=atoi(argv[4]);
    }

    cam = new CaptureCam(ip, port, width, height);

    window = new Fl_Window(0,0,h_size,50,cam->getName());
    //cv = new CamView();

    int vpos = 0;

    b_preview = new Fl_Check_Button(0,vpos,h_size/4,20,"Preview");
    b_preview->callback(preview_cb);
    //b_grey = new Fl_Check_Button(h_size/2,vpos,h_size/2,20,"Grey");
    //b_grey->callback(grey_cb);

    Fl_Choice *c_choiceboxy = new Fl_Choice(h_size/2,vpos,90,20,"Format");
    c_choiceboxy->callback(grey_cb);
    c_choiceboxy->add("RGB24",  0,0);
    c_choiceboxy->add("BGR24",  0,0);
    c_choiceboxy->add("RGB32",  0,0);
    c_choiceboxy->add("BGR32",  0,0);
    c_choiceboxy->add("GREY",  0,0);
    c_choiceboxy->add("SBGGR8",  0,0);
    c_choiceboxy->add("RGB565",  0,0);


    c_choiceboxy->add("RGB332",  0,0);
    c_choiceboxy->add("RGB555",  0,0);
    c_choiceboxy->add("RGB555X",  0,0);
    c_choiceboxy->add("RGB565X",  0,0);
    c_choiceboxy->add("YVU410",  0,0);
    c_choiceboxy->add("YVU420",  0,0);
    c_choiceboxy->add("YUYV",  0,0);
    c_choiceboxy->add("UYVY",  0,0);
    c_choiceboxy->add("YUV422P",  0,0);
    c_choiceboxy->add("YUV411P",  0,0);
    c_choiceboxy->add("Y41P",  0,0);
    c_choiceboxy->add("NV12",  0,0);
    c_choiceboxy->add("NV21",  0,0);
    c_choiceboxy->value(cam->getMode());

    vpos+=20;
    (new Fl_Check_Button(0,vpos,h_size/4,20,"Swap RB/BR"))->callback(swap_cb);
    vpos+=20;
    o_formatLabel = new Fl_Output(60,vpos, h_size-60, 20, "Stats:");
    vpos+=20;

    o_fps_label = new Fl_Output(60,vpos,h_size-60,20,"Speed:");

    vpos+=20;

    b_about = new Fl_Button(0,vpos,h_size/2,20,"About");
    b_about->callback(about_cb);
    b_ppm = new Fl_Button(h_size/2,vpos,h_size/2,20,"Capture");
    b_ppm->callback(captureppm_cb);

    vpos+=20;
    b_resize = new Fl_Button(0,vpos,h_size/2,20,"Resize");
    b_resize->callback(resize_cb);
    vpos+=20;
    o_w_label = new Fl_Int_Input(30,vpos,h_size/2-30,20,"W:");
    o_h_label = new Fl_Int_Input(30+h_size/2,vpos,h_size/2-30,20,"H:");
    vpos+=20;

    c_choiceboxy = new Fl_Choice(h_size/2,vpos,h_size/2,20,"Field Type");
    c_choiceboxy->callback(fileds_cb); vpos+=20;

    c_choiceboxy->add("ANY",  0,0);
    c_choiceboxy->add("NONE",  0,0);
    c_choiceboxy->add("TOP",  0,0);
    c_choiceboxy->add("BOTTOM",  0,0);
    c_choiceboxy->add("INTERLACED",  0,0);
    c_choiceboxy->add("SEQ_TB",  0,0);
    c_choiceboxy->add("SEQ_BT",  0,0);
    c_choiceboxy->add("ALTERNATE",  0,0);
    c_choiceboxy->value( cam->getField() );

    c_choiceboxy = new Fl_Choice(h_size/2,vpos,50,20,"Input");
    c_choiceboxy->callback(input_cb);

    c_choiceboxy->add("0",  0,0);
    c_choiceboxy->add("1",  0,0);
    c_choiceboxy->add("2",  0,0);
    c_choiceboxy->add("3",  0,0);
    c_choiceboxy->add("4",  0,0);
    c_choiceboxy->add("5",  0,0);
    c_choiceboxy->value(0);

    o_inLabel = new Fl_Output(h_size/2+55,vpos, h_size/2-55, 20, "");
    o_inLabel->value(cam->getSource());
    vpos+=20;

    c_choiceboxy = new Fl_Choice(h_size/2,vpos,h_size/2,20,"Player data mode:");
    c_choiceboxy->callback(datamodes_cb); vpos+=20;

    c_choiceboxy->add("PUSH_ALL",  0,0);
    c_choiceboxy->add("PULL_ALL",  0,0);
    c_choiceboxy->add("PUSH_NEW",  0,0);
    c_choiceboxy->add("PULL_NEW",  0,0);
    c_choiceboxy->add("PUSH_ASYNC",  0,0);

    vpos+=20;


    Fl_Value_Slider *slider;
    int ile = cam->countControls();
    printf("ile %d\n", ile);
    //vpos = 0;
    for(int i=0; i<ile; i++){
        char *lab = new char[33];
        strcpy(lab, cam->getControlName(i));
        slider = new Fl_Value_Slider(0,vpos,h_size,15,lab);
        slider->type(FL_HOR_NICE_SLIDER);
        slider->minimum(0.0);
        slider->maximum(1.0);
        slider->callback(sliders_cb);
        slider->value(cam->getValue(lab));
        printf("Added new control slider: %s (%f)\n", lab, slider->value());
        vpos+=45;
    }

    vpos+=20;
    b_quit = new Fl_Button(0,vpos,h_size/2,20,"Quit");
    b_quit->callback(quit_cb);

    vpos+=25;



    window->resize(0,0,h_size, vpos);
    //window->resizable(window);
    window->callback(quit_cb);

    window->end();
    Fl::visual(FL_RGB);

    b_preview->value(true);
    //b_grey->value(false); grey_cb(b_grey);


    imgWindow[0] = new ImageWindow("Capture", h_size+10, 0, width, height);

    window->show();
    imgWindow[0]->show();

    c_choiceboxy->value(2); //PUSH_NEW
    datamodes_cb(c_choiceboxy);

    cam->StartCapture();

    Fl::add_idle(idle_cb, cv);
    Fl::run();

    cam->StopCapture();
    delete cam;
}

