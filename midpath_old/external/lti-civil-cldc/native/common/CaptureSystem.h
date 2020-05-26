#include <list>
#include "CaptureDeviceInfo.h"

using namespace std;

class CaptureStream;

class CaptureSystem
{
public:
	virtual void init() = 0 /*throws CaptureException*/;
	virtual void dispose() = 0 /*throws CaptureException*/;
	/** @return List of {@link CaptureDeviceInfo} */
	virtual void getCaptureDeviceInfoList(list<CaptureDeviceInfo> &result) = 0 /*throws CaptureException*/;
	virtual CaptureStream *openCaptureDeviceStream(const wchar_t *deviceId) = 0 /*throws CaptureException*/;
};
