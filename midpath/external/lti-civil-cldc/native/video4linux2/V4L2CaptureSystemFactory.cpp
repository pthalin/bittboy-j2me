#include "VideoFormat.h"
#include "V4L2CaptureSystemFactory.h"
#include "V4L2CaptureSystem.h"


CaptureSystemFactory *gCaptureSystemFactory = new V4L2CaptureSystemFactory(); // TODO

CaptureSystem *V4L2CaptureSystemFactory::createCaptureSystem()// throws CaptureException
{	return new V4L2CaptureSystem();
}
