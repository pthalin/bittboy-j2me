#include <list>

class CaptureObserver;

class CaptureStream
{
public:
	virtual void start() = 0;// throws CaptureException;
	virtual void stop() = 0;// throws CaptureException;
	virtual void dispose() = 0;// throws CaptureException;
	virtual void setObserver(CaptureObserver *observer) = 0;
	virtual CaptureObserver *getObserver() = 0;
	virtual void enumVideoFormats(std::list<VideoFormat> &result) = 0; // throws CaptureException;
	virtual void setVideoFormat(VideoFormat &format) = 0; // throws CaptureException;
	virtual VideoFormat getVideoFormat() = 0; // throws CaptureException;
	CaptureStream(){};
	virtual ~CaptureStream(){};
	
	// should terminate when dispose is called:
	virtual void threadMain() = 0;// throws CaptureException;
};
