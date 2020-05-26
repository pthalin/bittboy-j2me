#include "v4l2capture.h"
//#include "frame.h"
#include "VideoFormat.h"
#include "V4L2CaptureSystem.h"
#include "CaptureException.h"
#include "V4L2CaptureStream.h"
#include <string.h>
#include <stdio.h>
#include <errno.h>

static void FailWithException(const char *msg, int errorCode)
{
	throw new CaptureException(msg, errorCode);	
}

void V4L2CaptureSystem::init() /*throws CaptureException*/
{
}

void V4L2CaptureSystem::dispose() /*throws CaptureException*/
{
}



static char wchar_t_to_char(wchar_t wc)
{
	return (char) wc;
}
static void wchar_t_to_char_array(const wchar_t *src, char *dest)
{	int len = wcslen(src);
	for (int i = 0; i <= len; ++i)
	{	dest[i] = wchar_t_to_char(src[i]);
	}
}
// caller must delete result.
static char *wchar_t_to_char_array_alloc(const wchar_t *src)
{	char *result = new char[wcslen(src) + 1];
	memset(result, 0, wcslen(src) + 1);	// just to be sure.  not needed.
	wchar_t_to_char_array(src, result);
	return result;
}

/** @return 1 if the specified path can be opened, 0 if not. */
static int canOpen(const char *path)
{
    int fd = open( path, O_RDONLY );
    if ( fd == -1 )
    	return 0;
    
    int res = close(fd);
    if (res != 0)
    	printf("Unable to close fd: %d\n", errno);
    	
    return 1;
    
}

static int NUM_VIDEO_DEVICE_PATHS = 2;
static const wchar_t * videoDevicePaths[] =
{
	L"/dev/video",
	L"/dev/video0",
	L"/dev/video1",
	L"/dev/video2",
	L"/dev/video3",
	L"/dev/video4",
	L"/dev/video5",
	L"/dev/video6",
	L"/dev/video7",
	L"/dev/video8",
	L"/dev/video9",
	L"/dev/video10",
	L"/dev/video11",
	L"/dev/video12",
	L"/dev/video13",
	L"/dev/video14",
	L"/dev/video15",
};

/** @return List of {@link CaptureDeviceInfo} */
void V4L2CaptureSystem::getCaptureDeviceInfoList(list<CaptureDeviceInfo> &result) /*throws CaptureException*/
{
	// TODO: if one of the devices is busy, it won't open and won't be in the list, even if it is valid.
	for (int i = 0; i < NUM_VIDEO_DEVICE_PATHS; ++i)
	{
		const char *path = wchar_t_to_char_array_alloc(videoDevicePaths[i]);
		const int openResult = canOpen(path);
		//printf("%s %d\n", path, openResult);
		delete path;
		if (openResult)
			result.push_back(CaptureDeviceInfo(videoDevicePaths[i], videoDevicePaths[i]));	// TODO: deal with memory allocation issues.
	}

}




CaptureStream *V4L2CaptureSystem::openCaptureDeviceStream(const wchar_t *deviceId) /*throws CaptureException*/
{	
	const char *deviceId_char_array = wchar_t_to_char_array_alloc(deviceId);
	FRAMEGRABBER2* fg = fg2_createFrameGrabber();	// TODO: delete when closing
	int res = fg2_open( fg, deviceId_char_array );
	if (res != 0)
	{	delete[] deviceId_char_array;
		FailWithException("fg2_open failed", res);
	}
	delete[] deviceId_char_array;

	return new V4L2CaptureStream(fg);	

}

