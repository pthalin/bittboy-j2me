//==========================================================================
//
//  Project:        libfg - Frame Grabber interface for Linux
//
//  Module:         test_capture
//
//  Description:    Simple test suite to exercise some of the main features
//                  of the libfg framegrabber library.
//
//  Author:         Gavin Baker <gavinb@antonym.org>
//
//  Homepage:       http://www.antonym.org/libfg
//
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

#include <sys/time.h>

#include "capture.h"
#include "frame.h"

//--------------------------------------------------------------------------

// Different picture formats
typedef struct
{
    char*   name;               // Short name of format
    int     format;             // Constant from V4L, VIDEO_PALETTE_*

} FMT;


// Test the supported subset
FMT fmts[] =
{
    { "rgb32",  VIDEO_PALETTE_RGB32  },
    { "rgb24",  VIDEO_PALETTE_RGB24  },
    { "rgb555", VIDEO_PALETTE_RGB555 },
    { "yuv422", VIDEO_PALETTE_YUV422 }
};

//--------------------------------------------------------------------------

// TV tuner channels
typedef struct
{
    char*   name;               // Name of channel
    int     channel;            // Channel number
    float   freq;               // Tuner frequency

} CHANNEL;

// Broadcast Channels in Melbourne, Oz
CHANNEL local[] =
{
    { "ABC",    2,       64.250f    },
    { "Se7en",  7,      182.250f    },
    { "GTV-9",  9,      196.250f    },
    { "Ten",    10,     209.250f    },
    { "SBS",    28,     527.250f    }
};

//--------------------------------------------------------------------------
//  Prototypes for tests
//--------------------------------------------------------------------------

int test_simple_grab( FRAMEGRABBER* fg );
int test_formats( FRAMEGRABBER* fg );
int test_tuner( FRAMEGRABBER* fg );
int test_timing( FRAMEGRABBER* fg );

//--------------------------------------------------------------------------

// Tests
typedef struct
{
    char*   name;
    int (*test_fn)(FRAMEGRABBER*);

} FG_TEST;

FG_TEST tests[] =
{
    { "Simple grab",            test_simple_grab    },
//    { "Supported formats",      test_formats        },
//    { "Tuner",                  test_tuner          },
//    { "Timing",                 test_timing         }
};


//--------------------------------------------------------------------------

// Simple API instrumenting, tests for 0=success
#define _T(f)     {int rc=(f); if (rc!=0) return rc;}

// Simple API instrumenting, tests for NULL=failure
#define _N(f)     {if ((f)==NULL) return -1;}


//--------------------------------------------------------------------------
//  main - control the tests
//--------------------------------------------------------------------------

int main( int argc, char* argv[] )
{
    int i;
    int rc;
    FRAMEGRABBER* fg = NULL;

    printf( "test_capture: libfg testing harness\n$Revision: 1.1 $\n" );

    // Bail if the default device cannot be opened
    if ( ( fg = fg_open( NULL ) ) == NULL )
        return -1;

    printf("open succeeded\n");
    // Dump the fg's state
    fg_dump_info( fg );

    // Run all tests
    for ( i = 0; i < sizeof(tests)/sizeof(FG_TEST); i++ )
    {
        printf( "\n------------------------------------"
                "------------------------------------\n");
        printf( "Executing test: %s\n", tests[i].name );
        printf( "------------------------------------"
                "------------------------------------\n\n");

        rc = tests[i].test_fn( fg );

        if ( rc == 0 )
        {
            printf( "Success.\n" );
        }
        else
        {
            printf( "  *** Failed: rc=%d\n", rc );
        }
    }

    fg_close( fg );

    return 0;
}

//----------------------------------------------------------------------------
//
//  Test:           test_simple_grab
//
//  Description:    Grabs a single frame and saves it to a file.
//
//  Setup:          - Source is TV tuner
//                  - Uses standard window sizing
//                  - Tunes to first local channel
//
//----------------------------------------------------------------------------
int test_simple_grab( FRAMEGRABBER* fg )
{
    FRAME* frame = NULL;

    _T( fg_set_source( fg, FG_SOURCE_TV ) );

    _T( fg_set_capture_window( fg, 0, 0,
                               fg->caps.maxwidth,
                               fg->caps.maxheight ) );

//    _T( fg_set_channel( fg, local[0].freq ) );

//    _T( fg_set_brightness( fg, 75 ) );

//    _T( fg_set_colour( fg, 80 ) );

    _N( frame = fg_grab( fg ) );

    printf( "Saving test frame...\n" );

    _T( frame_save( frame, "test_frame.ppm" ) );

    frame_release( frame );

    return 0;
}

//----------------------------------------------------------------------------
//
//  Test:           test_formats
//
//  Description:    Iterates through each of the supported frame formats,
//                  and attempts to save an image in each of them.
//
//  Setup:          - Source is default
//                  - Channel tuning is default 
//                  - Window sizing is default
//
//----------------------------------------------------------------------------
int test_formats( FRAMEGRABBER* fg )
{
    FRAME* frame = NULL;
    char fname[32];
    int i;

    for ( i = 0; i < sizeof(fmts)/sizeof(FMT); i++ )
    {
        printf( "Saving in %s...\n", fmts[i].name );

        _T( fg_set_format( fg, fmts[i].format ) );
        _N( frame = fg_grab( fg ) );

        snprintf( fname, sizeof(fname), "test_%s.ppm", fmts[i].name );

        _T( frame_save( frame, fname ) );
        frame_release( frame );
    }

    return 0;
}

//----------------------------------------------------------------------------
//
//  Test:           test_tuner
//
//  Description:    Iterates through each of the known local channels,
//                  and attempts to save an image from each of them.
//
//  Setup:          - Source is TV tuner
//                  - Tunes to each local channel sequentially
//                  - Uses standard window sizing
//                  - Saves in RGB24 format
//
//----------------------------------------------------------------------------
int test_tuner( FRAMEGRABBER* fg )
{
    FRAME* frame = NULL;
    char fname[32];
    int i;

    _T( fg_set_format( fg, VIDEO_PALETTE_RGB32 ) );
    _T( fg_set_capture_window( fg, 0, 0, 320, 240 ) );
    _N( frame = fg_new_compatible_frame( fg ) );

    for ( i = 0; i < sizeof(local)/sizeof(CHANNEL); i++ )
    {
        printf( "%u. Saving %s @ %fMHz\n", i, local[i].name, local[i].freq );

        _T( fg_set_channel( fg, local[i].freq ) );

        // Catch up to the new tuning
        _N( fg_grab_frame( fg, frame ) );
        _N( fg_grab_frame( fg, frame ) );

        snprintf( fname, sizeof(fname), "test_%s.ppm", local[i].name );

        _T( frame_save( frame, fname ) );
    }
    frame_release( frame );

    return 0;
}

//----------------------------------------------------------------------------
//
//  Test:           test_timing
//
//  Description:    Grabs a series of frames, and times how long each takes
//                  in a tight loop that also pretends to do some "processing"
//                  (by sleeping).
//
//  Setup:          - Source is default
//                  - Tuned to default
//                  - Uses standard window sizing and format
//
//----------------------------------------------------------------------------
int test_timing( FRAMEGRABBER* fg )
{
    FRAME* frame = NULL;
    struct timeval start_time, end_time;
    int i;

    printf( "Capture timings...\n" );

    _T( fg_set_channel( fg, local[0].freq ) );
    _T( fg_set_format( fg, VIDEO_PALETTE_RGB32 ) );
    _T( fg_set_capture_window( fg, 0, 0, 768, 576 ) );

    _N( frame = frame_new( 768, 576, VIDEO_PALETTE_RGB32 ) );

    for ( i = 0; i < 20; i++ )
    {
        gettimeofday( &start_time, NULL );

        fg_grab_frame( fg, frame );

        // Image crunching step would be here
        usleep(35);

        gettimeofday( &end_time, NULL );

        // This subtraction doesn't handle wrapping
        printf( "Elapsed time = %lu secs %lu usecs\n",
                ( end_time.tv_sec  - start_time.tv_sec  ),
                ( end_time.tv_usec - start_time.tv_usec ) );
    }

    return 0;
}

//==========================================================================
