#include "CaptureSystemFactory.h"

class V4LCaptureSystemFactory : public CaptureSystemFactory
{
public:
	virtual CaptureSystem *createCaptureSystem();// throws CaptureException
};
