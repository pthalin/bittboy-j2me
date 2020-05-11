#include "capture.h"
#include "frame.h"
#include "VideoFormat.h"
#include "V4LCaptureStream.h"
#include "CaptureObserver.h"
#include "CaptureException.h"
//#include <stdio.h>
#include "Image.h"
#include "yuv2rgb.h"


static void FailWithException(const char *msg, int errorCode)
{
	throw new CaptureException(msg, errorCode);	
}


void V4LCaptureStream::start()// throws CaptureException;
{
	//printf("V4LCaptureStream::start()\n");
	//fflush(stdout);
	
	disposing = false;
	disposed = false;
	
	streamThrottle.start();

    
}
void V4LCaptureStream::stop()// throws CaptureException;
{	
}

void V4LCaptureStream::dispose()// throws CaptureException;
{	
	printf("V4LCaptureStream::dispose()\n");
	//fg_close(fg); // TODO: we have to wait until the thread is done!
	//fg = 0;
	disposing = true;
	printf("V4LCaptureStream::dispose: waiting for thread to stop\n");
	fflush(stdout);
	while (!disposed)
	{	sleep(1); // TODO: sleep shorter period
	}
	printf("V4LCaptureStream::dispose: thread stopped\n");
	fflush(stdout);
	
}



//static bool first = true;	// TODO: this is a hack for testing.
void V4LCaptureStream::threadMain()// throws CaptureException;
{
	//if (first)
	{	printf("V4LCaptureStream::threadMain()\n");
		fflush(stdout);
		streamThrottle.waitUntilStart();
		//first = false;
		printf("V4LCaptureStream streamThrottle.waitUntilStart completed\n");
		fflush(stdout);
	}

	while (!disposing)
	{
	FRAME* frame = NULL;

	// TODO: support double-buffering.
	//printf("V4LCaptureStream::fg_grab...\n");
	//fflush(stdout);	
    frame = fg_grab( fg );
	//printf("V4LCaptureStream::fg_grab: %lx\n", (unsigned long) frame);
	//fflush(stdout);	
    if (frame == 0)
    	FailWithException("fg_grab failed", -1);	// TODO: notify observer instead.

	switch (frame->format)
	{
		// TODO: other formats
       case VIDEO_PALETTE_RGB24:
			if (observer != 0)
			{	
				Image image = Image(RGB24, frame->width, frame->height, (unsigned char *) frame->data, frame->width * frame->height * 3);
				observer->onNewImage(this, &image);	
			}			
			break;
       case VIDEO_PALETTE_RGB32:
			if (observer != 0)
			{	
				Image image = Image(RGB32, frame->width, frame->height, (unsigned char *) frame->data, frame->width * frame->height * 4);
				observer->onNewImage(this, &image);	
			}			
			break;
 		case VIDEO_PALETTE_YUV420P:
	 		{	if (rgbbufsize == 0)
	 			{	rgbbufsize = frame->width * frame->height * 3;
	 				rgbbuf = new unsigned char[rgbbufsize];
	 			}
				yuv2rgb_buf((unsigned char *) frame->data, frame->width, frame->height, rgbbuf);
				if (observer != 0)
				{	
					Image image = Image(RGB24, frame->width, frame->height, rgbbuf, rgbbufsize);
					observer->onNewImage(this, &image);	
				}			
				
			}
			break;
		default:
			FailWithException("unknown or unsupported format", frame->format);
	}
	frame_release(frame);
	}
	fg_close(fg);
	disposed = true;	// TODO: need some kind of finally...

}

