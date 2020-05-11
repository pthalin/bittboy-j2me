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
//========================================================================
//
// Desc: Some helper functions for capturing from v4l2 device
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
//========================================================================
#include <sys/time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <stdio.h>

//#include <player/error.h>
#include "error.h" // KAL


#include "v4l2capture.h"

#include <string.h>
#include <math.h>




//==========================================================================
//  Definitions
//==========================================================================

#define FREE_ME(x) if (x) free(x); x = NULL;
#define CLEAR(x) memset(&x, 0, sizeof(x))


#define PERROR(str_err) PLAYER_ERROR2("%s: %s", (str_err), strerror(errno))


/// @brief Discover all inputs and fill internal list/table of sources
static int discover_inputs(FRAMEGRABBER2* fg);

/// @brief Print given control stats
static void printControl(struct v4l2_queryctrl *queryctrl);

/// @brief find all controls
static int discover_controls(FRAMEGRABBER2* fg);

/// @brief read back format values from device (i.e. after failure)
static void getPixelFormat(FRAMEGRABBER2 *fg);

/// @brief Use actual settings from fg on device
static int usePixelFormat(FRAMEGRABBER2 *fg);

/// @brief Assign mmaped buffers to device
static int assignMBufs(FRAMEGRABBER2 *fg);

/// @brief release mmaped buffers
static void releaseMBufs(FRAMEGRABBER2* fg);

/// @brief release all memory used by fg handler, including mmaped buffers
static void wyczyscStan(FRAMEGRABBER2* fg);

/// @brief Clean and close device
static void fg2_close(FRAMEGRABBER2* fg);


//==========================================================================
//                  General access functions
//==========================================================================


/**
 * Create device handler (just fill structure)
 * @return new device handler
 */
FRAMEGRABBER2* fg2_createFrameGrabber()
{
    FRAMEGRABBER2* fg = (FRAMEGRABBER2*)malloc( sizeof( FRAMEGRABBER2 ) );

    fg->device = 0;     // Device name, eg. "/dev/video"
    fg->fd = -1;         // File handle for open device

    fg->numOfIn = 0;    // Number of Inputa
    fg->sources = 0;    // Input sources[] (eg. TV, SVideo)
    fg->source = 0;     // Currently selected source

    fg->numOfCtls = 0;
    fg->controls = 0;

    fg->buffers = 0;   // memory mapped buffer
    fg->n_buffers = 0;

    fg->isCapturing = 0;
    fg->pix_fmt = V4L2_PIX_FMT_RGB24;
    fg->width = 640;
    fg->height = 480;
    fg->field = V4L2_FIELD_ANY;
    fg->altField = V4L2_FIELD_TOP;

    fg->cur_frame=-1;  // Currently capuring frame no.


    return fg;
}
//-----------------------------------------------


/**
 * delete completely fg handler (including whatever is needed to close device)
 * @param fg
 */
void fg2_delete(FRAMEGRABBER2** fg)
{
    fg2_close(*fg);
    FREE_ME(*fg);
}
//-----------------------------------------------

/**
 * open device and discover capabilities and inputs. To capture from device
 * You need to use fg2_startCapture first. Earlier You can set source and size.
 * @param fg framegrabber handler
 * @param dev pathc to /dev/videoX file
 * @return error status
 */
int fg2_open( FRAMEGRABBER2* fg, const char *dev)
{
    //FRAMEGRABBER2* fg = (FRAMEGRABBER2*)malloc( sizeof( FRAMEGRABBER2 ) );
    if (fg->device) {
        PLAYER_WARN("Device already opened?");
    }

  // Use default device if none specified
    if ( dev != NULL )
    {
        fg->device = strdup( dev );
    }
    else
    {
        fg->device = strdup( FG_DEFAULT_DEVICE );
    }

  // Open the video device
    fg->fd = open( fg->device, O_RDWR );
    if ( fg->fd == -1 )
    {
        PERROR( "fg2_open(): open video device failed" );
        free( fg->device );
        fg->device = 0;
        //free( fg );
        return -1;
    }
    PLAYER_MSG0(2,"opened v4l2 device");

  // Make sure child processes don't inherit video (close on exec)
    fcntl( fg->fd, F_SETFD, FD_CLOEXEC );

  // For n-ary buffering
    fg->cur_frame = -1;

  // Get the device capabilities
    if( ioctl( fg->fd, VIDIOC_QUERYCAP, &(fg->caps) ) < 0 )
    {
        PERROR( "fg2_open(): query capabilities failed" );
        fg2_close(fg);
        //free( fg );
        return -1;
    }
    // sprawdz czy to wystarcza
    if( ! (fg->caps.capabilities && V4L2_CAP_VIDEO_CAPTURE) ){
        PLAYER_ERROR("V4L2_CAP_VIDEO_CAPTURE not supported");
        fg2_close(fg);
        return -1;
    }
    if( ! (fg->caps.capabilities && V4L2_CAP_STREAMING) ){
        PLAYER_ERROR("V4L2_CAP_STREAMING not supported");
        fg2_close(fg);
        return -1;
    }
    PLAYER_MSG2(1,"Found %s card with %s v4l2 driver", fg->caps.card, fg->caps.driver);

    /*+++++++++++++++++++++++++++*/
  // Read info for all input sources
    discover_inputs(fg);
    if (fg->numOfIn<=0){
        PLAYER_ERROR("No Inputs found");
        fg2_close(fg);
        return -1;
    }

    return 0;
}

//==========================================================================
//                  Capturing
//==========================================================================


/**
 * Turn capture stream on. You need to use it by hand after opening, or stopping
 * @return error status
 */
int fg2_startCapture(FRAMEGRABBER2* fg)
{
    unsigned int i;
    enum v4l2_buf_type type;

    if (fg->isCapturing) {
        PLAYER_WARN("Already capturing");
        return -1;
    }


    PLAYER_MSG0(2,"Assigning buffers");
    if (assignMBufs(fg)!=0){
        PERROR( "fg2_startCapture(): assignMBufs" );
        return -1;
    }

    PLAYER_MSG0(2,"Adding buffers to dirver Queue");
    for (i = 0; i < fg->n_buffers; ++i)
    {
        struct v4l2_buffer buf;

        CLEAR (buf);

        buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        buf.memory      = V4L2_MEMORY_MMAP;
        buf.index       = i;

        if (-1 == ioctl (fg->fd, VIDIOC_QBUF, &buf)) {
            PERROR("VIDIOC_QBUF");
            return -1;
        }
    }

    type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

    PLAYER_MSG0(2,"Turning streaming on");
    if (-1 == ioctl (fg->fd, VIDIOC_STREAMON, &type)) {
        PERROR ("VIDIOC_STREAMON");
        return -1;
    }
    fg->isCapturing = 1;
    PLAYER_MSG0(0,"Capturing started");
    return 0;
}

/**
 * Turn capture stream of. You need to use it by hand when changing format
 * or size of frame. Only controls doesn't need stopping
 * @return error status
 */
int fg2_stopCapture(FRAMEGRABBER2* fg)
{
    enum v4l2_buf_type type;

    PLAYER_MSG0(2,"stopping capturing");
    if (!fg->isCapturing){
        PLAYER_WARN("Capturing not started yet");
        return -1;
    }
    type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

    // Streamoff sam odkolejkowuje bufory
    PLAYER_MSG0(2,"stopping streaminfg");
    if (-1 == ioctl (fg->fd, VIDIOC_STREAMOFF, &type)) {
        PERROR ("VIDIOC_STREAMOFF");
        //return -1;
    }
    PLAYER_MSG0(2,"Releasing buffers");
    releaseMBufs(fg);
    PLAYER_MSG0(0,"capture stopped");
    fg->isCapturing = 0;
    return 0;
}



/**
 * light capture interface, gives live data without any memcpy (just after
 * dequeueing)
 * @param fg framegrabber struct
 * @return bufer struct (dont change it, just read)
 */
struct my_buffer *getFrameBuffer( FRAMEGRABBER2* fg )
{
    static struct v4l2_buffer buf;
    int rv;
    //CLEAR (buf);

    buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    buf.memory = V4L2_MEMORY_MMAP;
    buf.field = fg->altField;
    if (buf.field == V4L2_FIELD_TOP)
        buf.field = V4L2_FIELD_BOTTOM;
    else
        buf.field = V4L2_FIELD_TOP;

    rv = ioctl(fg->fd, VIDIOC_DQBUF, &buf);
    if (rv==-1)
    {
        switch (errno)
        {
            case EAGAIN:
                PERROR("VIDIOC_DQBUF, eagain");
                return NULL;
            default:
                PERROR("VIDIOC_DQBUF");
                return NULL;
        }
    }

    if (buf.index >= fg->n_buffers) {
        PLAYER_ERROR2("Something wrong: buf.index(%d) >= n_buffers(%d)", buf.index, fg->n_buffers);
        if (-1 == ioctl (fg->fd, VIDIOC_QBUF, &buf)) {
            PERROR("VIDIOC_QBUF");
        }
        return NULL;
    }
    //memcpy(&(fg->buffers[buf.index].buf), &buf, sizeof(buf));
    fg->buffers[buf.index].buf = &buf;

    return fg->buffers + buf.index;
}


/**
 * This is opposite to getFrameBuffer. It Queues used buffer to the driver.
 * @param fg framegrabber handler
 * @param mBuff earlier received buffer with image and its state
 */
void giveBackFrameBuffer( FRAMEGRABBER2* fg, struct my_buffer *mBuff)
{
    if (mBuff==NULL)
        return;
    if (-1 == ioctl (fg->fd, VIDIOC_QBUF, mBuff->buf)) {
        PERROR("VIDIOC_QBUF");
    }
}


//==========================================================================
//                  Source selection
//==========================================================================

/**
 * Returns number of found inputs
 */
int fg2_get_source_count( FRAMEGRABBER2* fg )
{
    return fg->numOfIn;
}

/**
 * Current source id
 */
int fg2_get_source_id( FRAMEGRABBER2* fg)
{
    int out=0;

    if ( ioctl( fg->fd, VIDIOC_G_INPUT, &out ) < 0 )
    {
        PERROR( "fg2_get_source_id(): VIDIOC_G_INPUT failed" );
        return -1;
    }
    return out;
}

/**
 * Current source name
 */
const char* fg2_get_source_name( FRAMEGRABBER2* fg)
{
    return fg2_get_id_source_name(fg, fg2_get_source_id(fg));
}

/**
 * Name of given source identified by id
 */
const char* fg2_get_id_source_name( FRAMEGRABBER2* fg, int src )
{
    if ( src<0 || (unsigned int)src > fg->numOfIn )
    {
        PLAYER_ERROR("fg2_get_source_name(): Invalid src number!" );
        return NULL;
    }

    return (const char *)fg->sources[src].name;
}

/**
 * Name of device
 */
const char * fg2_get_device_name( FRAMEGRABBER2* fg )
{
    return (const char *)(fg->caps.card);
}

/**
 * Select input source. if src<0, then strSrc is used to compare with known
 * sources
 * @return error status
 */
int fg2_set_source( FRAMEGRABBER2* fg, int srcNo, const char *strSrc )
{
    if (srcNo<0) {
        PLAYER_MSG1(1,"\t\tSetting SourceS: %s", strSrc);
        if (strSrc==NULL)
            srcNo=0;
        else {
            for(srcNo=0; (unsigned int)srcNo < fg->numOfIn; srcNo++)
                if (strcasecmp(strSrc,(const char *)fg->sources[srcNo].name)==0)
                    break;
            if ((unsigned int)srcNo >= fg->numOfIn)
                srcNo = -1;
        }
    } else
        PLAYER_MSG1(1,"\t\tSetting SourceI: %d", srcNo);

    if ( srcNo<0 ||  (unsigned int)srcNo >= fg->numOfIn )
    {
        PLAYER_ERROR2("fg2_set_sourced(): Invalid source number (%d > %d)r!", srcNo, fg->numOfIn );
        return -1;
    }
    PLAYER_MSG3(1,"selecting\t%d - %s (%d)", srcNo, fg->sources[srcNo].name, fg->sources[srcNo].type);

    fg->source = srcNo;

    if ( ioctl( fg->fd, VIDIOC_S_INPUT, &(fg->sources[srcNo]) ) < 0 )
    {
        PERROR( "fg2_set_source(): set source failed" );
        return -1;
    }
    // controls can be different per source
    discover_controls(fg);

    return 0;
}



//==========================================================================
//                  Changing settings by controls
//==========================================================================

/**
 * Returns number of found controls
 */
int fg2_countControls( FRAMEGRABBER2* fg)
{
    return fg->numOfCtls;
}

/**
 * Sets new value to control. Val is in 0.0-1.0 range. Values <0 means use default
 * Control is identified by id in local table
 */
int fg2_setControlValueI( FRAMEGRABBER2* fg, int id, double val)
{
    struct v4l2_control control;
    unsigned int uVal;

    if (id<0 || (unsigned int)id>fg->numOfCtls)
        return -1;

    PLAYER_MSG2(2, "Setting value %f to '%s'", val, fg->controls[id].name);

    uVal = fg->controls[id].default_value;

    if (val>1.0){
        PLAYER_WARN1("value %f out of range for control:", val);
        printControl(&(fg->controls[id]));
        return -2;
    }
    if (val>=0.0 && fg->controls[id].minimum != fg->controls[id].maximum) {
        uVal = fg->controls[id].minimum +
                (unsigned int )( val*(fg->controls[id].maximum
                - fg->controls[id].minimum));
        PLAYER_MSG2(2, "uint value %u of '%f'", uVal, val);
    }

    memset (&control, 0, sizeof (control));
    control.id = fg->controls[id].id;
    control.value = uVal;
    if (ioctl (fg->fd, VIDIOC_S_CTRL, &control)!=0){
        PERROR("VIDIOC_S_CTRL");
        PLAYER_WARN1("Failed to set value %f to control:", val);
        printControl(&(fg->controls[id]));
        return -3;
    }
    usleep(50000);
    PLAYER_MSG1(2, "Value written %u'", control.value);

    return 0;
}

/**
 * Sets new value to control. Val is in 0.0-1.0 range. Values <0 means use default
 * Control is identified by its name
 */
int fg2_setControlValue( FRAMEGRABBER2* fg, const char *id, double val)
{
    unsigned int i;
    for(i=0; i<fg->numOfCtls; i++){
        if (strcasecmp(id, (const char *)fg->controls[i].name) == 0){
            return fg2_setControlValueI(fg, i, val);
        }
    }
    return -1;
}

/**
 * Gets current control value. <0 means error.
 * Control is identified by id in local table
 */
double fg2_getControlValueI( FRAMEGRABBER2* fg, int id)
{
    struct v4l2_control control;
    double dval;

    if (id<0 || (unsigned int)id>fg->numOfCtls)
        return -1;

    memset (&control, 0, sizeof (control));
    control.id = fg->controls[id].id;
    if (ioctl (fg->fd, VIDIOC_G_CTRL, &control)!=0){
        PERROR("VIDIOC_G_CTRL");
        PLAYER_WARN("Failed to get value of control:");
        printControl(&(fg->controls[id]));
        return -3;
    }

    if (fg->controls[id].maximum == fg->controls[id].minimum )
        return 0.0;
    dval = (double)(control.value - fg->controls[id].minimum)
                   / (fg->controls[id].maximum-fg->controls[id].minimum);
    PLAYER_MSG2(2, "uint value %u of '%f'", control.value, dval);
    return dval;
}

/**
 * Gets current control value. <0 means error.
 * Control is identified by its name
 */
double fg2_getControlValue( FRAMEGRABBER2* fg, const char *id)
{
    unsigned int i;
    for(i=0; i<fg->numOfCtls; i++){
        if (strcasecmp(id, (const char *)fg->controls[i].name) == 0){
            return fg2_getControlValueI(fg, i);
        }
    }
    return -1.0;
}

const char *fg2_getControlName( FRAMEGRABBER2* fg, int id)
{
    if (id<0 || (unsigned int)id>=fg->numOfCtls)
        return 0;
    return (const char *)fg->controls[id].name;
}

//==========================================================================
//                  Other settings
//==========================================================================


/**
 * pal, ntsc, secam, etc.
 */
int fg2_set_source_norm( FRAMEGRABBER2* fg, v4l2_std_id norm )
{
    PLAYER_MSG0(1,"setting new norm");
    return 0;
    if ( ioctl( fg->fd, VIDIOC_S_STD, &norm ) < 0 )
    {
        PERROR( "Mfg2_set_source_norm(): set channel/norm failed" );
        return -1;
    }

    return 0;
}


/**
 * Tries to apply given frame settings
 */
int fg2_setPixelSettings( FRAMEGRABBER2* fg, int width, int height,
                          unsigned int fmt, enum v4l2_field fld, int dpth )
{
    PLAYER_MSG0(2,"fg2_set_capture_window()");

    fg->width = width;
    fg->height = height;
    fg->pix_fmt = fmt;
    fg->field = fld;
    fg->depth = dpth;
    ;
    return usePixelFormat(fg);
}





//==========================================================================
//                  static functions
//==========================================================================



/**
 * Discover all inputs and fill internal list/table of sources
 * @param fg device
 * @return error status
 */
static int discover_inputs(FRAMEGRABBER2* fg)
{
    struct v4l2_input input2;
    unsigned int i;
    fg->numOfIn=0;
    PLAYER_MSG0(2,"discover_inputs()");

    // okresl ile:
    for (fg->numOfIn = 0;; fg->numOfIn++) {
        memset(&input2,0,sizeof(input2));
        input2.index = fg->numOfIn;
        if (0 != ioctl( fg->fd, VIDIOC_ENUMINPUT, &input2 ) )
            break;
    }
    fg->source = 0;
    fg->sources = (struct v4l2_input *)malloc( sizeof( struct v4l2_input )*fg->numOfIn );
    PLAYER_MSG1(1,"Found sources: %d", fg->numOfIn);
    for ( i = 0; i < fg->numOfIn; i++ ) {
        fg->sources[i].index = i;
        ioctl( fg->fd, VIDIOC_ENUMINPUT, &(fg->sources[i]) );
        PLAYER_MSG3(1,"\t%d - %s (%d)", i, fg->sources[i].name, fg->sources[i].type);
    }

    return 0;
}




const char *ctlTypeToString[] = {
    "Unknown",
    "Integer", // 0-max-1
    "Boolean", // 0/1
    "Menu",    // 0-max-1
    "Button",  // dont care
};


static void printControl(struct v4l2_queryctrl *queryctrl){
    double procent = (queryctrl->default_value-queryctrl->minimum)*1.0
                   / (queryctrl->maximum-queryctrl->minimum);
    PLAYER_MSG6(1,"\t\"%s\" = %0.4f (def: %d, min: %d, max: %d) of %s",
          queryctrl->name,
          procent,
          queryctrl->default_value,
          queryctrl->minimum,
          queryctrl->maximum,
          ctlTypeToString[queryctrl->type]);
}


static int discover_controls(FRAMEGRABBER2* fg)
{
    struct v4l2_queryctrl queryctrl;
    unsigned int counter;
    int i=0;
    PLAYER_MSG0(1,"Discovering controls:");

    fg->numOfCtls = 0;
    FREE_ME(fg->controls);

    memset (&queryctrl, 0, sizeof (queryctrl));

    for (queryctrl.id = V4L2_CID_BASE;
                queryctrl.id < V4L2_CID_LASTP1 && i<10000;
                queryctrl.id++, i++) {
        if (0 == ioctl (fg->fd, VIDIOC_QUERYCTRL, &queryctrl)) {
            if (queryctrl.flags & V4L2_CTRL_FLAG_DISABLED)
                continue;

            fg->numOfCtls++;

            //if (queryctrl.type == V4L2_CTRL_TYPE_MENU)
            //    enumerate_menu ();
        } else {
            if (errno == EINVAL)
                continue;

            PERROR ("VIDIOC_QUERYCTRL");
            break;
        }
    }

    for (queryctrl.id = V4L2_CID_PRIVATE_BASE;;queryctrl.id++) {
        if (0 == ioctl (fg->fd, VIDIOC_QUERYCTRL, &queryctrl)) {
            if (queryctrl.flags & V4L2_CTRL_FLAG_DISABLED)
                continue;

            fg->numOfCtls++;

            //if (queryctrl.type == V4L2_CTRL_TYPE_MENU)
            //    enumerate_menu ();
        } else {
            if (errno == EINVAL) {
                if (queryctrl.id - V4L2_CID_PRIVATE_BASE < 100)
                    continue;
                break;
            }

            PERROR ("VIDIOC_QUERYCTRL");
            break;
        }
    }

    if (fg->numOfCtls<=0) {
        PLAYER_WARN("\tNo controls");
        return  0;
    }
    fg->controls = (struct v4l2_queryctrl *)malloc( sizeof( struct v4l2_queryctrl )*fg->numOfCtls );
    counter = 0;



    memset (&queryctrl, 0, sizeof (queryctrl));


    i=0;
    for (queryctrl.id = V4L2_CID_BASE;
                queryctrl.id < V4L2_CID_LASTP1 && i<10000;
                queryctrl.id++, i++) {
        if (counter >= fg->numOfCtls)
            break;
        memset (&(fg->controls[counter]), 0, sizeof (fg->controls[counter]));
        fg->controls[counter].id = queryctrl.id;
        if (0 == ioctl (fg->fd, VIDIOC_QUERYCTRL, &(fg->controls[counter]))) {
            if (queryctrl.flags & V4L2_CTRL_FLAG_DISABLED)
                continue;

            printControl(&(fg->controls[counter]));
            counter++;

            //if (queryctrl.type == V4L2_CTRL_TYPE_MENU)
            //    enumerate_menu ();
        } else {
            if (errno == EINVAL)
                continue;

            PERROR ("VIDIOC_QUERYCTRL");
            break;
        }
    }

    for (queryctrl.id = V4L2_CID_PRIVATE_BASE;;queryctrl.id++) {
        if (counter >= fg->numOfCtls)
            break;
        memset (&(fg->controls[counter]), 0, sizeof (fg->controls[counter]));
        fg->controls[counter].id = queryctrl.id;
        if (0 == ioctl (fg->fd, VIDIOC_QUERYCTRL, &(fg->controls[counter]))) {
            if (queryctrl.flags & V4L2_CTRL_FLAG_DISABLED)
                continue;

            printControl(&(fg->controls[counter]));
            counter++;

            //if (queryctrl.type == V4L2_CTRL_TYPE_MENU)
            //    enumerate_menu ();
        } else {
            if (errno == EINVAL) {
                if (queryctrl.id - V4L2_CID_PRIVATE_BASE < 100)
                    continue;
                break;
            }

            PERROR ("VIDIOC_QUERYCTRL");
            break;
        }
    }

    if (counter<fg->numOfCtls)
        fg->numOfCtls = counter;



    return 0;
}

static void getPixelFormat(FRAMEGRABBER2 *fg){
    char tmpp[5];
    tmpp[0] = tmpp[4] = 0;
    CLEAR (fg->frmt);
    fg->frmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    if (-1 == ioctl (fg->fd, VIDIOC_G_FMT, &(fg->frmt)))
    {
        PLAYER_ERROR1("VIDIOC_G_FMT: %s", strerror(errno));
        memcpy(tmpp, &(fg->frmt.fmt.pix.pixelformat), 4);
        PLAYER_ERROR3("got (%d, %d) %s", fg->frmt.fmt.pix.width, fg->frmt.fmt.pix.height, tmpp);
    } else {
        fg->width = fg->frmt.fmt.pix.width;
        fg->height = fg->frmt.fmt.pix.height;
        fg->pix_fmt = fg->frmt.fmt.pix.pixelformat;
        fg->field = fg->frmt.fmt.pix.field;
    }

}

/**
 * Use actual settings from fg on device
 * @return error status
 */
static int usePixelFormat(FRAMEGRABBER2 *fg)
{
    char tmpp[5];
    tmpp[0] = tmpp[4] = 0;
    memcpy(tmpp, &(fg->pix_fmt), 4);
    PLAYER_MSG3(1,"Changing pixel and frame format (%dx%d, %s)", fg->width, fg->height, tmpp);
	//struct v4l2_format fmt;
    CLEAR (fg->frmt);
    fg->frmt.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    fg->frmt.fmt.pix.width       = fg->width;
    fg->frmt.fmt.pix.height      = fg->height;

    fg->frmt.fmt.pix.pixelformat = fg->pix_fmt;
    fg->frmt.fmt.pix.field       = fg->field;

    if (-1 == ioctl (fg->fd, VIDIOC_S_FMT, &(fg->frmt)))
    {
        PLAYER_ERROR1("VIDIOC_S_FMT: %s", strerror(errno));
        memcpy(tmpp, &(fg->frmt.fmt.pix.pixelformat), 4);
        PLAYER_ERROR3("got (%d, %d) %s", fg->frmt.fmt.pix.width, fg->frmt.fmt.pix.height, tmpp);
        //return 0;
        getPixelFormat(fg);
        return -1;
    }
    if (fg->frmt.fmt.pix.bytesperline*8 != fg->depth*fg->width) {
        PLAYER_ERROR("Padding unsuported");
        getPixelFormat(fg);
        printf("Test");
        //return -1;
    }

    if (	fg->frmt.fmt.pix.width != fg->width ||
                fg->frmt.fmt.pix.height != fg->height )
    {
        PLAYER_ERROR("Failed to apply required frame size settings");
        getPixelFormat(fg);
        return -1;
    }

    return 0;
}



/**
 * Assign mmaped buffers to device
 * @return error status
 */
static int assignMBufs(FRAMEGRABBER2 *fg){
    struct v4l2_requestbuffers req;
    PLAYER_MSG0(2,"assignMBufs()");

    CLEAR (req);

    req.count               = 2; // liczba buforow. minimum 2, moze byc wiecej
    req.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    req.memory              = V4L2_MEMORY_MMAP;

    if (-1 == ioctl (fg->fd, VIDIOC_REQBUFS, &req)) {
        if (EINVAL == errno) {
            PLAYER_ERROR ("device does not support " "memory mapping");
            PLAYER_ERROR1("device does not support " "memory mapping: %s", strerror(errno));
        } else {
            PLAYER_ERROR1("VIDIOC_REQBUFS: %s", strerror(errno));
        }
        return -1;
    }

    if (req.count < 2)
    {
        PLAYER_ERROR ("Insufficient buffer memory on device");
        return -1;
    }

    //FREE_ME( fg->buffers );
    fg->buffers = (struct my_buffer*)calloc (req.count, sizeof (*fg->buffers));

    if (!fg->buffers)
    {
        PLAYER_ERROR ( "Out of memory");
        return -1;
    }

    // powiadom sterownik v4l2 o posiadanych buforach
    for (fg->n_buffers = 0; fg->n_buffers < req.count; ++fg->n_buffers)
    {
        struct v4l2_buffer buf;

        CLEAR (buf);

        buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
        buf.memory      = V4L2_MEMORY_MMAP;
        buf.index       = fg->n_buffers;

        if (-1 == ioctl (fg->fd, VIDIOC_QUERYBUF, &buf)){
            PLAYER_ERROR1("VIDIOC_QUERYBUF: %s", strerror(errno));
            return -1;
        }

        fg->buffers[fg->n_buffers].id = fg->n_buffers;
        fg->buffers[fg->n_buffers].length = buf.length;
        fg->buffers[fg->n_buffers].start = mmap(
                    NULL /* start anywhere */,
                    buf.length,
                    PROT_READ | PROT_WRITE /* required */,
                    MAP_SHARED /* recommended */,
                    fg->fd, buf.m.offset
                );

        if (MAP_FAILED == fg->buffers[fg->n_buffers].start) {
            PLAYER_ERROR1("MMAP: %s", strerror(errno));
            return -1;
        }
    }

    return 0;
}

static void releaseMBufs(FRAMEGRABBER2* fg){
    unsigned int i;
    for (i = 0; i < fg->n_buffers; ++i) {
        if (-1 == munmap (fg->buffers[i].start, fg->buffers[i].length))
            PLAYER_ERROR1("munmap: %s", strerror(errno));
    }
    FREE_ME(fg->buffers);
    fg->n_buffers = 0;

}





/**
 * release all memory used by fg handler, including mmaped buffers
 */
static void wyczyscStan(FRAMEGRABBER2* fg)
{
    unsigned int i;
    if (fg->isCapturing)
        fg2_stopCapture(fg);

    for (i = 0; i < fg->n_buffers; ++i)
        if (-1 == munmap (fg->buffers[i].start, fg->buffers[i].length))
            PERROR("munmap");
    fg->n_buffers=0;

    //if (fg->fd>0)
    //    close( fg->fd );
    //fg->fd = 0;

    // Make sure we free all memory (backwards!)
    FREE_ME( fg->buffers );
    FREE_ME( fg->device );
    FREE_ME( fg->sources );
    //free( fg );
}


/**
 * Clean and close device
 * @param fg
 */
static void fg2_close(FRAMEGRABBER2* fg)
{
    wyczyscStan(fg);
    if (fg->fd>0)
        close( fg->fd );
    fg->fd = 0;
}


/*
static int fg2_get_source_type( FRAMEGRABBER2* fg, int ch )
{
    if ( ch<0 || (unsigned int)ch > fg->numOfIn )
    {
        PLAYER_ERROR("fg2_get_source_type(): Invalid channel number!" );
        return -1;
    }

    return fg->sources[ch].type;
}//*/





