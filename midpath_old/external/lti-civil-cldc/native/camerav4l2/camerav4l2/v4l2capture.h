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
// Desc: Own helper functions for capturing from v4l2 device
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
//
// NOTE: this was based on similar library for v4l:
//  Project:        libfg - Frame Grabber interface for Linux
//  Author:         Gavin Baker <gavinb@antonym.org>
//  Homepage:       http://www.antonym.org/libfg
//--------------------------------------------------------------------------

//========================================================================
/** @file v4l2capture.h

@brief set of functions to grab frames from v4l2 device

This is a very light wrapper to v4l2 interface. It uses MMAP transfer method
only and doesn't do any copying, just passing assigned buffers filled by driver.
It's intention was to easily access device without need to prepate all
those structures for ioctl's.

To use it, first create FRAMEGRABBER2 object by @p fg2_createFrameGrabber

@par Authors

Marcin Rudowski


 */


#ifndef __V4L2CAPTURE__H_
#define __V4L2CAPTURE__H_


#ifdef __cplusplus
extern "C"
{
#endif


#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>

#include <sys/mman.h>
#include <sys/ioctl.h>

#include <linux/fs.h>
#include <linux/kernel.h>
#include <asm/types.h>          /* for videodev2.h */
#include <linux/videodev.h>


//==========================================================================
//  Definitions
//==========================================================================

// Standard device for fg2_open()
#define FG_DEFAULT_DEVICE       "/dev/video0"

// Normal capture size
#define FG_DEFAULT_WIDTH        640
#define FG_DEFAULT_HEIGHT       480

// Default input sources
#define FG_SOURCE_TV            0
#define FG_SOURCE_COMPOSITE     1
#define FG_SOURCE_SVIDEO        2


/**
 * stucture of buffer with image data with frame parameters (sequence, timestamp
 * input,
 */
struct my_buffer
{
    int id;
    void * start;
    size_t length;
    struct v4l2_buffer *buf;
};


/**
 * stucture of framgrabber context.
 */
typedef struct
{
    char*                       device;     // Device name, eg. "/dev/video"
    int                         fd;         // File handle for open device
    struct v4l2_capability      caps;       // Capabilities
    unsigned int                numOfIn;    // Number of Inputa
    struct v4l2_input*          sources;    // Input sources (eg. TV, SVideo)
    int                         source;     // Currently selected source
    struct v4l2_format          frmt;       // pixel format
    struct my_buffer            *buffers;   // memory mapped buffer
    unsigned int                n_buffers;

    struct v4l2_queryctrl       *controls;   // various countrols like brighteness etc
    unsigned int                numOfCtls;  // size of controls tab

    int                         isCapturing;
    unsigned int                pix_fmt;
    unsigned int                width;
    unsigned int                height;
    enum v4l2_field             field;
    int                         depth;
    enum v4l2_field             altField; // current field type in alternate mode




    struct v4l2_tuner          tuner;      // TV or Radio tuner
    int                         cur_frame;  // Currently capuring frame no.

} FRAMEGRABBER2;




//==========================================================================
//                  General access functions
//==========================================================================


/**
 * Create device handler (just fill structure)
 * @return new device handler
 */
FRAMEGRABBER2* fg2_createFrameGrabber();


/**
 * delete completely fg handler (including whatever is needed to close device)
 * @param fg
 */
void fg2_delete(FRAMEGRABBER2** fg);


/**
 * open device and discover capabilities and inputs. To capture from device
 * You need to use fg2_startCapture first. Earlier You can set source and size.
 * @param fg framegrabber handler
 * @param dev pathc to /dev/videoX file
 * @return error status
 */
int fg2_open( FRAMEGRABBER2* fg, const char *dev);

//==========================================================================
//                  Capturing
//==========================================================================


/**
 * Turn capture stream on. You need to use it by hand after opening, or stopping
 * @param fg framegrabber handler
 * @return error status
 */
int fg2_startCapture(FRAMEGRABBER2* fg);

/**
 * Turn capture stream of. You need to use it by hand when changing format
 * or size of frame. Only controls doesn't need stopping
 * @param fg framegrabber handler
 * @return error status
 */
int fg2_stopCapture(FRAMEGRABBER2* fg);



/**
 * light capture interface, gives live data without any memcpy (just after
 * dequeueing)
 * @param fg framegrabber handler
 * @return bufer struct (dont change it, just read)
 */
struct my_buffer *getFrameBuffer( FRAMEGRABBER2* fg );


/**
 * This is opposite to getFrameBuffer. It Queues used buffer to the driver.
 * @param fg framegrabber handler
 * @param mBuff earlier received buffer with image and its state
 */
void giveBackFrameBuffer( FRAMEGRABBER2* fg, struct my_buffer *mBuff);


//==========================================================================
//                  Source selection
//==========================================================================

/**
 * Returns number of found inputs
 */
int fg2_get_source_count( FRAMEGRABBER2* fg );

/**
 * Current source id
 */
int fg2_get_source_id( FRAMEGRABBER2* fg);

/**
 * Current source name
 */
const char* fg2_get_source_name( FRAMEGRABBER2* fg);

/**
 * Name of given source identified by id
 */
const char* fg2_get_id_source_name( FRAMEGRABBER2* fg, int src );

/**
 * Name of device
 */
const char * fg2_get_device_name( FRAMEGRABBER2* fg );

/**
 * Select input source. if src<0, then strSrc is used to compare with known
 * sources
 * @return error status
 */
int fg2_set_source( FRAMEGRABBER2* fg, int src, const char *strSrc );


//==========================================================================
//                  Changing settings by controls
//==========================================================================

/**
 * Returns number of found controls
 */
int fg2_countControls( FRAMEGRABBER2* fg);

/**
 * Sets new value to control. Val is in 0.0-1.0 range. Values <0 means use default
 * Control is identified by id in local table
 */
int fg2_setControlValueI( FRAMEGRABBER2* fg, int id, double val);
/**
 * Sets new value to control. Val is in 0.0-1.0 range. Values <0 means use default
 * Control is identified by its name
 */
int fg2_setControlValue( FRAMEGRABBER2* fg, const char *id, double val);

/**
 * Gets current control value. <0 means error.
 * Control is identified by id in local table
 */
double fg2_getControlValueI( FRAMEGRABBER2* fg, int id);
/**
 * Gets current control value. <0 means error.
 * Control is identified by its name
 */
double fg2_getControlValue( FRAMEGRABBER2* fg, const char *id);


const char *fg2_getControlName( FRAMEGRABBER2* fg, int id);

//==========================================================================
//                  Other settings
//==========================================================================


/**
 * pal, ntsc, secam, etc.
 */
int fg2_set_source_norm( FRAMEGRABBER2* fg, v4l2_std_id norm );


/**
 * Tries to apply given frame settings
 */
int fg2_setPixelSettings( FRAMEGRABBER2* fg, int width, int height,
                          unsigned int fmt, enum v4l2_field fld, int depth );



#ifdef __cplusplus
}
#endif

#endif /* __CAPTURE__H_ */
