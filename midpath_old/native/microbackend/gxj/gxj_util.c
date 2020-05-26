
#ifndef _GXJ_UTIL_H_
#define _GXJ_UTIL_H_

#include <jni.h>
#include <stdlib.h>
#include <stdio.h>

#ifdef __cplusplus
extern "C" {
#endif

void GXJ_ThrowException(JNIEnv * env, const char *className, const char *errMsg) {
    jclass excClass;
    if ((*env)->ExceptionOccurred(env)) {
        (*env)->ExceptionClear(env);
    }
    excClass = (*env)->FindClass(env, className);
    if (excClass == NULL) {
        jclass errExcClass;
        errExcClass = (*env)->FindClass(env, "java/lang/ClassNotFoundException");
        if (errExcClass == NULL) {
            errExcClass = (*env)->FindClass(env, "java/lang/InternalError");
            if (errExcClass == NULL) {
                fprintf(stderr, "JCL: Utterly failed to throw exeption ");
                fprintf(stderr, "%s", className);
                fprintf(stderr, " with message ");
                fprintf(stderr, "%s", errMsg);
                return;
            }
        }
        /* Removed this (more comprehensive) error string to avoid the need for
         * a static variable or allocation of a buffer for this message in this
         * (unlikely) error case. --Fridi.
         *
         * sprintf(errstr,"JCL: Failed to throw exception %s with message %s: could not find exception class.", className, errMsg);
         */
        (*env)->ThrowNew(env, errExcClass, className);
    }
    (*env)->ThrowNew(env, excClass, errMsg);
}

void *GXJ_malloc(size_t size) {
    void *mem = malloc(size);
    if (mem == NULL) {
        fprintf(stderr, "OutOfMemoryError");
        return NULL;
    }
    return mem;
}

void GXJ_free(void *p) {
    if (p != NULL) {
        free(p);
    }
}

#ifdef __cplusplus
}
#endif

#endif /* _GXJ_UTIL_H_ */
