/* src/native/vm/cldc1.1/com_sun_cldc_io_ResourceInputStream.c

   Copyright (C) 2007 R. Grafl, A. Krall, C. Kruegel, C. Oates,
   R. Obermaisser, M. Platter, M. Probst, S. Ring, E. Steiner,
   C. Thalinger, D. Thuernbeck, P. Tomsich, C. Ullrich, J. Wenninger,
   Institut f. Computersprachen - TU Wien

   This file is part of CACAO.

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2, or (at
   your option) any later version.

   This program is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
   02110-1301, USA.

   $Id: java_lang_VMRuntime.c 5900 2006-11-04 17:30:44Z michi $

*/


#include "config.h"
#include "vm/types.h"

#include <assert.h>
#include <dirent.h>
#include <sys/stat.h>
#include <stdlib.h>

#include "arch.h"

#include "mm/memory.h"

#if defined(ENABLE_THREADS)
# include "threads/native/lock.h"
#else
# include "threads/none/lock.h"
#endif

#include "native/jni.h"
#include "native/native.h"

#include "native/include/java_lang_Object.h"
#include "native/include/java_lang_String.h"
#include "native/include/com_sun_cldc_io_ResourceInputStream.h"

#include "vm/builtin.h"
#include "vm/exceptions.h"
#include "vm/initialize.h"
#include "vm/stringlocal.h"
#include "vm/properties.h"
#include "vm/vm.h" /* REMOVE ME: temporarily */

#include "vm/jit/asmpart.h"

#include "vmcore/class.h"
#include "vmcore/classcache.h"
#include "vmcore/linker.h"
#include "vmcore/loader.h"
#include "vmcore/options.h"
#include "vmcore/statistics.h"
#include "vmcore/suck.h"
#include "vmcore/zip.h"

#include "toolbox/list.h"
#include "toolbox/logging.h"
#include "toolbox/util.h"


/* native methods implemented by this file ************************************/
 
static JNINativeMethod methods[] = {
	{ "readAllBytes", "(Ljava/lang/String;[B)I", (void *) (ptrint) &Java_com_sun_cldc_io_ResourceInputStream_readAllBytes },
};
 
 
 
/* _Jv_com_sun_cldc_io_ResourceInputStream_init ********************************
 
   Register native functions.
 
*******************************************************************************/
 
void _Jv_com_sun_cldc_io_ResourceInputStream_init(void)
{
	utf *u;
 
	u = utf_new_char("com/sun/cldc/io/ResourceInputStream");
 
	native_method_register(u, methods, NATIVE_METHODS_COUNT);
}




static jobject *suck_resource(JNIEnv *env, utf *name)
{
	
	list_classpath_entry *lce;
	char                 *filename;
	s4                    filenamelen;
	char                 *path;
	FILE                 *classfile;
	s4                    len;
	struct stat           statBuffer;
	char 				 * dataBuffer;
	int bufferSize = -1;
	jmethodID mid;
	jobject jobj = NULL;
	jclass cls;
	jbyteArray jb;


	/* get the classname as char string (do it here for the warning at
       the end of the function) */

	filenamelen = utf_bytes(name) + strlen("0");
	filename = MNEW(char, filenamelen);

	utf_copy(filename, name);

	/* walk through all classpath entries */

	for (lce = list_first(list_classpath_entries); lce != NULL && bufferSize == -1;
		 lce = list_next(list_classpath_entries, lce)) {

			path = MNEW(char, lce->pathlen + filenamelen);
			strcpy(path, lce->path);
			strcat(path, filename);

			classfile = fopen(path, "r");

			if (classfile) {                                   /* file exists */
				if (!stat(path, &statBuffer)) {            /* read classfile data */
					
					bufferSize = statBuffer.st_size;
					dataBuffer = MNEW(char, bufferSize);

					/* read class data */

					len = fread(dataBuffer, 1, bufferSize, classfile);

					if (len != statBuffer.st_size) {
						MFREE(dataBuffer, char, bufferSize);
						printf("Reading error\n");
						/*suck_stop(cb);*/
/*  						if (ferror(classfile)) { */
/*  						} */
					} else {
  						
  						printf("val: %c%c\n", dataBuffer[0], dataBuffer[1]);
  						
  						
  						
  						/* Sun classes needs an Object to handle infos about the stream
  						 * We use here a ByteArrayInputStream. 
  						 * Drawback: it uses a lot of memory because all data are preloaded
  						 * 
  						 * Sun seems using an internal object to do the same task (i.e created by the VM)
  						 * 
  						 * Code to return a ByteArrayInputStream :
  						 * */
  						 /*jb=(*env)->NewByteArray(env, bufferSize);  
						(*env)->SetByteArrayRegion(env, jb, 0, bufferSize, (jbyte *)dataBuffer);
	
						cls = (*env)->FindClass(env,"java/util/ByteArrayInputStream");
  						if( cls == NULL ) {
    						printf("can't find class java.util.ByteArrayInputStream\n");
    						exceptions_throw_outofmemoryerror();
  						}
  						(*env)->ExceptionClear(env);
  						mid=(*env)->GetMethodID(env, cls, "<init>", "([B)V");
  						jobj=(*env)->NewObject(env, cls, mid, jb);*/
  	
					}

					/* close the class file */

					fclose(classfile);
				}
			}

			MFREE(path, char, lce->pathlen + filenamelen);

	}

	if (opt_verbose)
		if (bufferSize == -1)
			dolog("Warning: Can not open resource file '%s'", filename);

	MFREE(filename, char, filenamelen);

	return jobj;
}

/*
 * Class:     com/sun/cldc/io/ResourceInputStream
 * Method:    open
 * Signature: (Ljava/lang/String;)Ljava/lang/Object;
 */
JNIEXPORT java_lang_Object* JNICALL Java_com_sun_cldc_io_ResourceInputStream_open(JNIEnv *env, jclass clazz, java_lang_String *name)
{
	/**
	 * load_class_bootstrap(utf *name) : src/vmcore/loader.c line 1685
	 * struct classbuffer : src/vmcore/loader.h line 103
	 * 
	 * http://www.javaworld.com/javaworld/javatips/jw-javatip54.html
	 * http://java.sun.com/docs/books/jni/html/jniTOC.html
	 * http://java.sun.com/developer/onlineTraining/Programming/JDCBook/jniref.html#invoke
	 * http://java.sun.com/j2se/1.5.0/docs/guide/jni/spec/jniTOC.html
	 * type: https://java.sun.com/j2se/1.5.0/docs/guide/jni/spec/types.html#wp1064
	 */ 
	
	/*exceptions_throw_outofmemoryerror();
	return NULL;*/
	
	utf *u;
	jbyteArray jb;
	
	u = javastring_toutf(name, false);
	printf("resource name: %s\n", u->text);
	
	if (env == NULL) 
  		printf("env null\n");
	
	jb = suck_resource(env, u);
  	return jb;
	
}

/*
 * Class:     com/sun/cldc/io/ResourceInputStream
 * Method:    readAllBytes
 * Signature: (Ljava/lang/String;[B)I
 */
JNIEXPORT jint JNICALL Java_com_sun_cldc_io_ResourceInputStream_readAllBytes(JNIEnv *env, jclass clazz, java_lang_String *name, java_bytearray *jarray) {

	list_classpath_entry *lce;
	char                 *filename;
	s4                    filenamelen;
	char                 *path;
	FILE                 *classfile;
	s4                    len = -1;
	struct stat           statBuffer;
	int bufferSize = -1;
	utf *uname;
	
	/* get the classname as char string (do it here for the warning at
       the end of the function) */

	uname = javastring_toutf(name, false);
	filenamelen = utf_bytes(uname) + strlen("0");
	filename = MNEW(char, filenamelen);

	utf_copy(filename, uname);

	/* walk through all classpath entries */

	for (lce = list_first(list_classpath_entries); lce != NULL && bufferSize == -1;
		 lce = list_next(list_classpath_entries, lce)) {

			path = MNEW(char, lce->pathlen + filenamelen);
			strcpy(path, lce->path);
			strcat(path, filename);

			classfile = fopen(path, "r");

			if (classfile) {                                   /* file exists */
				if (!stat(path, &statBuffer)) {            /* read classfile data */
					
					bufferSize = statBuffer.st_size;

					/* read class data */

					len = fread(jarray->data, 1, bufferSize, classfile);

					if (len != statBuffer.st_size) {
						printf("Reading error\n");
					} else {
  						/*printf("val: %c%c\n", jarray->data[0], jarray->data[1]);*/
					}

					/* close the class file */

					fclose(classfile);
				}
			}

			MFREE(path, char, lce->pathlen + filenamelen);

	}

	if (opt_verbose)
		if (bufferSize == -1)
			dolog("Warning: Can not open resource file '%s'", filename);

	MFREE(filename, char, filenamelen);

	return len;

}

/*
 * Class:     com_sun_cldc_io_ResourceInputStream
 * Method:    bytesRemain
 * Signature: (Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_sun_cldc_io_ResourceInputStream_bytesRemain(JNIEnv *env, jclass clazz, java_lang_Object jobj) {

}

/*
 * Class:     com_sun_cldc_io_ResourceInputStream
 * Method:    readByte
 * Signature: (Ljava/lang/Object;)I
 */
JNIEXPORT jint JNICALL Java_com_sun_cldc_io_ResourceInputStream_readByte(JNIEnv *env, jclass clazz, java_lang_Object jobj) {

}

/*
 * Class:     com_sun_cldc_io_ResourceInputStream
 * Method:    readBytes
 * Signature: (Ljava/lang/Object;[BII)I
 */
JNIEXPORT jint JNICALL Java_com_sun_cldc_io_ResourceInputStream_readBytes(JNIEnv *env, jclass clazz, java_lang_Object jobj, java_bytearray jarray, s4 off, s4 len) {

}

/*
 * Class:     com_sun_cldc_io_ResourceInputStream
 * Method:    clone
 * Signature: (Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_sun_cldc_io_ResourceInputStream_clone(JNIEnv *env, jclass clazz, java_lang_Object jobj) {

}






/*
 * These are local overrides for various environment variables in Emacs.
 * Please do not remove this and leave it at the end of the file, where
 * Emacs will automagically detect them.
 * ---------------------------------------------------------------------
 * Local variables:
 * mode: c
 * indent-tabs-mode: t
 * c-basic-offset: 4
 * tab-width: 4
 * End:
 * vim:noexpandtab:sw=4:ts=4:
 */
