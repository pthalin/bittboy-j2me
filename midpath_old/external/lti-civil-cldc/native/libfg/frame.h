//==========================================================================
///
///     \file frame.h
///
///     libfg - Frame Grabber interface for Linux
///
///     \brief Frame interface
///
///     Each frame captured by the FRAMEGRABBER returns a FRAME (defined
///     here).  It contains the raw frame data, as well as information about
///     the frame's size and format.
///
///     \author Gavin Baker <gavinb@antonym.org>
///
///     \version $Revision: 1.1 $
///
///
//--------------------------------------------------------------------------
//
//  libfg - Frame Grabber interface for Linux
//  Copyright (c) 2002, 2003 Gavin Baker
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

#ifndef __FRAME_H__
#define __FRAME_H__

#ifdef __cplusplus__
extern "C" {
#endif /* __cplusplus__ */

//--------------------------------------------------------------------------
///
/// \brief A single frame buffer.
///
/// Represents a single image in the output from the frame grabber.  Carries
/// with it the dimensions, format and the data buffer.  The type of the
/// data depends on the format flag (uses the VIDEO_* flags from
/// Video4Linux), so RGB24 would be a triplet of chars, while RGB32 would be
/// an int.
///
//--------------------------------------------------------------------------

typedef struct
{
    int     width;          ///< width in pixels
    int     height;         ///< height in pixels
    int     depth;          ///< bit depth (bits per pixel)
    int     format;         ///< VIDEO_* format
    void*   data;           ///< pointer to data buffer

} FRAME;


//--------------------------------------------------------------------------
///
/// A 24-bit RGB component pixel
///
//--------------------------------------------------------------------------

typedef struct
{
    char    red;
    char    green;
    char    blue;
} FRAME_RGB;


//--------------------------------------------------------------------------
///
/// \brief Create a new frame
///
/// Creates a new frame buffer, of the given dimensions, for the specified
/// pixel format.
///
/// \param  width       Width to allocate (pixels)
/// \param  height      Height to allocate (pixels)
/// \param  format      Pixel format (VIDEO_* flags)
///
/// \return A new allocated frame buffer
///
//--------------------------------------------------------------------------

FRAME* frame_new( int width, int height, int format );


//--------------------------------------------------------------------------
///
/// Releases a frame and all its associated memory.
///
/// \param  fr          The frame to release
///
//--------------------------------------------------------------------------

void frame_release( FRAME* fr );


//--------------------------------------------------------------------------
///
/// Returns a pointer to the raw frame data.
///
/// \param  fr          The frame
///
//--------------------------------------------------------------------------

void* frame_get_data( FRAME* fr );


//--------------------------------------------------------------------------
///
/// Returns the size of the frame, given the dimensions and the pixel format.
///
/// \param  fr          The frame
///
//--------------------------------------------------------------------------

int frame_get_size( FRAME* fr );


//--------------------------------------------------------------------------
///
/// Returns the size of the frame, given the dimensions and the pixel format.
///
/// \param  fr          The frame
///
//--------------------------------------------------------------------------

int frame_get_width( FRAME* fr );


//--------------------------------------------------------------------------
///
/// Returns the size of the frame, given the dimensions and the pixel format.
///
/// \param  fr          The frame
///
//--------------------------------------------------------------------------

int frame_get_height( FRAME* fr );


//--------------------------------------------------------------------------
///
/// Saves the frame to a PNM file for external viewing
///
/// \param          fr          The frame to save
/// \param          filename    The output filename (eg. "capture.pnm")
///
//--------------------------------------------------------------------------

int frame_save( FRAME* fr, const char* filename );
    
//==========================================================================

#ifdef __cplusplus__
}
#endif /* __cplusplus__ */

#endif /*  __FRAME_H__ */
