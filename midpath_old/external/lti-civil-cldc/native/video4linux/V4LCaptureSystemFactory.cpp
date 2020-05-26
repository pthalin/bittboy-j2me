#include "V4LCaptureSystemFactory.h"
#include "V4LCaptureSystem.h"


CaptureSystemFactory *gCaptureSystemFactory = new V4LCaptureSystemFactory();

CaptureSystem *V4LCaptureSystemFactory::createCaptureSystem()// throws CaptureException
{	return new V4LCaptureSystem();
}
