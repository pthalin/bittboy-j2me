#include "CaptureStream.h"
#include "PThreadStreamThrottle.h"

class V4L2CaptureStream: public CaptureStream
{
private:
	CaptureObserver *observer;
	FRAMEGRABBER2* fg;
	PThreadStreamThrottle streamThrottle;
	
	VideoFormat format;
	int formatTypeV4L2;		// V4L2_PIX_FMT_RGB24, etc.	
	void queryCurrentFormat(); // throws CaptureException;
	// for converting to RGB from non-RGB formats like YUV.
	unsigned char *rgbbuf;	
	int rgbbufsize;
	// TODO: do these right, with some kind of mutex/var:
	volatile bool started;
	volatile bool disposing;
	volatile bool disposed;
public:
	V4L2CaptureStream(
			FRAMEGRABBER2* _fg)// throws CaptureException;
	{	observer = 0;
		fg = _fg;
		rgbbuf = 0;
		rgbbufsize = 0;
		started = false;
		disposing = false;
		disposed = false;
		
		queryCurrentFormat();
	}
	virtual ~V4L2CaptureStream()
	{	
			
	}
	
	virtual void start();// throws CaptureException;
	virtual void stop();// throws CaptureException;
	virtual void threadMain();// throws CaptureException;
	virtual void dispose();// throws CaptureException;
	virtual void setObserver(CaptureObserver *_observer) {observer = _observer;}
	virtual CaptureObserver *getObserver() {return observer;}
	virtual void enumVideoFormats(std::list<VideoFormat> &result); // throws CaptureException;
	virtual void setVideoFormat(VideoFormat &format); // throws CaptureException;
	virtual VideoFormat getVideoFormat(); // throws CaptureException;
};
