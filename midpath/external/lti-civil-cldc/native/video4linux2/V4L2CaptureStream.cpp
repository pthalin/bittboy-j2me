#include "v4l2capture.h"
//#include "frame.h"
#include "VideoFormat.h"
#include "V4L2CaptureStream.h"
#include "CaptureObserver.h"
#include "CaptureException.h"
//#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include "Image.h"
#include "yuv2rgb.h"


static void FailWithException(const char *msg, int errorCode)
{
	throw new CaptureException(msg, errorCode);	
}

// sets format and formatTypeV4L2
void V4L2CaptureStream::queryCurrentFormat() // throws CaptureException;
{
	// from http://www.linuxtv.org/downloads/video4linux/API/V4L2_API/spec-single/v4l2.html
	// To query the current image format applications set the type field of a struct v4l2_format to V4L2_BUF_TYPE_VIDEO_CAPTURE and call the VIDIOC_G_FMT ioctl with a pointer to this structure. Drivers fill the struct v4l2_pix_format pix member of the fmt union.
	
	// query width, height, format
	int width;
	int height;
	
	// TODO: this is in getPixelFormat, which is static.  Need to make an fg2_ function for this.
	{
		struct v4l2_format fmt;
		memset(&fmt, 0, sizeof(fmt));
		fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	
	 	int res = ioctl(fg->fd, VIDIOC_G_FMT, &fmt);
		if (res != 0)
			FailWithException("VIDIOC_G_FMT failed", errno);
	 	
	 	width = fmt.fmt.pix.width;
	 	height = fmt.fmt.pix.height;
	 	formatTypeV4L2 = fmt.fmt.pix.pixelformat;
	}
	
	int formatType;
	
	switch (formatTypeV4L2)
	{
		// TODO: other formats
       case V4L2_PIX_FMT_RGB24:
			formatType = RGB24;
			break;
       case V4L2_PIX_FMT_RGB32:
			formatType = RGB32;
			break;
 		case V4L2_PIX_FMT_YUV420:
	 		formatType = RGB24;
	 		break;
		default:
			FailWithException("unknown or unsupported format", formatTypeV4L2);
	}
	
	
	format = VideoFormat(0, formatType, width, height, FPS_UNKNOWN);	// TODO: FPS
}

void V4L2CaptureStream::enumVideoFormats(std::list<VideoFormat> &result) // throws CaptureException;
{
	// TODO: others 
	result.push_back(format);
}
void V4L2CaptureStream::setVideoFormat(VideoFormat &format) // throws CaptureException;
{
	// TODO
}
VideoFormat V4L2CaptureStream::getVideoFormat() // throws CaptureException;
{
	return format;
}	
void V4L2CaptureStream::start()// throws CaptureException;
{
	//printf("V4L2CaptureStream::start()\n");
	//fflush(stdout);
	
	started = true;
	disposing = false;
	disposed = false;
	
	streamThrottle.start();

    
}
void V4L2CaptureStream::stop()// throws CaptureException;
{	
}

void V4L2CaptureStream::dispose()// throws CaptureException;
{	
	printf("V4L2CaptureStream::dispose()\n");
	//fg_close(fg); // TODO: we have to wait until the thread is done!
	//fg = 0;
	if (started)
	{
		disposing = true;
		printf("V4L2CaptureStream::dispose: waiting for thread to stop\n");
		fflush(stdout);
		while (!disposed)
		{	sleep(1); // TODO: sleep shorter period
		}
		printf("V4L2CaptureStream::dispose: thread stopped\n");
		fflush(stdout);
	}
	else
	{
		// thread not started, dispose ourselves.
		disposing = true;
		fg2_delete(&fg);
		if (rgbbuf != 0)
		{	delete[] rgbbuf;
			rgbbuf = 0;
		}
		disposed = true;
	}
	started = false;
}



//static bool first = true;	// TODO: this is a hack for testing.
void V4L2CaptureStream::threadMain()// throws CaptureException;
{
	int res;

	//if (first)
	{	printf("V4L2CaptureStream::threadMain()\n");
		fflush(stdout);
		streamThrottle.waitUntilStart();
		//first = false;
		printf("V4L2CaptureStream streamThrottle.waitUntilStart completed\n");
		fflush(stdout);
	}




	res = fg2_startCapture(fg);
	if (res != 0)
		FailWithException("fg2_startCapture failed", res);

	while (!disposing)
	{
	struct my_buffer* frame = NULL;

	// TODO: support double-buffering.
	//printf("V4L2CaptureStream::fg2_grab...\n");
	//fflush(stdout);	
    frame = getFrameBuffer( fg );
	//printf("V4L2CaptureStream::fg2_grab: %lx\n", (unsigned long) frame);
	//fflush(stdout);	
    if (frame == 0)
    	FailWithException("getFrameBuffer failed", -1);	// TODO: notify observer instead.


	void *data = frame->start;
	int width = format.getWidth();
	int height = format.getHeight();

	switch (formatTypeV4L2)
	{
		// TODO: other formats
       case V4L2_PIX_FMT_RGB24:
			if (observer != 0)
			{	
				Image image = Image(format, (unsigned char *) data, width * height * 3);
				observer->onNewImage(this, &image);	
			}			
			break;
       case V4L2_PIX_FMT_RGB32:
			if (observer != 0)
			{	
				Image image = Image(format, (unsigned char *) data, width * height * 4);
				observer->onNewImage(this, &image);	
			}			
			break;
 		case V4L2_PIX_FMT_YUV420:
	 		{	if (rgbbufsize == 0)
	 			{	rgbbufsize = width * height * 3;
	 				rgbbuf = new unsigned char[rgbbufsize];
	 			}
				yuv2rgb_buf((unsigned char *) data, width, height, rgbbuf);
				if (observer != 0)
				{	
					Image image = Image(format, rgbbuf, rgbbufsize);
					observer->onNewImage(this, &image);	
				}			
				
			}
			break;
		default:
			FailWithException("unknown or unsupported format", formatTypeV4L2);
	}
	giveBackFrameBuffer(fg, frame);
	}
	res = fg2_stopCapture(fg);
	if (res != 0)
		FailWithException("fg2_stopCapture failed", res);

	fg2_delete(&fg);
	if (rgbbuf != 0)
	{	delete[] rgbbuf;
		rgbbuf = 0;
	}
	disposed = true;
	

}

