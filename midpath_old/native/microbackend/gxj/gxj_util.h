
#ifndef _GXJ_UTIL_H_
#define _GXJ_UTIL_H_

#include <jni.h>
#include <stdlib.h>
#include <stdio.h>

#ifdef __cplusplus
extern "C" {
#endif

enum {
	ALL_OK, OUT_OF_MEMORY
};

typedef int GXJError;

void GXJ_ThrowException (JNIEnv * env, const char *className, const char *errMsg);
void *GXJ_malloc(size_t size);
void GXJ_free(void *p);

#ifdef __cplusplus
}
#endif

#endif /* _GXJ_UTIL_H_ */
