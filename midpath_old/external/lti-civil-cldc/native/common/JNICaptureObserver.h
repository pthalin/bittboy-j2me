#include "CaptureObserver.h"

class CaptureStream;

// TODO: reconcile these two.
// 
#ifdef LINUX
#define SINGLE_BUFFER	1
#else
#define SINGLE_BUFFER	0
#endif

class JNICaptureObserver : public CaptureObserver
{
private:
	JavaVM *jvm; /* The virtual machine instance */
	jobject jCaptureStreamObj;	// must be a global reference
	jobject jCaptureObserverObj;	// must be a global reference
    jclass jCaptureObserverClass;
    jclass jImageClass;
    jclass jCaptureExceptionClass;
    
#if SINGLE_BUFFER    
    // single buffer, if used
    jobject jImage;
	jbyteArray jBytes;
	int jBytesLen;
#endif
public:
	JNICaptureObserver(JNIEnv *_pEnv, jobject _jCaptureStreamObj, jobject _jCaptureObserverObj);
    virtual ~JNICaptureObserver();
	virtual void onNewImage(CaptureStream *sender, Image* image);
	virtual void onError(CaptureStream *sender, CaptureException *e);
};
