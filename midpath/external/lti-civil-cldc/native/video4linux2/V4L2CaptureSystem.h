#include "CaptureSystem.h"

class V4L2CaptureSystem : public CaptureSystem
{
public:
	virtual void init() /*throws CaptureException*/;
	virtual void dispose() /*throws CaptureException*/;
	/** @return List of {@link CaptureDeviceInfo} */
	virtual void getCaptureDeviceInfoList(list<CaptureDeviceInfo> &result) /*throws CaptureException*/;
	virtual CaptureStream *openCaptureDeviceStream(const wchar_t *deviceId) /*throws CaptureException*/;
};
