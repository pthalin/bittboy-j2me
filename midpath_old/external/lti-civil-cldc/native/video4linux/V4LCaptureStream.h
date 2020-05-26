#include "CaptureStream.h"
#include "PThreadStreamThrottle.h"

class V4LCaptureStream: public CaptureStream
{
private:
	CaptureObserver *observer;
	FRAMEGRABBER* fg;
	PThreadStreamThrottle streamThrottle;
	
	// for converting to RGB from non-RGB formats like YUV.
	unsigned char *rgbbuf;	
	int rgbbufsize;
	// TODO: do these right, with some kind of mutex/var:
	volatile bool disposing;
	volatile bool disposed;
public:
	V4LCaptureStream(
			FRAMEGRABBER* _fg)
	{	observer = 0;
		fg = _fg;
		rgbbuf = 0;
		rgbbufsize = 0;
	}
	virtual ~V4LCaptureStream()
	{	if (rgbbuf != 0)
		{	delete[] rgbbuf;
			rgbbuf = 0;
		}
			
	}
	
	virtual void start();// throws CaptureException;
	virtual void stop();// throws CaptureException;
	virtual void threadMain();// throws CaptureException;
	virtual void dispose();// throws CaptureException;
	virtual void setObserver(CaptureObserver *_observer) {observer = _observer;}
	virtual CaptureObserver *getObserver() {return observer;}
};
