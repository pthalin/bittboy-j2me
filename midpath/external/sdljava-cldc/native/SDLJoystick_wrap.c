/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.22
 * 
 * This file is not intended to be easily readable and contains a number of 
 * coding conventions designed to improve portability and efficiency. Do not make
 * changes to this file unless you know what you are doing--modify the SWIG 
 * interface file instead. 
 * ----------------------------------------------------------------------------- */


#if defined(__GNUC__)
    typedef long long __int64; /*For gcc on Windows */
#endif
#include <jni.h>
#include <stdlib.h>
#include <string.h>


/* Support for throwing Java exceptions */
typedef enum {
  SWIG_JavaOutOfMemoryError = 1, 
  SWIG_JavaIOException, 
  SWIG_JavaRuntimeException, 
  SWIG_JavaIndexOutOfBoundsException,
  SWIG_JavaArithmeticException,
  SWIG_JavaIllegalArgumentException,
  SWIG_JavaNullPointerException,
  SWIG_JavaDirectorPureVirtual,
  SWIG_JavaUnknownError
} SWIG_JavaExceptionCodes;

typedef struct {
  SWIG_JavaExceptionCodes code;
  const char *java_exception;
} SWIG_JavaExceptions_t;


static void SWIG_JavaThrowException(JNIEnv *jenv, SWIG_JavaExceptionCodes code, const char *msg) {
  jclass excep;
  static const SWIG_JavaExceptions_t java_exceptions[] = {
    { SWIG_JavaOutOfMemoryError, "java/lang/OutOfMemoryError" },
    { SWIG_JavaIOException, "java/io/IOException" },
    { SWIG_JavaRuntimeException, "java/lang/RuntimeException" },
    { SWIG_JavaIndexOutOfBoundsException, "java/lang/IndexOutOfBoundsException" },
    { SWIG_JavaArithmeticException, "java/lang/ArithmeticException" },
    { SWIG_JavaIllegalArgumentException, "java/lang/IllegalArgumentException" },
    { SWIG_JavaNullPointerException, "java/lang/NullPointerException" },
    { SWIG_JavaDirectorPureVirtual, "java/lang/RuntimeException" },
    { SWIG_JavaUnknownError,  "java/lang/UnknownError" },
    { (SWIG_JavaExceptionCodes)0,  "java/lang/UnknownError" } };
  const SWIG_JavaExceptions_t *except_ptr = java_exceptions;

  while (except_ptr->code != code && except_ptr->code)
    except_ptr++;

  (*jenv)->ExceptionClear(jenv);
  excep = (*jenv)->FindClass(jenv, except_ptr->java_exception);
  if (excep)
    (*jenv)->ThrowNew(jenv, excep, msg);
}


/* Contract support */

#define SWIG_contract_assert(nullreturn, expr, msg) if (!(expr)) {SWIG_JavaThrowException(jenv, SWIG_JavaIllegalArgumentException, msg); return nullreturn; } else


  #include "SDL_joystick.h"

extern int SDL_NumJoysticks(void);
extern char const *SDL_JoystickName(int);
extern SDL_Joystick *SDL_JoystickOpen(int);
extern int SDL_JoystickOpened(int);
extern int SDL_JoystickIndex(SDL_Joystick *);
extern int SDL_JoystickNumAxes(SDL_Joystick *);
extern int SDL_JoystickNumBalls(SDL_Joystick *);
extern int SDL_JoystickNumHats(SDL_Joystick *);
extern int SDL_JoystickNumButtons(SDL_Joystick *);
extern void SDL_JoystickUpdate(void);
extern int SDL_JoystickEventState(int);
extern Sint16 SDL_JoystickGetAxis(SDL_Joystick *,int);
extern Uint8 SDL_JoystickGetHat(SDL_Joystick *,int);
extern int SDL_JoystickGetBall(SDL_Joystick *,int,int *,int *);
extern Uint8 SDL_JoystickGetButton(SDL_Joystick *,int);
extern void SDL_JoystickClose(SDL_Joystick *);

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1NumJoysticks(JNIEnv *jenv, jclass jcls) {
    jint jresult = 0 ;
    int result;
    
    (void)jenv;
    (void)jcls;
    result = (int)SDL_NumJoysticks();
    
    jresult = (jint)result; 
    return jresult;
}


JNIEXPORT jstring JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickName(JNIEnv *jenv, jclass jcls, jint jarg1) {
    jstring jresult = 0 ;
    int arg1 ;
    char *result;
    
    (void)jenv;
    (void)jcls;
    arg1 = (int)jarg1; 
    result = (char *)SDL_JoystickName(arg1);
    
    {
        if(result) jresult = (*jenv)->NewStringUTF(jenv, result); 
    }
    return jresult;
}


JNIEXPORT jlong JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickOpen(JNIEnv *jenv, jclass jcls, jint jarg1) {
    jlong jresult = 0 ;
    int arg1 ;
    SDL_Joystick *result;
    
    (void)jenv;
    (void)jcls;
    arg1 = (int)jarg1; 
    result = (SDL_Joystick *)SDL_JoystickOpen(arg1);
    
    *(SDL_Joystick **)&jresult = result; 
    return jresult;
}


JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickOpened(JNIEnv *jenv, jclass jcls, jint jarg1) {
    jint jresult = 0 ;
    int arg1 ;
    int result;
    
    (void)jenv;
    (void)jcls;
    arg1 = (int)jarg1; 
    result = (int)SDL_JoystickOpened(arg1);
    
    jresult = (jint)result; 
    return jresult;
}


JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickIndex(JNIEnv *jenv, jclass jcls, jlong jarg1) {
    jint jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int result;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    result = (int)SDL_JoystickIndex(arg1);
    
    jresult = (jint)result; 
    return jresult;
}


JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickNumAxes(JNIEnv *jenv, jclass jcls, jlong jarg1) {
    jint jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int result;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    result = (int)SDL_JoystickNumAxes(arg1);
    
    jresult = (jint)result; 
    return jresult;
}


JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickNumBalls(JNIEnv *jenv, jclass jcls, jlong jarg1) {
    jint jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int result;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    result = (int)SDL_JoystickNumBalls(arg1);
    
    jresult = (jint)result; 
    return jresult;
}


JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickNumHats(JNIEnv *jenv, jclass jcls, jlong jarg1) {
    jint jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int result;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    result = (int)SDL_JoystickNumHats(arg1);
    
    jresult = (jint)result; 
    return jresult;
}


JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickNumButtons(JNIEnv *jenv, jclass jcls, jlong jarg1) {
    jint jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int result;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    result = (int)SDL_JoystickNumButtons(arg1);
    
    jresult = (jint)result; 
    return jresult;
}


JNIEXPORT void JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickUpdate(JNIEnv *jenv, jclass jcls) {
    (void)jenv;
    (void)jcls;
    SDL_JoystickUpdate();
    
}


JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickEventState(JNIEnv *jenv, jclass jcls, jint jarg1) {
    jint jresult = 0 ;
    int arg1 ;
    int result;
    
    (void)jenv;
    (void)jcls;
    arg1 = (int)jarg1; 
    result = (int)SDL_JoystickEventState(arg1);
    
    jresult = (jint)result; 
    return jresult;
}


JNIEXPORT jshort JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickGetAxis(JNIEnv *jenv, jclass jcls, jlong jarg1, jint jarg2) {
    jshort jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int arg2 ;
    Sint16 result;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    arg2 = (int)jarg2; 
    result = (Sint16)SDL_JoystickGetAxis(arg1,arg2);
    
    jresult = (jshort)result; 
    return jresult;
}


JNIEXPORT jshort JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickGetHat(JNIEnv *jenv, jclass jcls, jlong jarg1, jint jarg2) {
    jshort jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int arg2 ;
    Uint8 result;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    arg2 = (int)jarg2; 
    result = (Uint8)SDL_JoystickGetHat(arg1,arg2);
    
    jresult = (jshort)result; 
    return jresult;
}


JNIEXPORT jint JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickGetBall(JNIEnv *jenv, jclass jcls, jlong jarg1, jint jarg2, jintArray jarg3, jintArray jarg4) {
    jint jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int arg2 ;
    int *arg3 = (int *) 0 ;
    int *arg4 = (int *) 0 ;
    int result;
    int temp3 ;
    int temp4 ;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    arg2 = (int)jarg2; 
    {
        if (!jarg3) {
            SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "array null");
            return 0;
        }
        if ((*jenv)->GetArrayLength(jenv, jarg3) == 0) {
            SWIG_JavaThrowException(jenv, SWIG_JavaIndexOutOfBoundsException, "Array must contain at least 1 element");
            return 0;
        }
        arg3 = &temp3; 
    }
    {
        if (!jarg4) {
            SWIG_JavaThrowException(jenv, SWIG_JavaNullPointerException, "array null");
            return 0;
        }
        if ((*jenv)->GetArrayLength(jenv, jarg4) == 0) {
            SWIG_JavaThrowException(jenv, SWIG_JavaIndexOutOfBoundsException, "Array must contain at least 1 element");
            return 0;
        }
        arg4 = &temp4; 
    }
    result = (int)SDL_JoystickGetBall(arg1,arg2,arg3,arg4);
    
    jresult = (jint)result; 
    {
        jint jvalue = (jint)temp3;
        (*jenv)->SetIntArrayRegion(jenv, jarg3, 0, 1, &jvalue);
    }
    {
        jint jvalue = (jint)temp4;
        (*jenv)->SetIntArrayRegion(jenv, jarg4, 0, 1, &jvalue);
    }
    return jresult;
}


JNIEXPORT jshort JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickGetButton(JNIEnv *jenv, jclass jcls, jlong jarg1, jint jarg2) {
    jshort jresult = 0 ;
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    int arg2 ;
    Uint8 result;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    arg2 = (int)jarg2; 
    result = (Uint8)SDL_JoystickGetButton(arg1,arg2);
    
    jresult = (jshort)result; 
    return jresult;
}


JNIEXPORT void JNICALL Java_sdljava_x_swig_SWIG_1SDLJoystickJNI_SDL_1JoystickClose(JNIEnv *jenv, jclass jcls, jlong jarg1) {
    SDL_Joystick *arg1 = (SDL_Joystick *) 0 ;
    
    (void)jenv;
    (void)jcls;
    arg1 = *(SDL_Joystick **)&jarg1; 
    SDL_JoystickClose(arg1);
    
}


#ifdef __cplusplus
}
#endif

