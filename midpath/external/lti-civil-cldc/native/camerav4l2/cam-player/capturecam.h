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
// Desc: Example application using camerav4l2 player module and library
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
///////////////////////////////////////////////////////////////////////////
#include <playerclient.h>
#include "playerv4l2.h"

class CaptureCam
{
    public:
        CaptureCam(const char *adress, int port, int width=352, int height=288);
        ~CaptureCam();
        bool StartCapture();
        bool StopCapture();
        bool Retrieve();
        unsigned char *GetImage(void) {  return out; }
        bool ShowHistograms(bool v) { show_hists=v; return true; }
        char *getName(void) { return crp->name; }

        int countControls() {  return crp->noOfControls;   }
        const char *getControlName(int id){  return crp->getControlName(id); }

        double getValue(const char *id){return crp->getControlValueS(id);}

        void setValue(const char *id, double val){crp->setControlValueS(id, val);}

        int resize(int w, int h){return crp->setFrameSize(w, h);}

        int selectSource(int id){return crp->selectSourceI(id);}

        const char *getSource(){return crp->inputName;}

        int doSwapRB(bool flag) {
            if (flag)
                return crp->setSwapRB(1);
            return crp->setSwapRB(0);
        }

        int selectField(const char *id) {
            isAlternate = strcmp(id, "ALTERNATE")==0;
            isAlternate = true;
            return crp->setFieldTypeS(id);
        }
        int getField(){return crp->field;}

        int getWidth(){return crp->width;}

        int getHeight(){return crp->height;}

        bool isGreyFrame() {return depth==8;}

        bool isField(int id) {
            if (isAlternate)
                return frameNo%2 == id;
            return id == 0;
        }

        int getDepth(){return depth;}

        int setMode(const char *frmt){
            formatPix = frmt;
            printf("Setting mode %s\n", frmt);
            return crp->setPixelFormatS(frmt);
        }

        int getMode(){return crp->pixelFormat;}

        void setPulMode(int mode);

        // public only for stats
        CameraV4L2Proxy *crp;
    protected:
        const char *formatPix;

        PlayerClient *robot;

        int frameNo;
        bool isAlternate;

        bool ProcessImage(unsigned char *p);
        bool ReadImage(void);

        int w,h;
        bool capturing;
        bool show_hists;
        char name[128];

        unsigned char *processed;
        unsigned char *out;
        int depth;
};

