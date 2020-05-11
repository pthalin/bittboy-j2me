//==========================================================================
///
///     \file capture.h
///
///     libfg - Frame Grabber interface for Linux
///
///     \brief Capture client interface
///
///     Provides a high-level C interface for controlling frame grabber and
///     TV tuner cards.  Uses the Video 4 Linux API (currently v1) and thus
///     supports any V4L supported device.
///
///     \author         Gavin Baker <gavinb@antonym.org>
///
///     \version        $Revision: 1.1 $
///
///     Homepage:       http://www.antonym.org/libfg
///
//--------------------------------------------------------------------------
//
//  libfg - Frame Grabber interface for Linux
//  Copyright (c) 2002 Gavin Baker
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
//  or obtain a copy from the GNU website at http://www.gnu.org/
//
//==========================================================================

#ifndef __CAPTURE__H_
#define __CAPTURE__H_

#ifdef __cplusplus__
extern "C" {
#endif /* __cplusplus__ */

#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>

#include <sys/mman.h>
#include <sys/ioctl.h>

#include <linux/fs.h>
#include <linux/kernel.h>
#include <linux/videodev.h>

#include "frame.h"

//==========================================================================
//  Definitions
//==========================================================================

// Standard device for fg_open()
#define FG_DEFAULT_DEVICE       "/dev/video"    /** Default video input */

// Normal capture size
#define FG_DEFAULT_WIDTH        640
#define FG_DEFAULT_HEIGHT       480

// Percentage of a ushort
#define FG_PERCENT(n)           ((n)*65535/100)
#define FG_50PC                 FG_PERCENT(50)

// Default input sources
#define FG_SOURCE_TV            0
#define FG_SOURCE_COMPOSITE     1
#define FG_SOURCE_SVIDEO        2

//--------------------------------------------------------------------------
///
///  \brief An opaque framegrabber handle.
///
/// Represents all information about a frame grabber device.  Returned by
/// fg_open(), and used as the first parameter for all other fg_*() calls.
///
//--------------------------------------------------------------------------
typedef struct
{
    char*                   device;     ///< Device name, eg. "/dev/video"
    int                     fd;         ///< File handle for open device
    struct video_capability caps;       ///< Capabilities
    struct video_channel*   sources;    ///< Input sources (eg. TV, SVideo)
    int                     source;     ///< Currently selected source
    struct video_tuner      tuner;      ///< TV or Radio tuner
    struct video_window     window;     ///< Capture window
    struct video_picture    picture;    ///< Picture controls (eg. bright)
    struct video_mmap       mmap;       ///< Memory-mapped info
    struct video_buffer     fbuffer;    ///< Frame buffer
    struct video_mbuf       mbuf;       ///< Memory buffer #frames, offsets
    void*                   mb_map;     ///< Memory-mapped buffer
    int                     cur_frame;  ///< Currently capuring frame no.

} FRAMEGRABBER;

//--------------------------------------------------------------------------
///
///     Opens and initialises the frame grabber device with some reasonable
///     default values, and queries for all capabilities.
///
///     \param   dev     Device name to open, eg. "/dev/video2"
///                      or NULL for "/dev/video".
///
///     \return The open framegrabber handle, or NULL in the case of an
///     error.
///
//--------------------------------------------------------------------------

extern
FRAMEGRABBER* fg_open( const char* dev );

//--------------------------------------------------------------------------
///
///     Closes an open framegrabber device, and releases all memory
///     allocated with it.
///
///     \param  fg      The framegrabber handle to close.
///
//--------------------------------------------------------------------------

extern
void fg_close( FRAMEGRABBER* fg );

//--------------------------------------------------------------------------
///
///     Reads a frame from the capture device, allocating a new FRAME
///     instance and returning it.  The frame will be allocated the maximum
///     size window in the default picture format.  Note that this is a
///     \em blocking read, and thus will wait until the next frame is
///     ready.  The caller is responsible for doing a frame_release()
///     when done with the frame (to free memory).
///
///     \param  fg      The framegrabber handle from which to capture
///
///     \return The most recently captured frame, or NULL on error
/// 
///     \note This function blocks!
///
//--------------------------------------------------------------------------

extern
FRAME* fg_grab( FRAMEGRABBER* fg );

//--------------------------------------------------------------------------
///
///     Reads a frame from the capture device, using the existing frame
///     storage as passed in.  Returns the same instance, with the contents
///     of the last frame.  Note that this is a *blocking* read, and
///     thus will wait until the next frame is ready.
///
///     \param  fg      The open framegrabber
///     \param  fr      An existing frame
///
///     \return The most recently captured frame, or NULL on error
///
///     \note This function blocks! The size \em must be correct!
///
//--------------------------------------------------------------------------

extern
FRAME* fg_grab_frame( FRAMEGRABBER* fg, FRAME* fr );


//---------------------------------------------------------------------------
///
///     Specifies the number of the video source to be used for the input
///     signal.  For example, tuner, composite or S/Video signal.
///
///     \param          fg          Framegrabber handle
///     \param          src         Source id (eg. FG_SOURCE_SVIDEO)
///
///  Returns:   0  on success,
///             -1 on failure
///
//---------------------------------------------------------------------------

extern
int fg_set_source( FRAMEGRABBER* fg, int src );


//--------------------------------------------------------------------------
///
///     Specifies the video signal norm (eg. PAL, NTSC, SECAM) for the
///     current input source.
///
///     \param          fg          Framegrabber handle
///     \param          norm        Signal norm (eg. VIDEO_MODE_PAL)
///
///     \return    0           On success
///             -1          Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_source_norm( FRAMEGRABBER* fg, int norm );


//--------------------------------------------------------------------------
///
///     Returns the number of input sources available.
///
///     \param          fg          Framegrabber handle
///
///     \return     >0          Sources (can be used in fg_set_source)
///
//--------------------------------------------------------------------------

extern
int fg_get_source_count( FRAMEGRABBER* fg );


//--------------------------------------------------------------------------
///
///     Returns a user-friendly name corresponding to the supplied channel
///     number.
///
///     \param          fg          Framegrabber handle
///     \param     src         Source id (eg. FG_SOURCE_TV)
///
///     \return    Name, like "Television"
///
//--------------------------------------------------------------------------

extern
char* fg_get_source_name( FRAMEGRABBER* fg, int src );


//--------------------------------------------------------------------------
///
///     Sets the TV tuner to the specified frequency.
///
///     \param          fg          Framegrabber handle
///     \param          freq        Tuner frequency, in MHz
///
///     \return     0           Success, tuned in
///                -1          Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_channel( FRAMEGRABBER* fg, float freq );


//--------------------------------------------------------------------------
///
///     Queries the current frequency of the TV tuner.
///
///     \param          fg          Framegrabber handle
///
///     \return    The frequency in MHz
///
//--------------------------------------------------------------------------

extern
float fg_get_channel( FRAMEGRABBER* fg );


//--------------------------------------------------------------------------
///
///     Specifies the capture format to use.  Must be one of the
///     VIDEO_PALETTE_* flags.
///
///     \param          fg          Framegrabber handle
///     \param          fmt         pixel format
///
///     \note Currently only RGB32 and RGB24 are properly supported.
///
///     \return     0           Success
///
//--------------------------------------------------------------------------

extern
int fg_set_format( FRAMEGRABBER* fg, int fmt );

//--------------------------------------------------------------------------
///
///     Specifies a sub-window of the input source to capture.  The
///     parameters specify the capture window that is smaller than or
///     equal to the maximum supported window size.
///
///     \param          fg          Framegrabber handle
///     \param          x
///     \param          y
///     \param          width
///     \param          height
///
///  \return         0           Success
///                  -1          Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_capture_window( FRAMEGRABBER* fg,
                           int x, int y, int width, int height );


//--------------------------------------------------------------------------
///
///     Sets the picture brightness to the specified value.
///
///     \param          fg  Framegrabber handle
///     \param          br  Brightness (in percent)
///
///     \return     0           Success
///                  -1          Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_brightness( FRAMEGRABBER* fg, int br );


//--------------------------------------------------------------------------
///
///     Sets the picture hue control to the specified value.
///
///     \param          fg          Framegrabber handle
///     \param          hu          Hue (in percent)
///
///     \return         0           Success
///                    -1           Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_hue( FRAMEGRABBER* fg, int hu );


//--------------------------------------------------------------------------
///
///     Sets the picture colour balance for Queen's English speakers to the
///     specified value.
///
///     \param          fg          Framegrabber handle
///     \param          co          Colour balance (in percent)
///
///     \return         0           Success
///                    -1           Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_colour( FRAMEGRABBER* fg, int co );


//--------------------------------------------------------------------------
///
///     Sets the picture color balance for Americans to the specified value.
///
///     \param          fg          Framegrabber handle
///     \param          co          Color balance (in percent)
///
///     \return      0           Success
///                  -1          Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_color( FRAMEGRABBER* fg, int co );


//--------------------------------------------------------------------------
///
///     Sets the picture contrast to the specified value.
///
///     \param          fg          Framegrabber handle
///     \param          ct          Contrast (in percent)
///
///     \return          0           Success
///                     -1          Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_contrast( FRAMEGRABBER* fg, int ct );


//--------------------------------------------------------------------------
///
///     Sets the picture white balance to the specified value.
///
///     \param          fg          Framegrabber handle
///     \param          wh          Whiteness (in percent)
///
///     \return         0           Success
///                    -1           Failure
///
//--------------------------------------------------------------------------

extern
int fg_set_whiteness( FRAMEGRABBER* fg, int wh );


//--------------------------------------------------------------------------
///
///     Returns a newly allocated frame that is compatible with the current
///     frame grabber settings; that is, the window width and height, and
///     the capture format.  This frame must be deleted by the caller with
///     frame_release().
///
///     \return     A new frame
///
//--------------------------------------------------------------------------

extern
FRAME* fg_new_compatible_frame( FRAMEGRABBER* fg );


//--------------------------------------------------------------------------
///
///     Dumps to the console on stdout all the status information available
///     for the framegrabber.
///
///     \param          fg          Framegrabber handle
///
//--------------------------------------------------------------------------

extern
void fg_dump_info( FRAMEGRABBER* fg );


//==========================================================================

#ifdef __cplusplus__
}
#endif /* __cplusplus__ */

#endif /* __CAPTURE__H_ */
