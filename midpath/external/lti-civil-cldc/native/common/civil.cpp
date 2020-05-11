#include <jni.h>
#include "com_lti_civil_impl_jni_NativeCaptureSystemFactory.h"
#include "com_lti_civil_impl_jni_NativeCaptureSystem.h"
#include "com_lti_civil_impl_jni_NativeCaptureStream.h"
#include "VideoFormat.h"
#include "CaptureSystem.h"
#include "CaptureException.h"
#include "CaptureStream.h"
#include "CaptureSystemFactory.h"
#include "JNICaptureObserver.h"
#include <stdio.h>

using namespace std;

void *getPeerPtr(JNIEnv *pEnv, jobject jObj);
void throwJavaCaptureException(JNIEnv *pEnv, CaptureException *e);
void throwJavaCaptureException(JNIEnv *pEnv, const char *msg, int errorCode);

extern CaptureSystemFactory *gCaptureSystemFactory;	// must be declared by actual impl.

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

static void *jlong2ptr(jlong jl)
{
	
	void *ptr = 0;
	if (sizeof(void *) > sizeof(jlong))
	{	fprintf(stderr, "sizeof(void *) > sizeof(jlong)\n");
		return 0;
		//(* (int *) 0) = 0;	// crash.
	}
	
	memcpy(&ptr, &jl, sizeof(void *));
	return ptr;
}

// NativeCaptureSystemFactory:

JNIEXPORT jobject JNICALL Java_com_lti_civil_impl_jni_NativeCaptureSystemFactory_newCaptureSystemObj
  (JNIEnv *pEnv, jclass)
{
	jclass jNativeCaptureSystemClass = pEnv->FindClass("com/lti/civil/impl/jni/NativeCaptureSystem");
	jmethodID jNativeCaptureSystemConstructor = pEnv->GetMethodID(jNativeCaptureSystemClass, "<init>", "(J)V");
	
	try
	{
		CaptureSystem *pPeer = gCaptureSystemFactory->createCaptureSystem();
		jobject jNativeCaptureSystem = pEnv->NewObject(jNativeCaptureSystemClass, jNativeCaptureSystemConstructor, ptr2jlong(pPeer));
		return jNativeCaptureSystem;
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
		return 0;
	}


}

// NativeCaptureSystem:

JNIEXPORT void JNICALL Java_com_lti_civil_impl_jni_NativeCaptureSystem_dispose
  (JNIEnv *pEnv, jobject jObj)
{
	CaptureSystem *pPeer = (CaptureSystem *) getPeerPtr(pEnv, jObj);
	try
	{
		pPeer->dispose();
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
	}
}

// the following functions are needed because on some (non-windows) platforms, 
// a wchar_t is larger than a jchar.  A jchar is always 2 bytes, and on windows
// a wchar_t is 2 bytes, but on linux a wchar_t is 4 bytes.
static jchar wchar_t_to_jchar(wchar_t wc)
{
	return (jchar) wc;
}
static void wchar_t_to_jchar_array(const wchar_t *src, jchar *dest)
{	int len = wcslen(src);
	for (int i = 0; i <= len; ++i)
	{	dest[i] = wchar_t_to_jchar(src[i]);
	}
}
// caller must delete result.
static jchar *wchar_t_to_jchar_array_alloc(const wchar_t *src)
{	jchar *result = new jchar[wcslen(src) + 1];
	wchar_t_to_jchar_array(src, result);
	return result;
}

static size_t jslen(const jchar *s)
{	size_t result = 0;
	while (*s != 0)
	{	++s;
		++result;
	}
	return result;
}
static wchar_t jchar_to_wchar_t(jchar jc)
{
	return (wchar_t) jc;
}
static void jchar_to_wchar_t_array(const jchar *src, wchar_t *dest)
{	int len = jslen(src);
	for (int i = 0; i <= len; ++i)
	{	dest[i] = jchar_to_wchar_t(src[i]);
	}
}
// caller must delete result.
static wchar_t *jchar_to_wchar_t_array_alloc(const jchar *src)
{	wchar_t *result = new wchar_t[jslen(src) + 1];
	jchar_to_wchar_t_array(src, result);
	return result;
}

JNIEXPORT jobject JNICALL Java_com_lti_civil_impl_jni_NativeCaptureSystem_getCaptureDeviceInfoList
  (JNIEnv *pEnv, jobject jObj)
{
	// get the peer:
	CaptureSystem *pPeer = (CaptureSystem *) getPeerPtr(pEnv, jObj);

	// get the result from the peer:
	list<CaptureDeviceInfo> L;
	try
	{
		pPeer->getCaptureDeviceInfoList(L);
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
	}

	// convert the result to a java list of java objects:
	jclass jListClass = pEnv->FindClass("java/util/ArrayList");
	jmethodID jListConstructor = pEnv->GetMethodID(jListClass, "<init>", "()V");
	jmethodID jListAddMethodID = pEnv->GetMethodID(jListClass, "add", "(Ljava/lang/Object;)Z");
	jobject jList = pEnv->NewObject(jListClass, jListConstructor);
	
	jclass jNativeCaptureDeviceInfoClass = pEnv->FindClass("com/lti/civil/impl/jni/NativeCaptureDeviceInfo");
	jmethodID jNativeCaptureDeviceInfoConstructor = pEnv->GetMethodID(jNativeCaptureDeviceInfoClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");

	list<CaptureDeviceInfo>::iterator i;

	for(i=L.begin(); i != L.end(); ++i) 
	{	
		// TODO: this does not handle unicode.
		const wchar_t *deviceID = (*i).getDeviceID();
		const wchar_t *description = (*i).getDescription();
		
		//wprintf(L"Java_com_lti_civil_impl_jni_NativeCaptureSystem_getCaptureDeviceInfoList deviceID: [%s]\n", deviceID);
		//wprintf(L"Java_com_lti_civil_impl_jni_NativeCaptureSystem_getCaptureDeviceInfoList description: [%s]\n", description);
		
		jchar *jDeviceIDarray = wchar_t_to_jchar_array_alloc(deviceID);
		jchar *jDescriptionarray = wchar_t_to_jchar_array_alloc(description);
		
		//printf("len: %d\n",  wcslen(deviceID));
		//printf("size: %d\n",  sizeof(wchar_t));
		//wprintf(L"%ls\n", L"asdf");
		jstring jDeviceIDString = pEnv->NewString(jDeviceIDarray, wcslen(deviceID));
		jstring jDescriptionString = pEnv->NewString(jDescriptionarray, wcslen(description));

		delete[] jDeviceIDarray;
		delete[] jDescriptionarray;
		
		
		jobject jNativeCaptureDeviceInfo = pEnv->NewObject(jNativeCaptureDeviceInfoClass, jNativeCaptureDeviceInfoConstructor, jDeviceIDString, jDescriptionString);
		
		pEnv->CallBooleanMethod(jList, jListAddMethodID, jNativeCaptureDeviceInfo);
		
	}

	return jList;
}

JNIEXPORT void JNICALL Java_com_lti_civil_impl_jni_NativeCaptureSystem_init
  (JNIEnv *pEnv, jobject jObj)
{
	CaptureSystem *pPeer = (CaptureSystem *) getPeerPtr(pEnv, jObj);
	try
	{
		pPeer->init();
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
	}


}


JNIEXPORT jobject JNICALL Java_com_lti_civil_impl_jni_NativeCaptureSystem_openCaptureDeviceStream
  (JNIEnv *pEnv, jobject jObj, jstring jDeviceIDStr)
{	

	CaptureSystem *pPeer = (CaptureSystem *) getPeerPtr(pEnv, jObj);

	jclass jNativeCaptureStreamClass = pEnv->FindClass("com/lti/civil/impl/jni/NativeCaptureStream");
	jmethodID jNativeCaptureStreamConstructor = pEnv->GetMethodID(jNativeCaptureStreamClass, "<init>", "(J)V");
	

	const jchar *jDeviceIDStrArray = pEnv->GetStringChars(jDeviceIDStr, NULL);
	const wchar_t *szDeviceIDStr = jchar_to_wchar_t_array_alloc(jDeviceIDStrArray);	// TODO: delete
	CaptureStream *pCaptureStreamPeer;
	try
	{
		pCaptureStreamPeer = pPeer->openCaptureDeviceStream(szDeviceIDStr);
		
	}
	catch (CaptureException *e)
	{	
		pEnv->ReleaseStringChars(jDeviceIDStr, jDeviceIDStrArray);
		throwJavaCaptureException(pEnv, e);
	}
	jobject jNativeCaptureStream = pEnv->NewObject(jNativeCaptureStreamClass, jNativeCaptureStreamConstructor, ptr2jlong(pCaptureStreamPeer));
	pEnv->ReleaseStringChars(jDeviceIDStr, jDeviceIDStrArray);
	return jNativeCaptureStream;

}


// NativeCaptureStream:

/*
 * Class:     com_lti_civil_impl_jni_NativeCaptureStream
 * Method:    enumVideoFormats
 * Signature: ()Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_lti_civil_impl_jni_NativeCaptureStream_enumVideoFormats
  (JNIEnv *pEnv, jobject jObj)
{
	CaptureStream *pCaptureStreamPeer = (CaptureStream *) getPeerPtr(pEnv, jObj);

	list<VideoFormat> formatList;

	try
	{
		pCaptureStreamPeer->enumVideoFormats(formatList);
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
	}


	// convert the result to a java list of java objects:
	jclass jListClass = pEnv->FindClass("java/util/ArrayList");
	jmethodID jListConstructor = pEnv->GetMethodID(jListClass, "<init>", "()V");
	jmethodID jListAddMethodID = pEnv->GetMethodID(jListClass, "add", "(Ljava/lang/Object;)Z");
	jobject jList = pEnv->NewObject(jListClass, jListConstructor);
	
	jclass jNativeVideoFormatClass = pEnv->FindClass("com/lti/civil/impl/jni/NativeVideoFormat");
	jmethodID jNativeVideoFormatConstructor = pEnv->GetMethodID(jNativeVideoFormatClass, "<init>", "(JIIIF)V");


	list<VideoFormat>::iterator i;
	for(i=formatList.begin(); i != formatList.end(); ++i) 
	{
		VideoFormat format = *i;
		
		jobject jNativeVideoFormat 
			= pEnv->NewObject(jNativeVideoFormatClass, jNativeVideoFormatConstructor, 
									ptr2jlong(format.handle), 
									format.formatType, 
									format.width, 
									format.height, 
									(jfloat) format.fps);
		pEnv->CallBooleanMethod(jList, jListAddMethodID, jNativeVideoFormat);
				
	}
	
	return jList;
}

/*
 * Class:     com_lti_civil_impl_jni_NativeCaptureStream
 * Method:    getVideoFormat
 * Signature: ()Lcom/lti/civil/VideoFormat;
 */
JNIEXPORT jobject JNICALL Java_com_lti_civil_impl_jni_NativeCaptureStream_getVideoFormat
  (JNIEnv *pEnv, jobject jObj)
{
	CaptureStream *pCaptureStreamPeer = (CaptureStream *) getPeerPtr(pEnv, jObj);

	jclass jNativeVideoFormatClass = pEnv->FindClass("com/lti/civil/impl/jni/NativeVideoFormat");
	jmethodID jNativeVideoFormatConstructor = pEnv->GetMethodID(jNativeVideoFormatClass, "<init>", "(JIIIF)V");

	try
	{
		VideoFormat format = pCaptureStreamPeer->getVideoFormat();
		
		jobject jNativeVideoFormat 
			= pEnv->NewObject(jNativeVideoFormatClass, jNativeVideoFormatConstructor, 
									ptr2jlong(format.handle), 
									format.formatType, 
									format.width, 
									format.height, 
									(jfloat) format.fps);
		return jNativeVideoFormat;
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
		return NULL;
	}

}

/*
 * Class:     com_lti_civil_impl_jni_NativeCaptureStream
 * Method:    setVideoFormat
 * Signature: (Lcom/lti/civil/VideoFormat;)V
 */
JNIEXPORT void JNICALL Java_com_lti_civil_impl_jni_NativeCaptureStream_setVideoFormat
  (JNIEnv *pEnv, jobject jObj, jobject jVideoFormat)
{
	CaptureStream *pCaptureStreamPeer = (CaptureStream *) getPeerPtr(pEnv, jObj);


	// extract values from the VideoFormat object
	jclass jNativeVideoFormatClass = pEnv->FindClass("com/lti/civil/impl/jni/NativeVideoFormat");
	jmethodID jNativeVideoFormatGetOpaqueMethodID = pEnv->GetMethodID(jNativeVideoFormatClass, "getOpaque", "()J");
	jmethodID jNativeVideoFormatGetFormatTypeMethodID = pEnv->GetMethodID(jNativeVideoFormatClass, "getFormatType", "()I");
	jmethodID jNativeVideoFormatGetWidthMethodID = pEnv->GetMethodID(jNativeVideoFormatClass, "getWidth", "()I");
	jmethodID jNativeVideoFormatGetHeightMethodID = pEnv->GetMethodID(jNativeVideoFormatClass, "getHeight", "()I");
	jmethodID jNativeVideoFormatGetFPSMethodID = pEnv->GetMethodID(jNativeVideoFormatClass, "getFPS", "()F");
	jlong result = pEnv->CallLongMethod(jVideoFormat, jNativeVideoFormatGetOpaqueMethodID);
	void *handle = jlong2ptr(result);
	int formatType = pEnv->CallIntMethod(jVideoFormat, jNativeVideoFormatGetFormatTypeMethodID);
	int width = pEnv->CallIntMethod(jVideoFormat, jNativeVideoFormatGetWidthMethodID);
	int height = pEnv->CallIntMethod(jVideoFormat, jNativeVideoFormatGetHeightMethodID);
	float fps = pEnv->CallFloatMethod(jVideoFormat, jNativeVideoFormatGetFPSMethodID);

	VideoFormat videoFormat(handle, formatType, width, height, fps);

	try
	{	
		pCaptureStreamPeer->setVideoFormat(videoFormat);
	}
	catch (CaptureException *e)
	{	
		printf("CaptureException\n");
		throwJavaCaptureException(pEnv, e);
	}	
}
  

JNIEXPORT void JNICALL Java_com_lti_civil_impl_jni_NativeCaptureStream_setObserver
  (JNIEnv *pEnv, jobject jObj, jobject jObserver)
{
	CaptureStream *pCaptureStreamPeer = (CaptureStream *) getPeerPtr(pEnv, jObj);


	if (jObserver != 0)
		pCaptureStreamPeer->setObserver(new JNICaptureObserver(pEnv, pEnv->NewGlobalRef(jObj), pEnv->NewGlobalRef(jObserver)));	// TODO: dispose properly, obj & refs
	else
		pCaptureStreamPeer->setObserver(0);



}

JNIEXPORT void JNICALL Java_com_lti_civil_impl_jni_NativeCaptureStream_nativeStart
  (JNIEnv *pEnv, jobject jObj)
{
	CaptureStream *pPeer = (CaptureStream *) getPeerPtr(pEnv, jObj);
	try
	{
		pPeer->start();
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
	}


}


JNIEXPORT void JNICALL Java_com_lti_civil_impl_jni_NativeCaptureStream_nativeStop
  (JNIEnv *pEnv, jobject jObj)
{
	CaptureStream *pPeer = (CaptureStream *) getPeerPtr(pEnv, jObj);
	try
	{
		printf("Java_com_lti_civil_impl_jni_NativeCaptureStream_stop, stopping...\n"); fflush(stdout);
		pPeer->stop();
		printf("Java_com_lti_civil_impl_jni_NativeCaptureStream_stop, stopped.\n"); fflush(stdout);
	}
	catch (CaptureException *e)
	{	
		printf("CaptureException\n");
		throwJavaCaptureException(pEnv, e);
	}
}

JNIEXPORT void JNICALL Java_com_lti_civil_impl_jni_NativeCaptureStream_nativeDispose
  (JNIEnv *pEnv, jobject jObj)
{
	CaptureStream *pPeer = (CaptureStream *) getPeerPtr(pEnv, jObj);
	try
	{
		pPeer->dispose();
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
	}
}

JNIEXPORT void JNICALL Java_com_lti_civil_impl_jni_NativeCaptureStream_threadMain
  (JNIEnv *pEnv, jobject jObj)
{
	//printf("Java_com_lti_civil_impl_jni_NativeCaptureStream_threadMain %lx\n", (long) pEnv);
	CaptureStream *pPeer = (CaptureStream *) getPeerPtr(pEnv, jObj);
	try
	{
		pPeer->threadMain();
	}
	catch (CaptureException *e)
	{	
		throwJavaCaptureException(pEnv, e);
	}
}  
// Utility functions:

// Get a peer ptr from a Java object which extends Peer.  The peer ptr is the C++ pointer to the C++ peer.
void *getPeerPtr(JNIEnv *pEnv, jobject jObj)
{
	// TODO: we need to make sure the object passed in really is of the right class.	
	jclass jPeerClass = pEnv->FindClass("com/lti/civil/impl/jni/Peered");
	jmethodID jPeerGetPeerPtrMethodID = pEnv->GetMethodID(jPeerClass, "getPeerPtr", "()J");
	jlong result = pEnv->CallLongMethod(jObj, jPeerGetPeerPtrMethodID);
	return jlong2ptr(result);

}


void throwJavaCaptureException(JNIEnv *pEnv, CaptureException *e)
{
	throwJavaCaptureException(pEnv, e->msg, e->errorCode);
}

void throwJavaCaptureException(JNIEnv *pEnv, const char *msg, int errorCode)
{
	jclass jCaptureExceptionClass = pEnv->FindClass("com/lti/civil/CaptureException");
	jmethodID jCaptureExceptionConstructor = pEnv->GetMethodID(jCaptureExceptionClass, "<init>", "(Ljava/lang/String;I)V");
	jstring jMsgString = msg ? pEnv->NewStringUTF(msg) : 0;
	jobject jCaptureException = pEnv->NewObject(jCaptureExceptionClass, jCaptureExceptionConstructor, jMsgString, errorCode);
	
	printf("%s: %d\n", msg, errorCode);

	pEnv->Throw((jthrowable) jCaptureException);
	//pEnv->ThrowNew(jCaptureExceptionClass, msg, errorCode);

	pEnv->DeleteLocalRef(jCaptureExceptionClass);

}


