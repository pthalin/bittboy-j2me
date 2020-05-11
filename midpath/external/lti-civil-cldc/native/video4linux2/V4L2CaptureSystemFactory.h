#include "CaptureSystemFactory.h"

class V4L2CaptureSystemFactory : public CaptureSystemFactory
{
public:
	virtual CaptureSystem *createCaptureSystem();// throws CaptureException
};
