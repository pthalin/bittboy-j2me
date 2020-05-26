//==========================================================================
//
//  camview
//
//  Copyright (c) 2003 Gavin Baker <gavinb@antonym.org>
//
//  Released under the Artistic license
//
//  A simple program to display the camera output on screen.
//  Uses libSDL in concert with libfg.
//
//==========================================================================

#include <stdio.h>
#include <sys/time.h>

#include <SDL/SDL.h>

#include "capture.h"

//--------------------------------------------------------------------------

typedef struct
{
    FRAMEGRABBER*   fg;
    FRAME*          frame;
    SDL_Surface*    screen;
    SDL_Surface*    image;
    unsigned        frames;
    struct timeval  timestamp;

} camview_app;

//--------------------------------------------------------------------------

int init( camview_app* app )
{
    unsigned int bmask = 0x000000ff;
    unsigned int gmask = 0x0000ff00;
    unsigned int rmask = 0x00ff0000;
    unsigned int amask = 0xff000000;

    // Open framegrabber device

    app->fg = fg_open( NULL );
    app->frame = fg_new_compatible_frame( app->fg );
    app->frames = 0;

    fg_dump_info( app->fg );

    int bpp = 0;
    switch ( app->frame->format )
    {
        case VIDEO_PALETTE_RGB24:
            bpp = 24;
            break;

        case VIDEO_PALETTE_RGB32:
            bpp = 32;
            break;

        default:
            fprintf( stderr, "Unsupported frame format! %u",
                     app->frame->format );
            return -1;
    }

    // Start SDL

    if ( SDL_Init( SDL_INIT_VIDEO ) < 0 )
    {
        fprintf( stderr, "Failed to initialise SDL: %s\n", SDL_GetError() );
        return -1;
    }
    atexit( SDL_Quit );

    // Create a window the size of the display

    SDL_WM_SetCaption( "camview", NULL );

    app->screen = SDL_SetVideoMode( app->fg->caps.maxwidth,
                                    app->fg->caps.maxheight,
                                    bpp, SDL_SWSURFACE );

    if ( app->screen == NULL )
    {
        fprintf( stderr, "Failed to set video mode: %s\n", SDL_GetError() );
        return -1;
    }

    // Wrap the libfg frame in an SDL surface

    app->image = SDL_CreateRGBSurfaceFrom( app->frame->data,
                                           app->frame->width,
                                           app->frame->height,
                                           bpp,
                                           app->frame->width * 4,
                                           rmask, gmask, bmask, amask);

    if ( app->image == NULL )
    {
        fprintf( stderr, "Failed to create RGB surface! %s\n", SDL_GetError());
        return 1;
    }

    // Ignore alpha channel, go opaque
    SDL_SetAlpha( app->image, 0, 0 );

    gettimeofday( &app->timestamp, NULL );

    return 0;
}

//--------------------------------------------------------------------------

int cleanup( camview_app* app )
{
    fg_close( app->fg );
    frame_release( app->frame );

    SDL_FreeSurface( app->image );

    SDL_Quit();

    return 0;
}

//--------------------------------------------------------------------------

int update( camview_app* app )
{
    fg_grab_frame( app->fg, app->frame );
    app->frames++;

    if ( SDL_BlitSurface( app->image, NULL,
                          app->screen, NULL ) < 0 )
    {
        fprintf( stderr, "Cannot blit! %s\n", SDL_GetError() );
        return 1;
    }

    SDL_UpdateRect( app->screen, 0, 0, 0, 0 );

    if ( app->frames % 10 == 0 )
    {
        struct timeval now;
        gettimeofday( &now, NULL );

        float elapsed = ( now.tv_sec - app->timestamp.tv_sec ) +
            ( now.tv_usec - app->timestamp.tv_usec )/1e6;

        printf( "%0.2f frames/sec    \n", app->frames/elapsed );
    }

    return 0;
}

//--------------------------------------------------------------------------

int run( camview_app* app )
{
    SDL_Event event;
    int running = 1;

    while( running )
    {
        while ( SDL_PollEvent(&event) )
        {
            if ( event.type == SDL_QUIT )
            {
                running = 0;
            }

            if ( event.type == SDL_KEYDOWN )
            {
                if ( event.key.keysym.sym == SDLK_ESCAPE )
                {
                    running = 0;
                }
            }
        }

        update( app );
    }

    return 0;
}

//--------------------------------------------------------------------------

int main( int argc, char* argv[] )
{
    camview_app app;

    printf( "camview\n" );

    if ( init( &app ) < 0 )
        return 1;

    int rc = run( &app );

    cleanup( &app );

    printf( "\n\ndone\n" );

    return rc;
}

//--------------------------------------------------------------------------
