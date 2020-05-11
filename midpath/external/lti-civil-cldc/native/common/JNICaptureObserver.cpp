#include <jni.h>
#include "VideoFormat.h"
#include "JNICaptureObserver.h"
#include "CaptureStream.h"
#include "CaptureException.h"
#include "Image.h"
#include <stdio.h>


static jlong ptr2jlong(void *ptr)
{
	jlong jl = 0;
	if (sizeof(void *) > sizeof(jlong))
	{	fprintf(stderr, "sizeof(void *) > sizeof(jlong)\n");
		return 0;
		//(* (int *) 0) = 0;	// crash.
	}
	
	memcpy(&jl, &ptr, sizeof(void *));
	return jl;
}


JNICaptureObserver::JNICaptureObserver(JNIEnv *pEnv, jobject _jCaptureStreamObj, jobject _jCaptureObserverObj)
{	jint res = pEnv->GetJavaVM(&jvm); 
	if (res < 0) {
        fprintf(stderr, "GetJavaVM failed\n");
        return;
     }
	jCaptureStreamObj = _jCaptureStreamObj;
	jCaptureObserverObj = _jCaptureObserverObj;
    
    /* Find classes used in the on... methods. The classes can't be looked up locally since these methods
     * are called from a system thread and FindClass therefore uses the system class loader to find
     * these classes. This fails if civil is used with Java Web Start, which uses its own class loader.
     * Looking the classes up in the constructor works because the constructor is called from a
     * Java -> Native call and the class loader of the calling Java class is used.
     * We need to create global refs since the class objects are used from a different thread.
     */
    jCaptureObserverClass = (jclass) pEnv->NewGlobalRef(pEnv->FindClass("com/lti/civil/CaptureObserver"));
    jImageClass = (jclass) pEnv->NewGlobalRef(pEnv->FindClass("com/lti/civil/impl/jni/NativeImage"));
    jCaptureExceptionClass = (jclass) pEnv->NewGlobalRef(pEnv->FindClass("com/lti/civil/CaptureException"));
    
#if SINGLE_BUFFER
    jImage = 0;
	jBytes = 0;
	jBytesLen = 0;
#endif
}

JNICaptureObserver::~JNICaptureObserver()
{
    // get a reference to the current JNIEnv
    JNIEnv *pEnv;
    jint res;
    bool attached = false;
    res = jvm->GetEnv((void**)&pEnv, JNI_VERSION_1_2); // TODO: is 1_2 ok, or need to support earlier versions?
    if (res == JNI_EDETACHED) {
        attached = true;
        res = jvm->AttachCurrentThread((void**)&pEnv, NULL);
        if (res < 0) {
            fprintf(stderr, "Attach failed\n");
            return;
        }
    } else if (res < 0) {
        fprintf(stderr, "GetEnv failed\n");
        return;
    }
    
    // release global class references
    pEnv->DeleteGlobalRef(jCaptureObserverClass);
    pEnv->DeleteGlobalRef(jImageClass);
    pEnv->DeleteGlobalRef(jCaptureExceptionClass);
    
    if (attached) {
        jvm->DetachCurrentThread();
    }
}



void JNICaptureObserver::onNewImage(CaptureStream *sender, Image *image)
{

	static bool reentrant = false;
	if (reentrant)
	{	
		fprintf(stderr, "JNICaptureObserver::onNewImage: reentrant==true\n");
		fflush(stderr);
		
	}
	reentrant = true;

#if !SINGLE_BUFFER
	jobject jImage;
	jbyteArray jBytes;
	int jBytesLen = 0;
#endif
	
	JNIEnv *pEnv;
	jint res;
	res = jvm->AttachCurrentThread((void**)&pEnv, NULL);
	if (res < 0) {
		fprintf(stderr, "Attach failed\n");
		reentrant = false;
		return;
	}
	
	
	jmethodID jCaptureObserverOnNewImageMethodID = pEnv->GetMethodID(jCaptureObserverClass, "onNewImage", "(Lcom/lti/civil/CaptureStream;Lcom/lti/civil/Image;)V");
	
	
	// TODO: get the real objects.
//	printf("JNICaptureObserver::onNewImage %lx\n", (long) pEnv);
	jmethodID jImageConstructor = pEnv->GetMethodID(jImageClass, "<init>", "(Lcom/lti/civil/VideoFormat;[B)V");
	
	jclass jNativeVideoFormatClass = pEnv->FindClass("com/lti/civil/impl/jni/NativeVideoFormat");
	jmethodID jNativeVideoFormatConstructor = pEnv->GetMethodID(jNativeVideoFormatClass, "<init>", "(JIIIF)V");
	

	if (image == 0)
	{	
		fprintf(stderr, "JNICaptureObserver::onNewImage: image == 0, skipping.\n");
		fflush(stderr);
		jImage = 0;
		jBytes = 0;
		jvm->DetachCurrentThread();
		reentrant = false;	
		return;		
	}
	else if (image->getLength() <= 0)
	{
		fprintf(stderr, "JNICaptureObserver::onNewImage: image->getLength() <= 0: %d, skipping.\n", image->getLength());
		fflush(stderr);
		jImage = 0;
		jBytes = 0;
		jvm->DetachCurrentThread();
		reentrant = false;	
		return;		
	}
	else
	{
	
		/*const*/ jbyte *imageBytes = (jbyte *) image->getBytes();
	
#if SINGLE_BUFFER	
		if (jBytes == 0 || jBytesLen < image->getLength())
#else
		if (true)
#endif				
		{	jBytesLen = image->getLength();
			// TODO: if jBytes is already allocated, deallocate.
			//fprintf(stdout, "JNICaptureObserver::onNewImage: Allocating %d bytes\n", jBytesLen);
			//fflush(stdout);
			jBytes = pEnv->NewByteArray(jBytesLen);
		}

		if (jBytes == 0)
		{	fprintf(stderr, "JNICaptureObserver::onNewImage: Out of memory: jBytes: %lx jBytesLen: %lx\n", (long) jBytes, (long) jBytesLen);	// we get 0 if we run out of memory.
			fflush(stderr);
			// TODO: jvm->DetachCurrentThread();
			reentrant = false;
			jBytesLen = 0;
			return;	// TODO: throw exception.
		}

		pEnv->SetByteArrayRegion(jBytes, 0, image->getLength(), imageBytes);

#if SINGLE_BUFFER	
		if (jImage == 0)	// TODO: handle size change.
#else
		if (true)
#endif
		{	
			VideoFormat &format = image->getFormat(); 
			// TODO: why needlessly create this object every time?
			jobject jNativeVideoFormat 
				= pEnv->NewObject(jNativeVideoFormatClass, jNativeVideoFormatConstructor, 
									ptr2jlong(format.handle), 
									format.formatType, 
									format.width, 
									format.height, 
									(jfloat) format.fps);
									
			jImage = pEnv->NewObject(jImageClass, jImageConstructor, jNativeVideoFormat, jBytes);
		}
	
#if !SINGLE_BUFFER	
	// TODO: don't allocate and reallocate each time!
		
	// I think we are running out of memory because we sit in a native method the whole time.
	// not sure the garbage collector can collect these.
#endif			
	}

	pEnv->CallVoidMethod(jCaptureObserverObj, jCaptureObserverOnNewImageMethodID, jCaptureStreamObj, jImage);
	
	// just in case the observer throws an exception, we will print it and ignore.  This keeps the JVM from crashing
	// TODO: we should stop the capture.
	jthrowable exc = pEnv->ExceptionOccurred();
	if (exc)
	{
		pEnv->ExceptionDescribe();
        pEnv->ExceptionClear();
	}
	

#if !SINGLE_BUFFER
#if 0	// no need to free, garbage collector will free.
	if (jBytes != 0)
	  	pEnv->ReleaseByteArrayElements(jBytes, imageBytes, 0);
#endif
#endif
	  	
	jvm->DetachCurrentThread();
	reentrant = false;	
}

void JNICaptureObserver::onError(CaptureStream *sender, CaptureException *e)
{
	JNIEnv *pEnv;
	jint res;
	res = jvm->AttachCurrentThread((void**)&pEnv, NULL);
	if (res < 0) {
		fprintf(stderr, "Attach failed\n");
		return;
	}

	jmethodID jCaptureObserverOnErrorMethodID = pEnv->GetMethodID(jCaptureObserverClass, "onError", "(Lcom/lti/civil/CaptureStream;Lcom/lti/civil/CaptureException;)V");

	jmethodID jCaptureExceptionConstructor = pEnv->GetMethodID(jCaptureExceptionClass, "<init>", "(Ljava/lang/String;I)V");
	jstring jMsgString = e->msg ? pEnv->NewStringUTF(e->msg) : 0;
	jobject jCaptureException = pEnv->NewObject(jCaptureExceptionClass, jCaptureExceptionConstructor, jMsgString, e->errorCode);

	pEnv->CallVoidMethod(jCaptureObserverObj, jCaptureObserverOnErrorMethodID, jCaptureStreamObj, jCaptureException);
	
	// just in case the observer throws an exception, we will print it and ignore.  This keeps the JVM from crashing
	// TODO: we should stop the capture.
	jthrowable exc = pEnv->ExceptionOccurred();
	if (exc)
	{
		pEnv->ExceptionDescribe();
        pEnv->ExceptionClear();
	}
	
	jvm->DetachCurrentThread();

}

