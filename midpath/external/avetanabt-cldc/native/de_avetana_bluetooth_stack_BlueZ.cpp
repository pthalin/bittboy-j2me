/*
JBlueZ - Bluetooth Java Interface for Linux Using BlueZ

Copyright for the original JBluez code:
Copyright (c) 2002 The Appliance Studio Limited.
Written by Edward Kay <ed.kay@appliancestudio.com>
http://www.appliancestudio.com

Copyright for the modifications (Service search ..etc..)
Copyright (c) 2004 Avetana GmbH.
Modified by Julien Campana <julien.campana@avetana.de>
http://www.avetana.de

Received Signal Strength (RSSI) and Link Quality code from 
Cristiano di Flora (diflora@unina.it) and the Mobilab Research Group
at the University of Naples (ITALY): www.mobilab.unina.it

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License version 2 as published by the
Free Software Foundation.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS.

IN NO EVENT SHALL THE COPYRIGHT HOLDER(S) AND AUTHOR(S) BE LIABLE FOR ANY
CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION
OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

ALL LIABILITY, INCLUDING LIABILITY FOR INFRINGEMENT OF ANY PATENTS,
COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS, RELATING TO USE OF THIS SOFTWARE IS
DISCLAIMED.

---

This file, bluez.cpp contains the native (C++) code for implementing the Java
native methods defined in BlueZ.java. These are called from Java using the
Java Native Interface (JNI).

The associated header file,  com_appliancestudio_jbluez_BlueZ.h, is generated
by Java using the 'javah' tool. Do not edit
com_appliancestudio_jbluez_BlueZ.h - if you wish to make changes, make them
in BlueZ.java, compile using 'javac', and then use the 'javah' tool. Further
information regarding this process can be found in any good JNI tutorial or
reference, or see the included Makefile.


The purpose of this file is to expose the many functions provided by the
BlueZ libraries to Java. For more information on what each of the functions
do, see the BlueZ documentation (for C) or the associated Javadoc for
BlueZ.java.
*/

/* Standard includes */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

/* JNI includes */
#include <jni.h>
#include "de_avetana_bluetooth_stack_BlueZ.h"

/* Bluetooth includes */
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <sys/poll.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/hci.h>
#include <bluetooth/l2cap.h>
#include <bluetooth/hci_lib.h>
#include <bluetooth/rfcomm.h>
#include <bluetooth/sdp.h>
#include <bluetooth/sdp_lib.h>
#include "sdp_internal.h"
#include <errno.h>

/* These constants and their values are provided by the JSR 82 Java Bluetooth Specification */
#define SERVICE_SEARCH_COMPLETED 0x01
#define SERVICE_SEARCH_TERMINATED 0x02
#define SERVICE_SEARCH_ERROR 0x03
#define SERVICE_SEARCH_NO_RECORDS 0x04
#define SERVICE_SEARCH_DEVICE_NOT_REACHABLE 0x06


#define __HCI_DEVICE_HACK__
#ifdef __HCI_DEVICE_HACK__
/* static device_id for hci_open_dev() */
//#define HCI_DEVICE_ID 0
int HCI_DEVICE_ID = 0;
#endif

#define DEBUG 0


typedef struct {
       uint8_t length;
       unsigned char data[16];
} __attribute__ ((packed)) sdp_cstate_t;

struct search_context {
	char            *svc;           // Service
	uuid_t          group;          // Browse group
	int             tree;           // Display full attribute tree
	uint32_t        handle;         // Service record handle
};

struct ServiceHandle {
	int type;
	int fd;
	int srHandle;
};

/**
 * Julien Campana: The sdp_service_search_attr_req function from BlueZ. Totally modified in order
 * to generate JNI-Java Object and to call the desired methods of the DiscoveryListener.
 */
int my_sdp_service_search_attr_req(JNIEnv* env, jclass jobj, sdp_session_t *session, const sdp_list_t *search,
		sdp_attrreq_type_t reqtype, const sdp_list_t *attrids, sdp_list_t **rsp,
		sdp_list_t *attr_list, jstring addr, jint transID);

int openBTConnection(JNIEnv *env, const char *name, int channel, int type, int master, int auth, int encrypt);

/**
 * Julien Campana: These functions aim to extract the lenght of an attribute (without retrieving
 * the attribute itself).
 */
int extract_attr_len(const char *p, int *size);
int extract_uuid_len(const char *p, int *scanned);
void extract_str_len(const char *p, int *len);
void extract_seq_len(const char *p, int *len);
void extract_int_len(const char *p, int *len);
int find_conn(int s, int dev_id, long arg);


/**
 * Listen for an incomming connection on the given channel.
 * @return
 */
int listenRFCOMM(JNIEnv *env, int channel, int master, int auth, int encrypt, int psm);
int listenL2CAP(JNIEnv *env, int psm, int master, int auth, int encrypt, int omtu, int imtu, int channel);

int list_contains_attr(sdp_list_t *attr_list, uint16_t comp);

/**
 * Julien Campana: Send to the java application the result of the search
 */
void send_searchCompleteEvent(JNIEnv* env, int sentCode, int transID);

/**
 * Julien Campana: Extract a PDU and save it as a ServiceRecord object.
 *
 */
void fill_jobject(JNIEnv *env, jclass obj, const char *buf, int *scanned, sdp_list_t *attr_list, jobject *defRecord);


/**
 * Functions and methods part of the BlueZ code. But not directly accessible (undefined reference during the
 * execution). That's why these functions were directly copied here.
 */
int sdp_extract_seqtype(const char *buf, uint8_t *dtdp, int *size);
static int gen_dataseq_pdu(char *dst, const sdp_list_t *seq, uint8_t dtd);
static int gen_searchseq_pdu(char *dst, const sdp_list_t *seq);
static int gen_attridseq_pdu(char *dst, const sdp_list_t *seq, uint8_t dataType);
int sdp_send_req(sdp_session_t *session, char *buf, int size);
int sdp_read_rsp(sdp_session_t *session, char *buf, int size);
int m_sdp_send_req_w4_rsp(sdp_session_t *session, char *reqbuf, char *rspbuf, int reqsize, int *rspsize);
int sdp_gen_pdu(sdp_buf_t *buf, sdp_data_t *d);
static int get_data_size(sdp_buf_t *buf, sdp_data_t *sdpdata);
int sdp_set_data_type(sdp_buf_t *buf, uint8_t dtd);
void sdp_set_seq_len(char *ptr, int length);

/**
 * Read a local service from the bluetooth local service database and return an instance of LocalServiceRecord.
 * @return
 */
jobject my_sdp_service_attr_req(JNIEnv *env, jclass jobj, sdp_session_t *session, uint32_t handle, sdp_attrreq_type_t reqtype,
		const sdp_list_t *attrids);

/**
 * Function from the BlueZ librairies, which is not accessible (why? If this function is not redefined here,
 * an "Undefined reference error" occurs at runtime).
 * @return
 */
static int gen_dataseq_pdu(char *dst, const sdp_list_t *seq, uint8_t dtd) {
	sdp_data_t *dataseq;
	void **types, **values;
	sdp_buf_t buf;
	int i, seqlen = sdp_list_len(seq);

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	// Fill up the value and the dtd arrays
	if(DEBUG==1)  printf(" ");

	memset(&buf, 0, sizeof(sdp_buf_t));
	buf.data = (uint8_t *)malloc(SDP_UUID_SEQ_SIZE);
	buf.buf_size = SDP_UUID_SEQ_SIZE;

	if(DEBUG==1)  printf("Seq length : %d\n", seqlen);

	types = (void **)malloc(seqlen * sizeof(void *));
	values = (void **)malloc(seqlen * sizeof(void *));
	for (i = 0; i < seqlen; i++) {
		void *data = seq->data;
		types[i] = &dtd;
		if (SDP_IS_UUID(dtd))
			data = &((uuid_t *)data)->value;
		values[i] = data;
		seq = seq->next;
	}

	dataseq = sdp_seq_alloc(types, values, seqlen);
	if(DEBUG==1)  printf("Data Seq : 0x%p\n", seq);
	seqlen = sdp_gen_pdu(&buf, dataseq);
	if(DEBUG==1)  printf("Copying : %d\n", buf.data_size);
	memcpy(dst, buf.data, buf.data_size);

	sdp_data_free(dataseq);

	free(types);
	free(values);
	free(buf.data);
	return seqlen;
}

/**
 * Function from the BlueZ librairies, which is not accessible (why? If this function is not redefined here,
 * an "Undefined reference error" occurs at runtime).
 * @return
 */

static int gen_searchseq_pdu(char *dst, const sdp_list_t *seq) {
	uuid_t *uuid = (uuid_t *)seq->data;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	return gen_dataseq_pdu(dst, seq, uuid->type);
}

/**
 * Function from the BlueZ librairies, which is not accessible (why? If this function is not redefined here,
 * an "Undefined reference error" occurs at runtime).
 * @return
 */

static int gen_attridseq_pdu(char *dst, const sdp_list_t *seq, uint8_t dataType)
{
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return gen_dataseq_pdu(dst, seq, dataType);
}



/**
 * Throws a Java exception if an error occured. Depending on the erro, you will
 * surely have to call the method exit(1) after this excpetion is thrown
 */
void throwException(JNIEnv *env, char *msg)
{
	/* Throw a BlueZException in Java, with the given message */
	jclass exception_cls;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
  
	exception_cls = env->FindClass( "de/avetana/bluetooth/stack/BlueZException");
	if (env->ThrowNew(exception_cls, msg) < 0)
	{
		/* If there was a problem even throwing the exception, something */
		/* big must be wrong. Print out any info available and exit.     */
		printf("** Error throwing BlueZ exception - exiting **\n");
		printf("Message:\n%s\n", msg);
		exit(1);
	}
	return;
}

/**
  Does nothing in linux. Only used in Windows
  */
JNIEXPORT void JNICALL Java_de_avetana_bluetooth_stack_BlueZ_flush (JNIEnv *env, jclass obj, jint fd) {
}

/**
 * Open the local Bluetooth device.
 * The last argument of the method named "hciD" is the number that characterizes
 * the device. This number usually equals "0" if only one bluetooth device is locally
 * connected
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_hciOpenDevice (JNIEnv *env, jclass obj, jint hciID, jobject bluezRef) {
	/* Open the specified HCI device */
	jint dd;

  
#ifdef __HCI_DEVICE_HACK__
	printf("hciOpenDevice(%i)\n", hciID);
	HCI_DEVICE_ID = hciID;
	printf("setting HCI_DEVICE_ID to %i\n", HCI_DEVICE_ID);
	dd = hci_open_dev(HCI_DEVICE_ID);
#else
	dd = hci_open_dev(hciID);
#endif
	if (dd < 0)
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_hciOpenDevice: HCI Device open failed");

	return dd;
}

/**
 * Close the opened device number dd.
 */
JNIEXPORT void JNICALL Java_de_avetana_bluetooth_stack_BlueZ_hciCloseDevice (JNIEnv *env, jclass obj, jint dd) {
	/* Close the specified HCI device */
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	hci_close_dev(dd);
	return;
}

/**
 * Perform an inquiry and return and instance of HCIInquiryResult. The right JSR-82 implementation would be asynchronous:
 * each time a device is discovered, a message is sent to the DiscoveryListener. The following implementation is
 * synchronous (because BlueZ offers a synchronous stack implementation) and looks for all
 * available devices and saves the result into a specific Java-Object. Then and only then, the Listener
 * becomes a notification message.
 */
JNIEXPORT jboolean JNICALL Java_de_avetana_bluetooth_stack_BlueZ_hciInquiry
	(JNIEnv *env, jclass obj, jint dd, jint length, jint max_num_rsp, jlong flags, jobject agent) {
	/* Perform an HCI inquiry - result is returned as a */
	/* Java InquiryInfo object.                         */

	jclass ii_cls, iid_cls;
	jmethodID ii_add_id, iid_con_id;
	jobject info_dev;
	jvalue iid_args[5];

	int num_rsp, i;
	inquiry_info *inq_info = NULL;
	bdaddr_t bdaddr_cpy;

	char *ba_str;
	jstring jba_str;
	
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

#ifdef __HCI_DEVICE_HACK__
//	printf("BlueZ.cpp hciInquiry: changing dd=%i => to dd=%i \n", dd, dd);
	dd = HCI_DEVICE_ID;
#endif

	/* Perform the HCI inquiry */
	num_rsp = hci_inquiry(dd, length, max_num_rsp, NULL, &inq_info, flags);
	if (num_rsp < 0)
	{
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_hciInquiry: Inquiry failed");
		return false;
	}

	inquiry_info *inq_info_bak = inq_info;

	ii_cls = env->FindClass("javax/bluetooth/DiscoveryAgent");
	ii_add_id = env->GetMethodID(ii_cls, "deviceDiscovered", "(Ljavax/bluetooth/RemoteDevice;)V");

	/* For each of the responses, create a new RemoteDevice object */
	/* and add it to the DiscoveryAgent instance.                 */
	iid_cls = env->FindClass("javax/bluetooth/RemoteDevice");
	iid_con_id = env->GetMethodID(iid_cls, "<init>", "(Ljava/lang/String;BBBSI)V");

	for (i=0; i<num_rsp; i++)
	{
		baswap(&bdaddr_cpy, &inq_info->bdaddr);
		ba_str = batostr(&bdaddr_cpy); // Convert from bdaddr_t to char*
		jba_str = env->NewStringUTF(ba_str);
		iid_args[0].l = jba_str;
		iid_args[1].b = (jbyte)  inq_info->pscan_rep_mode;
		iid_args[2].b = (jbyte)  inq_info->pscan_period_mode;
		iid_args[3].b = (jbyte)  inq_info->pscan_mode;
		/*		iid_args[4].s = (jshort)  inq_info->dev_class[0];
				iid_args[5].s = (jshort)  inq_info->dev_class[1];
				iid_args[6].s = (jshort)  inq_info->dev_class[2];*/
		iid_args[4].s = (jshort)    inq_info->clock_offset;
		//		iid_args[5].i = (jint)((inq_info->dev_class[0] & 0xfc) | ((inq_info->dev_class[1] & 0x1f) << 8) | ((inq_info->dev_class[2] & 0x3ff) << 13));
		iid_args[5].i = (jint)((inq_info->dev_class[0] & 0xff) | ((inq_info->dev_class[1] & 0xff) << 8) | ((inq_info->dev_class[2] & 0xff) << 16));
		info_dev = env->NewObjectA(iid_cls, iid_con_id, iid_args);
		env->CallVoidMethod(agent, ii_add_id, info_dev);
		inq_info++;
	}
	free(inq_info_bak);
	return true;
}

/**
 * Provides an estimation of the Link Quality of the connection to
 * another specified bluetooth-device.
 *
 * See HCI_Read_Link_Quality in the Bluetooth Specification for further
 * details of the returned values.
 *
 * @return An estimation of the Link Quality parameter.
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_getLinkQuality
	(JNIEnv *env, jclass obj, jstring bdaddr_jstr) {

	char *bdaddr_str;
	struct hci_conn_info_req *cr;
	struct hci_request rq;
	read_link_quality_rp rp;
	bdaddr_t bdaddr;
	int dev_id;
	int dd;
	jboolean fbol = 1;
	
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	bdaddr_str = (char*) env->GetStringUTFChars(bdaddr_jstr, &fbol);
	baswap(&bdaddr, strtoba(bdaddr_str));
	env->ReleaseStringUTFChars(bdaddr_jstr, bdaddr_str);

	dev_id = hci_for_each_dev(HCI_UP, find_conn, (long) &bdaddr);
	if (dev_id < 0) {
		//No previous connection
		printf( " linkQuaility err: no connection available \n");
		return 0x100;
	}

#ifdef __HCI_DEVICE_HACK__
	dd = hci_open_dev(HCI_DEVICE_ID);
	if (dd < 0) {
		printf( " linkQuaility err: cannot open device hci%d\n", HCI_DEVICE_ID);
		return 0x100;
	}
#else
	dd = hci_open_dev(dev_id);
	if (dd < 0) {
		printf( " linkQuaility err: cannot open device hci%d\n", dev_id);
		return 0x100;
	}
#endif

	cr = (hci_conn_info_req *)malloc(sizeof(*cr) + sizeof(struct hci_conn_info));
	if (!cr)
	{
		printf( " linkQuaility err: memory allocation \n");
		return 0x100;
	}

	bacpy(&cr->bdaddr, &bdaddr);
	cr->type = ACL_LINK;
	if (ioctl(dd, HCIGETCONNINFO, (unsigned long) cr) < 0) {
		printf( " linkQuaility err: get connection info \n");
		return 0x100;
	}

	memset(&rq, 0, sizeof(rq));
	rq.ogf    = OGF_STATUS_PARAM;
	rq.ocf    = OCF_READ_LINK_QUALITY;
	rq.cparam = &cr->conn_info->handle;
	rq.clen   = 2;
	rq.rparam = &rp;
	rq.rlen   = READ_LINK_QUALITY_RP_SIZE;

	if (hci_send_req(dd, &rq, 100) < 0) {
		printf( " linkQuaility err: sending reuquest \n");
		return 0x100;
	}

	if (rp.status) {
		printf( " linkQuaility err: invalid status returned \n");
		return 0x100;
	}

	close(dd);
	free(cr);
	/* printf( " linkQuaility: %d \n", rp.link_quality ); */
	return rp.link_quality;
}



/* HCI Get RSSI*/
/**
 * Provides an estimation of the strength of the signal received
 * from another specified bluetooth-device.
 *
 * See HCI_Get_RSSI in the Bluetooth Specification for further
 * details of the returned values.
 *
 * @return An estimation of the Received Signal Strength Indicator.
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_getRssi
	(JNIEnv *env, jclass cls, jstring bdaddr_jstr) {

	char *bdaddr_str;
	struct hci_conn_info_req *cr;
	struct hci_request rq;
	read_rssi_rp rp;
	bdaddr_t bdaddr;
	int dev_id;
	int dd;
	jboolean fbol = 1;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	bdaddr_str = (char*) env->GetStringUTFChars(bdaddr_jstr, &fbol);
	baswap(&bdaddr, strtoba(bdaddr_str));
	env->ReleaseStringUTFChars(bdaddr_jstr, bdaddr_str);

	//A HCI-connection should have been previously established.

	dev_id = hci_for_each_dev(HCI_UP, find_conn, (long) &bdaddr);
	if (dev_id < 0) {
		//No previous connection
		printf(" rssi err: No connection available\n");
		return 0x100;
	}

#ifdef __HCI_DEVICE_HACK__
	dd = hci_open_dev(HCI_DEVICE_ID);
	if (dd < 0) {
		printf(" rssi err: cannot open hci%d\n",HCI_DEVICE_ID);
		return 0x100;
	};
#else
	dd = hci_open_dev(dev_id);
	if (dd < 0) {
		printf(" rssi err: cannot open hci%d\n",dev_id);
		return 0x100;
	};
#endif

	cr = (hci_conn_info_req *)malloc(sizeof(*cr) + sizeof(struct hci_conn_info));

	if (!cr)
	{
		printf(" rssi err: mem allocation\n");
		return 0x100;
	}

	bacpy(&cr->bdaddr, &bdaddr);
	cr->type = ACL_LINK;
	if (ioctl(dd, HCIGETCONNINFO, (unsigned long) cr) < 0) {
		printf( " rssi err: get connection info\n");
		return 0x100;
	}

	memset(&rq, 0, sizeof(rq));
	rq.ogf    = OGF_STATUS_PARAM;
	rq.ocf    = OCF_READ_RSSI;
	rq.cparam = &cr->conn_info->handle;
	rq.clen   = 2;
	rq.rparam = &rp;
	rq.rlen   = READ_RSSI_RP_SIZE;

	if (hci_send_req(dd, &rq, 100) < 0) {
		printf( " rssi err: send request\n");
		return 0x100;
	}

	if (rp.status) {
		printf( " rssi err: invalid status\n");
		return 0x100;
	}
	close(dd);
	free(cr);
	/* printf( " rssi: strength= %d\n", rp.rssi ); */
	return rp.rssi;
}



/**
 * Returns the BT address of a device.
 * BlueZ identifes the local address with a number. You use this number in the different BlueZ commands
 * as suffix of the name "hci". For example hci0 is the BT device number 0.
 * With this method, you can retrieve the BT address of the local device number hciID
 */
JNIEXPORT jobject JNICALL Java_de_avetana_bluetooth_stack_BlueZ_hciDevBTAddress
	(JNIEnv *env, jclass obj, jint hciID) {

	// Finds the Bluetooth address of the given HCI device,
	// returned as a Java BTAddress object.
	bdaddr_t bdaddr, bdaddr_cpy;
	char *bdaddr_str;
	jclass bda_cls;
	jobject bdaddr_obj;
	jmethodID bda_con_id;
	jstring bdaddr_jstr;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	// Get the Bluetooth address
	if (hci_devba(hciID, &bdaddr) < 0)
	{
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_hciDevBTAddress: Unable to get Bluetooth address for device");
		return 0;
	}

	/* Create a BTAddress object for the Bluetooth device address */
	bda_cls = env->FindClass("de/avetana/bluetooth/util/BTAddress");
	bda_con_id = env->GetMethodID(bda_cls, "<init>", "(Ljava/lang/String;)V");
	baswap(&bdaddr_cpy, &bdaddr);
	bdaddr_str = batostr(&bdaddr_cpy); // Convert from bdaddr_t to char*
	bdaddr_jstr = env->NewStringUTF(bdaddr_str);
	// Convert from char* to jstring
	bdaddr_obj = env->NewObject(bda_cls, bda_con_id, bdaddr_jstr);

	/* Return the BTAddress object */
	return bdaddr_obj;
}

/**
 * Find the HCI device ID for a local device with the given BT address
 * Returns the HCI device ID as an int.
 * Not used and not tested (26/05/2004)
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_hciDeviceID
	(JNIEnv *env, jclass obj, jstring bdaddr_jstr) {
	const char *bdaddr_str;
	jint devID;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	/* Convert Java String Bluetooth address to a char* string */
	jboolean fbol = 1;
	bdaddr_str = env->GetStringUTFChars(bdaddr_jstr, &fbol);

	/* Find the device ID */
	devID = hci_devid(bdaddr_str);
	if (devID < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_hciDeviceID: Unable to get device ID");
		return -1;
	}

	/* Inform the Java VM the native code no longer needs access to bdaddr_str */
	env->ReleaseStringUTFChars(bdaddr_jstr, bdaddr_str);

	return devID;
}


/**
 *  Read the name of a local Bluetooth device, returned as a Java String.
 */
JNIEXPORT jstring JNICALL Java_de_avetana_bluetooth_stack_BlueZ_hciLocalName
	(JNIEnv *env, jclass obj, jint dd, jint timeOut) {

	char name_str[248];
	jstring name_jstr;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if (hci_local_name(dd, sizeof(name_str), name_str, timeOut) < 0)
	{
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_hciLocalName: Unable to read local name");
		return 0;
	}

	/* Convert the name to a Java String */
	name_jstr = env->NewStringUTF(name_str);

	return name_jstr;
}

/**
 * Read the device class of the local device identified by its device number.
 * Under linux, this method requires root priviliges!
 * See description on https://www.bluetooth.org/foundry/assignnumb/document/baseband for correct values
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_getDeviceClass
	(JNIEnv *env, jclass obj, jint dd) {
	uint8_t cls[3];
	//  cls[2]: bits 23 to 16
	//  cls[1]: bits 15 to  8
	//  cls[0]: bits 7  to  0
	// See as
	//  Service: bits 23 to 13
	//  Major:   bits 12 to  8
	//  Minor:   bits  7 to  2
	//  format:  bits  1 and 0
	uint32_t retour;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if( 0 > hci_read_class_of_dev(dd, cls, 1000) ) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_getDeviceClass: Unable to read local device class. Have you the requested permissions?");
		return -1;
	}
	// Wrong:
	//retour=(cls[0] & 0xfc ) + ((cls[1] & 0x1f )<< 8) + ((cls[2] & 0xffe000) << 13);
	// the value read from hci_read_class_of_dev is unformated
	if (DEBUG) printf ("Raw: 23_16  : %x  15_8 : %x  7_0  : %x\n", cls[2], cls[1], cls[0] );

	// The following values are correct:
	uint16_t service = ((cls[2] & 0xff) << 3) + ((cls[1] & 0xe0) >>5);
	uint8_t  major   = (cls[1] & 0x1f);
	uint8_t  minor   = (cls[0] & 0xfc);
	if (DEBUG) printf ("     Service: 0x%.4x  Major: 0x%.2x  Minor: 0x%.2x\n", service, major, minor);

	// This value must be used to get the correct informations 
	retour = (service << 13) | (major << 8) | minor;
	if (DEBUG) printf( "Return: %x\n", retour);
	return (jint)retour;
}
/**
 * Set the device class of the local device identified by its device number.
 * Under linux, this method requires root priviliegs
 * See description on https://www.bluetooth.org/foundry/assignnumb/document/baseband for correct values 
 */
JNIEXPORT jboolean JNICALL Java_de_avetana_bluetooth_stack_BlueZ_setDeviceClass
	(JNIEnv *env, jclass obj, jint dd, jint cls) {

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
		
	if( 0 > hci_write_class_of_dev(dd, cls, 1000) ) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_setDeviceClass: Unable to write local device class. Have you the requested permissions?");
		return false;
	}
	return true;
}

/**
 * Has the user the right to switch the role of the bluetooth devices during a connection?
 * The right implementation would be: looks for the privileges of the user. If the user has
 * root privileges then return 1 else return 0;
 */
JNIEXPORT jboolean JNICALL Java_de_avetana_bluetooth_stack_BlueZ_isMasterSwitchAllowed
	(JNIEnv *env, jclass obj) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return 1;
}

/**
 * Return the maximum amount of connected devices that the implementation can handle.
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_getMaxConnectedDevices
(JNIEnv *env, jclass obj) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return 7;
}

/**
 * Scanning during a connection allowed? The test with BlueZ have shown that this two
 * commands are not compatible. Return therefore 0.
 */
JNIEXPORT jboolean JNICALL Java_de_avetana_bluetooth_stack_BlueZ_inquiryScanAndConAllowed
(JNIEnv *env, jclass obj) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return 0;
}

/**
 * Inquiring during a connection allowed? The test with BlueZ have shown that this two
 * commands are not compatible. Return therefore 0.
 */
JNIEXPORT jboolean JNICALL Java_de_avetana_bluetooth_stack_BlueZ_inquiryAndConAllowed
(JNIEnv *env, jclass obj) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return 0;
}


/**
 * Page scanning during a connection allowed? The test with BlueZ have shown that this two
 * commands are not compatible. Return therefore 0.
 */
JNIEXPORT jboolean JNICALL Java_de_avetana_bluetooth_stack_BlueZ_pageScanAndConAllowed
(JNIEnv *env, jclass obj) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return 0;
}

/**
 * Paging during a connection allowed? The test with BlueZ have shown that this two
 * commands are not compatible. Return therefore 0.
 */
JNIEXPORT jboolean JNICALL Java_de_avetana_bluetooth_stack_BlueZ_pageAndConnAllowed
(JNIEnv *env, jclass obj) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return 0;
}

/**
 * Get the name of the remote device identified by its bluetooth address.
 * Return  it as a java string
 */
JNIEXPORT jstring JNICALL Java_de_avetana_bluetooth_stack_BlueZ_hciRemoteName
	(JNIEnv *env, jclass obj, jint dd, jstring bdaddr_jstr, jint timeOut) {

	bdaddr_t bdaddr;
	const char *bdaddr_str;
	char name_str[248];
	jstring name_jstr;
	int dev_id, dev_dd;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	/* Convert Java String Bluetooth address to a bdaddr_t type */
	jboolean fbol = 1;
	bdaddr_str = env->GetStringUTFChars(bdaddr_jstr, &fbol);
	baswap(&bdaddr, strtoba(bdaddr_str));
	env->ReleaseStringUTFChars(bdaddr_jstr, bdaddr_str);

	dev_id = hci_get_route(&bdaddr);
	if (dev_id < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_hciRemoteName: Remote Device is not available!!");
		return 0;
	}

#ifdef __HCI_DEVICE_HACK__
	dev_dd = hci_open_dev(HCI_DEVICE_ID);
#else
	dev_dd = hci_open_dev(dev_id);
#endif
	if (dev_dd < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_hciRemoteName: Remote Device could not be opened!!");
		return 0;
	}

	if (hci_read_remote_name(dev_dd, &bdaddr, sizeof(name_str), name_str, timeOut ) < 0) {
		close(dev_dd);
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_hciRemoteName: Remote Name could not be read!!");
		return 0;
	}
	close(dev_dd);
	name_jstr = env->NewStringUTF(name_str);
	return name_jstr;
}

/**
 * Open aa RFCOMM connection. Following parameters are used:
 * jstring bdaddr_jstr The BT address of the remote device <br>
 * jint channel The number of the channel <br>
 * jboolean master Is the boolean master or slave for this connection <br>
 * jboolean auth Is authentication requested in order to establish this connection <br>
 * jboolean encrypt Is encryption allowed for this ACL link <br>
 *
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_openRFCommNative
(JNIEnv *env, jclass obj, jstring bdaddr_jstr, jint channel, jboolean master, jboolean auth, jboolean encrypt, jint timeout) {
	jboolean fbol = 1;
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	const char *name = env->GetStringUTFChars(bdaddr_jstr, &fbol);
	return (jint)openBTConnection(env, name, channel, BTPROTO_RFCOMM, (int)master,(int)auth, (int)encrypt);
}

/**
 * Open aa RFCOMM connection. Following parameters are used:
 * jstring bdaddr_jstr The BT address of the remote device <br>
 * jint channel The PSM number <br>
 * jboolean master Is the boolean master or slave for this connection <br>
 * jboolean auth Is authentication requested in order to establish this connection <br>
 * jboolean encrypt Is encryption allowed for this ACL link <br>
 * jint receiveMTU Fix the size of the input MTU<br>
 * jnit transmitMTU Fix the size of the output MTU<br>
 *
 */

JNIEXPORT jobject JNICALL Java_de_avetana_bluetooth_stack_BlueZ_openL2CAPNative
(JNIEnv *env, jclass obj, jstring bdaddr_jstr, jint psm,
 jboolean master, jboolean auth, jboolean encrypt,
 jint receiveMTU, jint transmitMTU, jint timeout) {

	jboolean fbol = 1;
	struct sockaddr_l2 rem_addr, loc_addr;
	bdaddr_t bdaddr;
	struct l2cap_options opts;
	int s;
	socklen_t opt;
	const char *name = env->GetStringUTFChars(bdaddr_jstr, &fbol);
	str2ba(name, &bdaddr);

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	// Create the socket for the link
	if ((s = socket(PF_BLUETOOTH, SOCK_SEQPACKET, BTPROTO_L2CAP)) < 0) {
		printf("Can't create socket. %s(%d)", strerror(errno), errno);
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_openL2CAPNative: Unable to connect to local Bluetooth device!");
		return 0;
	}

	memset(&loc_addr, 0, sizeof(loc_addr));
	loc_addr.l2_family = AF_BLUETOOTH;
#ifdef __HCI_DEVICE_HACK__
	hci_devba(HCI_DEVICE_ID, &loc_addr.l2_bdaddr);
#else
	bacpy(&loc_addr.l2_bdaddr, BDADDR_ANY);
#endif

	// Bind the socket
	if (bind(s, (struct sockaddr *) &loc_addr, sizeof(loc_addr)) < 0) {
		printf("Can't bind socket. %s(%d)", strerror(errno), errno);
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_openL2CAPNative: Unable to establish connection!");
		return 0;
	}

	/* Get default options */
	opt = sizeof(opts);
	if (getsockopt(s, SOL_L2CAP, L2CAP_OPTIONS, &opts, &opt) < 0) {
		printf("Can't get default L2CAP options. %s(%d) \n", strerror(errno), errno);
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_openL2CAPNative: Unable to set connection options!");
		return 0;
	}

	/* Set new options */
	if(transmitMTU!=-1 || receiveMTU!=-1) {
		opts.omtu = (transmitMTU==-1?672:transmitMTU);
		opts.imtu = (receiveMTU==-1?672:receiveMTU);
		if (setsockopt(s, SOL_L2CAP, L2CAP_OPTIONS, &opts, opt) < 0) {
			printf("Can't set L2CAP options. %s(%d)", strerror(errno), errno);
			throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_openL2CAPNative: Unable to set MTU options!");
			return 0;
		}
	}

	/* Set link mode */
	opt = 0;
	if (master) opt |= L2CAP_LM_MASTER;
	if (auth) opt |= L2CAP_LM_AUTH;
	if (encrypt) opt |= L2CAP_LM_ENCRYPT;

	if (setsockopt(s, SOL_L2CAP, L2CAP_LM, &opt, sizeof(opt)) < 0) {
		printf("Can't set L2CAP link mode. %s(%d)", strerror(errno), errno);
	}

	// Set connection parameters
	memset(&rem_addr, 0, sizeof(rem_addr));
	rem_addr.l2_family = AF_BLUETOOTH;
	bacpy(&rem_addr.l2_bdaddr, &bdaddr);
	rem_addr.l2_psm = htobs(psm);

	// Connect with the remote BT device
	if (connect(s, (struct sockaddr *)&rem_addr, sizeof(rem_addr)) < 0 ) {
		printf("Can't connect. %s(%d)", strerror(errno), errno);
		close(s);
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_openL2CAPNative: Unable to establish connection!");
		return NULL;
	}

	opt = sizeof(opts);

	// Get the MTU values (Output and Input) set during the connection.
	if (getsockopt(s, SOL_L2CAP, L2CAP_OPTIONS, &opts, &opt) < 0) {
		printf("Can't get L2CAP options. %s(%d)!!!", strerror(errno), errno);
		close(s);
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_openL2CAPNative: Unable to establish connection!");
		return 0;
	}
	jclass service_cls = env->FindClass("de/avetana/bluetooth/l2cap/L2CAPConnParam");
	jmethodID service_constructor = env->GetMethodID(service_cls, "<init>", "(III)V");
	if(service_constructor==0) {
		printf("Bad service constructor!\n");
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_openL2CAPNative: Unable to construct connection object");
		return 0;
	}
	// R

	jclass blueZ = env->FindClass("de/avetana/bluetooth/stack/BlueZ");
	jmethodID ssmeth = env->GetStaticMethodID(blueZ, "startReaderThread", "(II)V");
	env->CallStaticVoidMethod(blueZ, ssmeth, (jint)s, (jint)opts.imtu);

	jobject rec = env->NewObject(service_cls, service_constructor, s, (jint)opts.imtu, (jint)opts.omtu);
	return rec;
}

/**
 * This functions first aimed to be general enough to handle any kind of connections. But the structures
 * for L2CAP or RFCOMM connections are different. That's why this function now just aims to
 * open RFCOMM connections
 * @return The file ID for this connection
 */
int openBTConnection(JNIEnv *env, const char *name, int channel, int type, int master, int auth, int encrypt) {
	bdaddr_t bdaddr;
	int fd;
	struct sockaddr_rc remote_addr, local_addr;
	int err;
	unsigned char ch = (unsigned char)channel;

	str2ba(name, &bdaddr);

	/* Create the connection */

	if ((fd = socket(PF_BLUETOOTH, SOCK_STREAM, type)) < 0)
		return fd;

	memset(&local_addr, 0, sizeof(local_addr));
	local_addr.rc_family = AF_BLUETOOTH;

#ifdef __HCI_DEVICE_HACK__
	hci_devba(HCI_DEVICE_ID, &local_addr.rc_bdaddr);
#else
	bacpy(&local_addr.rc_bdaddr, BDADDR_ANY);
#endif

	int opt = 0;

	// With RFCOMM sockets, the different security options (Master, authenticate and encrypt), cannot
	// be set. Maybe a future version of BlueZ will implement these socket options.
	if (master) opt |= L2CAP_LM_MASTER;
	if (auth) opt |= L2CAP_LM_AUTH;
	if (encrypt) opt |= L2CAP_LM_ENCRYPT;

	int sockType=-1;
	switch(type) {
		case BTPROTO_RFCOMM:
			sockType=SOL_RFCOMM;
			break;
		case BTPROTO_L2CAP:
			sockType=SOL_L2CAP;
			break;
		default:
			break;
	}
	int sockresult=-1;
	if (opt != 0 && sockType!=-1 && (sockresult=setsockopt(fd, sockType, L2CAP_LM, &opt, sizeof(opt))) < 0) {
		printf("WARNING can't set link Mode (reason: %s)\n\n", strerror(errno));
	}

	if ((err = bind(fd, (struct sockaddr *)&local_addr, sizeof(local_addr))) < 0) {
		close(fd);
		return err;
	}


	memset(&remote_addr, 0, sizeof(remote_addr));
	remote_addr.rc_family = AF_BLUETOOTH;
	bacpy(&remote_addr.rc_bdaddr, &bdaddr);
	remote_addr.rc_channel = ch;
	if ((err = connect(fd, (struct sockaddr *)&remote_addr, sizeof(remote_addr))) < 0) {
		close(fd);
		return err;
	}

	jclass blueZ = env->FindClass("de/avetana/bluetooth/stack/BlueZ");
	jmethodID ssmeth = env->GetStaticMethodID(blueZ, "startReaderThread", "(II)V");
	env->CallStaticVoidMethod(blueZ, ssmeth, (jint)fd, (jint)1000);

	/* Return the handle */
	return fd;
}

/**
 * Close the connection identified by its ID.
 */
JNIEXPORT void JNICALL Java_de_avetana_bluetooth_stack_BlueZ_closeConnection
(JNIEnv *env, jclass obj, jint fd) {

	close (fd);

}

/**
 * Read bytes from an opened connection. The programmer decides the number of byte to be read.
 * With L2CAP connections, the value of "len" is typically 672. The BluetoothStream class try to read 1000
 * bytes each 200 milliseconds.
 */
JNIEXPORT void JNICALL Java_de_avetana_bluetooth_stack_BlueZ_readBytes
(JNIEnv *env, jclass obj, jint fd, jint mtu) {

	int rlen = 0;
	jbyte *c = (jbyte *)malloc (mtu);
	int sel;
	struct pollfd pfd;
	pfd.fd = fd;
	pfd.events = POLLIN | POLLERR | POLLHUP | POLLNVAL;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	jclass cls=env->FindClass("de/avetana/bluetooth/stack/BlueZ");
	jmethodID cclosed=env->GetStaticMethodID(cls,
			"connectionClosed",
			"(I)V");
	jmethodID newData=env->GetStaticMethodID(cls,
			"newData",
			"([BI)I");


	while (true) {
		sel = poll(&pfd, 1, 20);
		
		// ignore if the poll was interrupted
		if (sel == -1 && errno == EINTR)  continue;

			//printf("poll returned %d %X\n", sel, pfd.revents);
		if (sel == -1 || (sel == 1 && ((pfd.revents & 0x30) != 0))) { // Error case
			break;
		} else if(sel > 0) { //Data is available (inc. 0-length packets)
			rlen = read (fd, c, mtu);
			if (rlen == -1) break;
			jbyteArray arr = env->NewByteArray(rlen);
			//printf ("Read %d %d\n", rlen, arr);
			env->SetByteArrayRegion(arr, 0, rlen, (jbyte *)c);
			jint ret = env->CallStaticIntMethod(cls, newData, arr, (int)fd);
			if (ret != 1) break;
		} 
	}

	free (c);
	env->CallStaticVoidMethod(cls, cclosed, (int)fd);

}

/**
 * Write bytes.
 */
JNIEXPORT int JNICALL Java_de_avetana_bluetooth_stack_BlueZ_writeBytes
	(JNIEnv *env, jclass obj, jint fd, jbyteArray b, jint off, jint len) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	jbyte *bytes = env->GetByteArrayElements(b, 0);

	int done = 0;

	while(done < len) {
		int count = write (fd, (char *)(bytes+off+done), len-done);

		if (count < 0) {
			env->ReleaseByteArrayElements(b, bytes, 0);

			env->ThrowNew(env->FindClass("java/io/IOException"), "Failed to write");
			return 0;
		}

		done += count;
	}

	if (len == 0) { 
		int ret = write (fd, "foo", 0);
		printf ("Wrote 0-length packet %d\n", ret);
		if (ret < 0) {
			env->ThrowNew(env->FindClass("java/io/IOException"), "Failed to write");
			return 0;
		}
	}

	env->ReleaseByteArrayElements(b, bytes, 0);
	return 1;
}

/**
 * Update an existing service record. The service record is identified by its service handle. The new
 * service record is given as an arry of bytes.
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_updateService
	(JNIEnv *env, jclass obj, jobject srecord, jlong ser_handle) {

	bdaddr_t interface;
	sdp_session_t *session;
	char *p;
	int status = 0;
	char *reqbuf, *rspbuf;
	int reqsize, rspsize;
	sdp_pdu_hdr_t *reqhdr, *rsphdr;
	uint32_t handle;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	jclass lsrclass=env->GetObjectClass(srecord);
	jmethodID mid=env->GetMethodID(lsrclass, "toByteArray", "()[B");

	if(mid == 0) {
		throwException(env, "de_avetana_bluetooth_stack_BlueZ_createService: Bad method description. Unable to create Service!");
		return 0;
	}

	jbyteArray array = (jbyteArray)env->CallObjectMethod(srecord, mid);

	int length = env->GetArrayLength(array);

	char *data = (char *)env->GetByteArrayElements (array, NULL);

	handle = (uint32_t)((ServiceHandle *)ser_handle)->srHandle;

	if (handle == SDP_SERVER_RECORD_HANDLE) {
		printf ("Service handle is %d\n", (int)handle);
		return -2;
	}


#ifdef __HCI_DEVICE_HACK__
	hci_devba(HCI_DEVICE_ID, &interface);
#else
	bacpy(&interface, BDADDR_ANY);
#endif
	session = sdp_connect(&interface, BDADDR_LOCAL, SDP_RETRY_IF_BUSY);
	if (!session) {
		printf("No local session allowed!!!!");
		return -1;
	}

	reqbuf = (char *)malloc(SDP_REQ_BUFFER_SIZE);
	rspbuf = (char *)malloc(SDP_RSP_BUFFER_SIZE);
	if (!reqbuf || !rspbuf) {
		if(reqbuf) free(reqbuf);
		if(rspbuf) free(rspbuf);
		sdp_close(session);
		return -1;
	}
	reqhdr = (sdp_pdu_hdr_t *)reqbuf;
	reqhdr->pdu_id = SDP_SVC_UPDATE_REQ;
	reqhdr->tid    = htons(sdp_gen_tid(session));

	p = (char *)(reqbuf + sizeof(sdp_pdu_hdr_t));
	reqsize = sizeof(sdp_pdu_hdr_t);

	sdp_put_unaligned(htonl(handle), (uint32_t *)p);
	reqsize += sizeof(uint32_t);
	p += sizeof(uint32_t);

	memcpy(p, data, (int)length);
	reqsize += (int)length;

	reqhdr->plen = htons(reqsize - sizeof(sdp_pdu_hdr_t));
	status = m_sdp_send_req_w4_rsp(session, reqbuf, rspbuf, reqsize, &rspsize);

	if (status == 0) {
		rsphdr = (sdp_pdu_hdr_t *)rspbuf;
		p = rspbuf + sizeof(sdp_pdu_hdr_t);
		status = sdp_get_unaligned((uint16_t *)p);
	}
	sdp_close(session);
	
	return status;
}

/**
 * Create a new Service record an stores it in the local BCC. This functions takes a jobject as parameter.
 * This jobject (a LocalServiceRecord if you are using the Avetana implementation) MUST implement a method
 * toByteArray().
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_createService
	(JNIEnv *env, jclass obj, jobject srecord) {

	bdaddr_t interface;
	sdp_session_t *session;
	char *p;
	int status = 0;
	char *req, *rsp;
	int reqsize, rspsize;
	sdp_pdu_hdr_t *reqhdr, *rsphdr;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	jclass lsrclass=env->GetObjectClass(srecord);
	jmethodID mid=env->GetMethodID(lsrclass,
			"toByteArray",
			"()[B");
	
	jmethodID mid_gcn=env->GetMethodID(lsrclass,
			"getChannelNumber",
			"()I");
	
	jmethodID mid_ucn=env->GetMethodID(lsrclass,
			"updateChannelNumber",
			"(I)V");
	
	jmethodID mid_gpr=env->GetMethodID(lsrclass,
			"getProtocol",
			"()S");

	jint cn = env->CallIntMethod (srecord, mid_gcn);
	jshort prot = env->CallShortMethod (srecord, mid_gpr);
	if (cn == 0) cn = prot == 0 ? 11 : 1;

	ServiceHandle *srh = (ServiceHandle *)malloc (sizeof (ServiceHandle));
	srh->type = prot;
	srh->fd = 0;

	//Prepare the sockets
	int fd;
	
	if (prot == 1 || prot == 2) {
		struct sockaddr_rc local_addr;

		memset(&local_addr, 0, sizeof(local_addr));
		local_addr.rc_family = AF_BLUETOOTH;
#ifdef __HCI_DEVICE_HACK__
		hci_devba(HCI_DEVICE_ID, &local_addr.rc_bdaddr);
#else
		bacpy(&local_addr.rc_bdaddr, BDADDR_ANY);
#endif
		local_addr.rc_channel = (unsigned char)cn;

		//printf("Function called: %s, %i\n"__FILE__, __LINE__);

		if ((fd = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM)) < 0)
			return fd;


		int err = -1;
		while (err == -1 && local_addr.rc_channel < 32) {
			err=bind(fd, (struct sockaddr *)&local_addr, sizeof(local_addr));
			if (err == -1) local_addr.rc_channel++;
			
		} 
		if (err == -1)	{
				throwException (env, "Unable to bind socket");
				close(fd);
				return err;
		}
		cn = (int)local_addr.rc_channel;
		printf ("Channel opened at %d\n", cn);
	} else {
		sockaddr_l2 local_addr;
		memset(&local_addr, 0, sizeof(local_addr));
		local_addr.l2_family = AF_BLUETOOTH;
#ifdef __HCI_DEVICE_HACK__
		hci_devba(HCI_DEVICE_ID, &local_addr.l2_bdaddr);
#else
		bacpy(&local_addr.l2_bdaddr, BDADDR_ANY);
#endif
		local_addr.l2_psm    = htobs(cn);
		
		if ((fd = socket(PF_BLUETOOTH, SOCK_SEQPACKET, BTPROTO_L2CAP)) < 0) {
			printf("Can't create socket. %s(%d)\n", strerror(errno), errno);
			return fd;
		}

		int err = -1;
		while (err == -1 && local_addr.l2_psm < 65535) {
			err=bind(fd, (struct sockaddr *)&local_addr, sizeof(local_addr));
			if (err == -1) local_addr.l2_psm += 2;
			
		} 
		if (err == -1)	{
				throwException (env, "Unable to bind socket");
				close(fd);
				return err;
		}

		cn = (int)local_addr.l2_psm;
	}

	int l = listen(fd, 10);
	if (l != 0) {
		free (srh);
		throwException (env, "Failed to set socket to listen mode\n");
		close (fd);
		return -1;
	}
	srh->fd = fd;

	//Now update the service record to the real channel
	env->CallVoidMethod (srecord, mid_ucn, cn);

	if(mid == 0) {
		throwException(env, "de_avetana_bluetooth_stack_BlueZ_createService: Bad method description. Unable to create Service!");
		return -1;
	}
	jbyteArray array = (jbyteArray)env->CallObjectMethod(srecord, mid);

	int length = env->GetArrayLength(array);
	jboolean fb = true;
	char *data = (char *)env->GetByteArrayElements (array, &fb);

#ifdef __HCI_DEVICE_HACK__
	hci_devba(HCI_DEVICE_ID, &interface);
#else
	bacpy(&interface, BDADDR_ANY);
#endif

	session = sdp_connect(&interface, BDADDR_LOCAL, SDP_RETRY_IF_BUSY);
	if (!session) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_createServiceByte: Local connection!!");
		return -1;
	}

	req = (char *)malloc(SDP_REQ_BUFFER_SIZE);
	rsp = (char *)malloc(SDP_RSP_BUFFER_SIZE);

	if (req == NULL || rsp == NULL) {
		if(req) free(req);
		if(rsp) free(rsp);
		sdp_close(session);
		return -1;
	}

	reqhdr = (sdp_pdu_hdr_t *)req;
	reqhdr->pdu_id = SDP_SVC_REGISTER_REQ;
	reqhdr->tid    = htons(sdp_gen_tid(session));
	reqsize = sizeof(sdp_pdu_hdr_t) + 1;
	p = req + sizeof(sdp_pdu_hdr_t);
	*p++ = SDP_RECORD_PERSIST;
	memcpy(p, data, (int)length);

	reqsize += (int)length;
	reqhdr->plen = htons(reqsize - sizeof(sdp_pdu_hdr_t));

	status = m_sdp_send_req_w4_rsp(session, req, rsp, reqsize, &rspsize);
	if (status < 0) {
		if(req) free(req);
		if(rsp) free(rsp);
		sdp_close(session);
		return -2;
	}
	rsphdr = (sdp_pdu_hdr_t *)rsp;
	p = rsp + sizeof(sdp_pdu_hdr_t);
	if (rsphdr->pdu_id == SDP_SVC_REGISTER_RSP) {
		uint32_t handle  = ntohl(sdp_get_unaligned((uint32_t *)p));
		if(req) free(req);
		if(rsp) free(rsp);
		sdp_close(session);
		
		srh->srHandle = handle;
		
		
	return (jint)srh;
	}
	else return -1;
}

/**
 * Register a service and listen for an incoming RFCOMM connection.
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_registerService
	(JNIEnv *env, jclass obj, jint serviceHandle, jint channel, jboolean master, jboolean auth, jboolean encrypt) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return listenRFCOMM(env, ((ServiceHandle *)serviceHandle)->fd, master, auth, encrypt, channel);
}

/**
 * Function from the BlueZ librairies (see the source code of hcitool).
 * It verifies if the local device number "dev_id" is currently connected.
 */

int find_conn(int s, int dev_id, long arg)
{
	struct hci_conn_list_req *cl;
	struct hci_conn_info *ci;
	int i;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if (!(cl = (hci_conn_list_req *)malloc(10 * sizeof(*ci) + sizeof(*cl)))) {
		return -1;
	}
	cl->dev_id = dev_id;
	cl->conn_num = 10;
	ci = cl->conn_info;

	if (ioctl(s, HCIGETCONNLIST, (void*)cl)) {
		return -1;
	}

	for (i=0; i < cl->conn_num; i++, ci++)
		if (!bacmp((bdaddr_t *)arg, &ci->bdaddr))
			return 1;
	return 0;
}

/**
 * Turn to on/off he encryption state of an EXISTING connection.
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_encrypt
(JNIEnv *env, jclass obj, jint connectionHandle, jstring badr, jboolean enable) {

	jboolean fbol = 1;
	bdaddr_t bdaddr;
	const char *straddr;
	struct hci_conn_info_req *cr;
	struct hci_request rq;
	set_conn_encrypt_cp cp;
	evt_encrypt_change rp;
	int dd, dev_id;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	straddr = env->GetStringUTFChars(badr, &fbol);
	str2ba(straddr, &bdaddr);

	dev_id = hci_for_each_dev(HCI_UP, find_conn, (long) &bdaddr);
	if (dev_id < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_encrypt: Device is not connected!!");
		return -1;
	}

#ifdef __HCI_DEVICE_HACK__
	dd = hci_open_dev(HCI_DEVICE_ID);
#else
	dd = hci_open_dev(dev_id);
#endif
	if (dd < 0) throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_encrypt: Unable to open remote device!!");

	cr = (hci_conn_info_req *)malloc(sizeof(*cr) + sizeof(struct hci_conn_info));
	if (!cr) return -2;

	bacpy(&cr->bdaddr, &bdaddr);
	cr->type = ACL_LINK;
	if (ioctl(dd, HCIGETCONNINFO, (unsigned long) cr) < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_encrypt: Unable to get connection information. The connection may have been closed!!");
		return -1;
	}
	cp.handle = cr->conn_info->handle;
	cp.encrypt = (int)enable;

	memset(&rq, 0, sizeof(rq));
	rq.ogf    = OGF_LINK_CTL;
	rq.ocf    = OCF_SET_CONN_ENCRYPT;
	rq.cparam = &cp;
	rq.clen   = SET_CONN_ENCRYPT_CP_SIZE;
	rq.rparam = &rp;
	rq.rlen   = EVT_ENCRYPT_CHANGE_SIZE;
	rq.event  = EVT_ENCRYPT_CHANGE;

	if (hci_send_req(dd, &rq, 25000) < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_encrypt: Unable to set encryption option (you MUST be root under Linux)!!");
		return 0;
	}

	close(dd);
	free(cr);
	return 1;
}


/**
 * Get and return the state of a connection. Returned value is an arry of boolean containing, in this order, following
 * state flags:
 * - Accept
 * - Master (if true, the local device is master)
 * - Authenticated (if true, the remote device is authenticated)
 * - Encrypted (if true, the ACL link with the remote device is encrypted)
 * - trusted (if true, the remote device is a trusted device)
 */
JNIEXPORT jbooleanArray JNICALL Java_de_avetana_bluetooth_stack_BlueZ_connectionOptions
	(JNIEnv *env, jclass obj, jint handle, jstring deviceAddr) {

	struct hci_conn_list_req *cl;
	struct hci_conn_info *ci;
	int i, dev_id;
	bdaddr_t bdaddr;
	const char *straddr;
	int flags[5]={HCI_LM_ACCEPT, HCI_LM_MASTER, HCI_LM_AUTH, HCI_LM_ENCRYPT, HCI_LM_TRUSTED};
	jboolean fbol=1;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	straddr = env->GetStringUTFChars(deviceAddr, &fbol);
	str2ba(straddr, &bdaddr);

	dev_id = hci_for_each_dev(HCI_UP, find_conn, (long)&bdaddr);
	if (dev_id < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_connectionOptions: Remote Device is not connected!!");
		return 0;
	}

	if (!(cl = (hci_conn_list_req*)malloc(10 * sizeof(*ci) + sizeof(*cl)))) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_connectionOptions: Unable to allocate enough memory!!!");
		return 0;
	}

	int s = socket(AF_BLUETOOTH, SOCK_RAW, BTPROTO_HCI);
	if (s < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_connectionOptions: Unable to open the socket!!");
		return 0;
	}

	cl->dev_id = dev_id;
	cl->conn_num = 10;
	ci = cl->conn_info;

	if (ioctl(s, HCIGETCONNLIST, (void*)cl)) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_connectionOptions: Unable to load connection list!!!");
		return 0;
	}

	for (i=0; i < cl->conn_num; i++, ci++) {
		char addr[18];
		ba2str(&ci->bdaddr, addr);
		int comp=bacmp(&bdaddr, &ci->bdaddr);
		if(comp == 0 && (ci->type == ACL_LINK)) {
			jbooleanArray myArray=env->NewBooleanArray(5);
			jboolean* buf = (jboolean*)malloc(5 * sizeof(jboolean));
			for(int u=0;u<5;u++) {
				buf[u]=(jboolean)((ci->link_mode & flags[u])>0);
			}
			env->SetBooleanArrayRegion(myArray,0,5,buf);
			return myArray;
		}
	}
	return NULL;
}

/**
 * Try to authenticate the remote device.
 * BlueZ uses a timeout for every hci request. But the authenticate
 * method requests the user of the remote device to enter a PIN Code.
 * The best solution would be to wait (in a separate process)
 * until this code is given by the user.
 * As this is not possile, this function uses a timeout of 25 seconds.
 *
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_authenticate
	(JNIEnv *env, jclass obj, jint connectionHandle, jstring badr, jstring pin) {

	struct hci_conn_info_req *cr;
	struct hci_request rq;
	auth_requested_cp cp;
	evt_auth_complete rp;
	bdaddr_t bdaddr;
	const char *straddr;
	int dd;
	jboolean fbol = 1;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	straddr = env->GetStringUTFChars(badr, &fbol);
	str2ba(straddr, &bdaddr);

	int dev_id = hci_for_each_dev(HCI_UP, find_conn, (long) &bdaddr);
	if (dev_id < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_authenticate: Device is not connected!!");
		return -2;
	}

#ifdef __HCI_DEVICE_HACK__
	dd = hci_open_dev(HCI_DEVICE_ID);
#else
	dd = hci_open_dev(dev_id);
#endif
	if (dd < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_authenticate: Unable to open remote device!!");
		return -2;
	}

	cr = (hci_conn_info_req *)malloc(sizeof(*cr) + sizeof(struct hci_conn_info));
	if (!cr)  return -2;

	bacpy(&cr->bdaddr, &bdaddr);
	cr->type = ACL_LINK;
	if (ioctl(dd, HCIGETCONNINFO, (unsigned long) cr) < 0) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_authenticate: Unable to get connection information. The connection may have been closed!!");
		return -2;
	}

	cp.handle = cr->conn_info->handle;

	memset(&rq, 0, sizeof(rq));
	rq.ogf    = OGF_LINK_CTL;
	rq.ocf    = OCF_AUTH_REQUESTED;
	rq.cparam = &cp;
	rq.clen   = AUTH_REQUESTED_CP_SIZE;
	rq.rparam = &rp;
	rq.rlen   = EVT_AUTH_COMPLETE_SIZE;
	rq.event  = EVT_AUTH_COMPLETE;

	if (hci_send_req(dd, &rq, 25000) < 0)
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_authenticate: Unable to set authentication option (you MUST be root under Linux)!!");
	free(cr);
	return 1;
}

/**
 * Returns the Access mode of the local device (mostly GIAC or LIAC). See the bluetooth specification
 * for more details
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_getAccessMode
	(JNIEnv *env, jclass obj, jint device) {
	uint8_t lap[3 * MAX_IAC_LAP];
	uint8_t n;
	int res;
	uint32_t retour;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	res=hci_read_current_iac_lap(device, &n, lap, 1000);
	if (res < 0 || n > 1) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_getAccessMode: Failed to read Access Mode (you must have root privileges under Linux)!!");
		return -1;
	}
	retour=(lap[0] & 0xff) + ((lap[1] & 0xff) << 8) + ((lap[2] & 0xff) << 16);
	return retour;

}

/**
 * Set the access for the local device
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_setAccessMode
	(JNIEnv *env, jclass obj, jint device, jint mode) {
	uint8_t lap[3];

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if (mode < 0x9e8b00 || mode > 0x9e8b3f) {
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_setAccessMode: Invalid Access Mode!!");
		return 0;
	}
	lap[0] = (mode & 0xff);
	lap[1] = (mode >> 8) & 0xff;
	lap[2] = (mode >> 16) & 0xff;
	if (hci_write_current_iac_lap(device, 1, lap, 10000) < 0) return -1;
	return 1;
}

/**
 * Register an L2CAP service and wait for an incoming connection
 */
JNIEXPORT jint JNICALL Java_de_avetana_bluetooth_stack_BlueZ_registerL2CAPService
	(JNIEnv *env, jclass obj, jint serviceHandle, jint channel, jboolean master, jboolean auth, jboolean encrypt,
	 jint omtu, jint imtu) {
	//printf("Function called: %s, %i\n"__FILE__, __LINE__);
	return listenL2CAP(env, ((ServiceHandle *)serviceHandle)->fd, master, auth, encrypt, omtu, imtu, channel);
}

/**
 * Wait for an incoming L2CAP connection.
 * Notes: BlueZ refuses the connection if the server try to set the values of IMTU and OMTU. Maybe there
 * is a bug in the setsockopt for L2CAP sockets.
 * @return
 */
int listenL2CAP(JNIEnv *env, int fd, int master, int auth, int encrypt, int omtu, int imtu, int psm) {
	struct sockaddr_l2 rem_addr;
	struct l2cap_options opts;
	socklen_t  opt;
	int nfd;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	struct pollfd pfd;
	pfd.fd = fd;
	pfd.events = POLLIN | POLLERR | POLLHUP | POLLNVAL;

	while (true) {
		int sel = poll(&pfd, 1, 20);
		
		// ignore if the poll was interrupted
		if (sel == -1 && errno == EINTR)  continue;

			//printf("poll returned %d %X\n", sel, pfd.revents);
		if (sel == -1 || (sel == 1 && ((pfd.revents & 0x30) != 0))) { // Error case
			throwException (env, "Notifier closed");
			return -1;
		} else if(sel > 0) { //Data is available (inc. 0-length packets)
			break;
		} 
	}


	socklen_t alen = sizeof(rem_addr);
	nfd = accept(fd, (struct sockaddr *) &rem_addr, &alen);

	/* Set link mode */
	opt = 0;
	if (master) opt |= L2CAP_LM_MASTER;
	if (auth) opt |= L2CAP_LM_AUTH;
	if (encrypt) opt |= L2CAP_LM_ENCRYPT;

	if (setsockopt(nfd, SOL_L2CAP, L2CAP_LM, &opt, sizeof(opt)) < 0) {
		printf("WARNING - Can't set L2CAP link mode. %s(%d)\n", strerror(errno), errno);
	}

	opt = sizeof(opts);
	if (getsockopt(nfd, SOL_L2CAP, L2CAP_OPTIONS, &opts, &opt) < 0) {
		printf("WARNING - Can't get default L2CAP options. %s(%d)\n", strerror(errno), errno);
	}
	/* Set new options */
	opts.imtu = (imtu==-1?672:imtu);
	opts.omtu = (omtu==-1?672:omtu);
	if(imtu!=-1 && omtu!=-1) {
		if (setsockopt(nfd, SOL_L2CAP, L2CAP_OPTIONS, &opts, opt) < 0) {
			printf("WARNING - Can't set L2CAP options. %s(%d)\n", strerror(errno), errno);
		}
	}

	opt = sizeof(opts);
	if (getsockopt(nfd, SOL_L2CAP, L2CAP_OPTIONS, &opts, &opt) < 0) {
		printf("Can't get L2CAP options. %s(%d)", strerror(errno), errno);
	} else {
		bdaddr_t ba;
		baswap(&ba, &rem_addr.l2_bdaddr);
	}
	char *ba_str=(char *)malloc(18);
	ba2str(&rem_addr.l2_bdaddr, ba_str); // Convert from bdaddr_t to char*

	jstring jBTAddr= env->NewStringUTF(ba_str);
	jclass cls=env->FindClass("de/avetana/bluetooth/stack/BlueZ");
	jmethodID mid=env->GetStaticMethodID(cls,
			"connectionEstablished",
			"(IIILjava/lang/String;)Z");
	jboolean jb = env->CallStaticBooleanMethod(cls, mid, (int)nfd, psm, 0,jBTAddr);
	if (jb != 0) {
		jclass blueZ = env->FindClass("de/avetana/bluetooth/stack/BlueZ");
		jmethodID ssmeth = env->GetStaticMethodID(blueZ, "startReaderThread", "(II)V");
		env->CallStaticVoidMethod(blueZ, ssmeth, (jint)nfd, (jint)opts.imtu);

		return nfd;
	}
	else {
		close (nfd);
	}
	return -1;
}

/**
 * Listen for an incoming RFCOMM connections. The parameters master, auth and encrypt are of no use
 * at this moment, because BlueZ does not implement secured RFCOMM sockets
 * @return
 */
int listenRFCOMM(JNIEnv *env, int fd, int master, int auth, int encrypt, int channel) {

	struct sockaddr_rc remote_addr;
	int nfd;

	socklen_t alen = sizeof(remote_addr);
	
	struct pollfd pfd;
	pfd.fd = fd;
	pfd.events = POLLIN | POLLERR | POLLHUP | POLLNVAL;

	while (true) {
		int sel = poll(&pfd, 1, 20);
		
		// ignore if the poll was interrupted
		if (sel == -1 && errno == EINTR)  continue;

			//printf("poll returned %d %X\n", sel, pfd.revents);
		if (sel == -1 || (sel == 1 && ((pfd.revents & 0x30) != 0))) { // Error case
			throwException (env, "Notifier closed");
			return -1;
		} else if(sel > 0) { //Data is available (inc. 0-length packets)
			break;
		} 
	}


	nfd = accept(fd, (struct sockaddr *) &remote_addr, &alen);
	
	char *ba_str=(char *)malloc(18);
	ba2str(&remote_addr.rc_bdaddr, ba_str); // Convert from bdaddr_t to char*


	jstring jBTAddr= env->NewStringUTF(ba_str);
	jclass cls=env->FindClass("de/avetana/bluetooth/stack/BlueZ");
	jmethodID mid=env->GetStaticMethodID(cls,
			"connectionEstablished",
			"(IIILjava/lang/String;)Z");
	jboolean jb = env->CallStaticBooleanMethod(cls, mid, (int)nfd, channel,1, jBTAddr);
	if (jb != 0) {
		jclass blueZ = env->FindClass("de/avetana/bluetooth/stack/BlueZ");
		jmethodID ssmeth = env->GetStaticMethodID(blueZ, "startReaderThread", "(II)V");
		env->CallStaticVoidMethod(blueZ, ssmeth, (jint)nfd, (jint)1000);

		return nfd;
	}
	else close (nfd);

	return -1;
}

/*
   JNIEXPORT jobject JNICALL Java_de_avetana_bluetooth_stack_BlueZ_getLocalRecord
   (JNIEnv *env, jclass obj, jlong a_handle) {
   uint32_t handle;
   sdp_list_t *attrid;
   uint32_t range = 0x0000ffff;
   bdaddr_t interface;

   handle=(uint32_t)a_handle;
   bacpy(&interface, BDADDR_ANY);

   sdp_session_t *session = sdp_connect(&interface, BDADDR_LOCAL, SDP_RETRY_IF_BUSY);
   if (!session) {
   printf("Failed to connect to SDP server");
   return NULL;
   }

   attrid = sdp_list_append(0, &range);
   jobject rec = my_sdp_service_attr_req(env, obj, session, handle,SDP_ATTR_REQ_RANGE, attrid);
   sdp_list_free(attrid, 0);
   sdp_close(session);
   if (!rec) {
   printf("Service get request failed.\n");
   return NULL;
   }
   return rec;
   }*/

/**
 * Remove the service record identifed by its Record Handle
 */
JNIEXPORT void JNICALL Java_de_avetana_bluetooth_stack_BlueZ_disposeLocalRecord
	(JNIEnv *env, jclass obj, jlong serviceHandle) {

	uint32_t handle, range = 0x0000ffff;
	sdp_list_t *attr;
	sdp_session_t *sess;
	sdp_record_t *rec;
	bdaddr_t interface;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

#ifdef __HCI_DEVICE_HACK__
	hci_devba(HCI_DEVICE_ID, &interface);
#else
	bacpy(&interface, BDADDR_ANY);
#endif

	sess = sdp_connect(&interface, BDADDR_LOCAL, SDP_RETRY_IF_BUSY);
	if (!sess) {
		printf("No local SDP server!\n");
		return;
	}
	handle = (uint32_t)((ServiceHandle *)serviceHandle)->srHandle;
	attr = sdp_list_append(0, &range);
	rec = sdp_service_attr_req(sess, handle, SDP_ATTR_REQ_RANGE, attr);
	sdp_list_free(attr, 0);
	if (!rec) {
		printf("Service Record not found.\n");
		sdp_close(sess);
		return;
	}
	if (sdp_record_unregister(sess, rec)) {
		printf("Failed to unregister service record: %s\n", strerror(errno));
		sdp_close(sess);
		return;
	}
	sdp_close(sess);
	
	close (((ServiceHandle *)serviceHandle)->fd);
	free ((void *)serviceHandle);
	return;
}

/**
 * Perform an SDP service search.<br>
 * jstring bdaddr_jstr The address of the remote BT device
 * jshortArray j_uuids The array of UUIDs that the services must contain
 * jintArray j_attrIds The list of Attribute the returned service should contain.
 *
 */
JNIEXPORT void JNICALL Java_de_avetana_bluetooth_stack_BlueZ_listService
	(JNIEnv *env, jclass obj, jstring bdaddr_jstr, jobjectArray j_uuids, jintArray j_attrIds, jint transID) {
	sdp_list_t *attrid, *search, *seq, *all_attrid;
	uint32_t range = 0x0000ffff;
	sdp_session_t *sess;
	bdaddr_t interface;
	bdaddr_t bdaddr;
	char *bdaddr_str;
	struct search_context context;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	search = NULL;

	// Initialise search context
	memset(&context, '\0', sizeof(struct search_context));
	//Convert the jstring address to a bdaddr_str type

	jboolean fbol = 1;
	bdaddr_str = (char*) env->GetStringUTFChars(bdaddr_jstr, &fbol);
	baswap(&bdaddr, strtoba(bdaddr_str));
	env->ReleaseStringUTFChars(bdaddr_jstr, bdaddr_str);
	sdp_uuid16_create(&(context.group), PUBLIC_BROWSE_GROUP);

#ifdef __HCI_DEVICE_HACK__
	hci_devba(HCI_DEVICE_ID, &interface);
#else
	bacpy(&interface, BDADDR_ANY);
#endif

	sess = sdp_connect(&interface, &bdaddr, SDP_RETRY_IF_BUSY);
	if (!sess) {
		send_searchCompleteEvent(env,SERVICE_SEARCH_DEVICE_NOT_REACHABLE,transID);
		return;
	}

	if (DEBUG == 1) printf("Connected to sdp socket:socket %d, state %d, local %d, flags %d, tid 0x%02x", sess->sock,sess->state,sess->local,sess->flags,sess->tid);

	int i;

	// Decoding list of UUIDs and saving them in a sdp_list_t structure
	jsize len = env->GetArrayLength(j_uuids);
	if (len == 0) {
		uuid_t m_uuid;
		uint16_t m_int=(uint16_t)(0x1002);
		sdp_uuid16_create(&m_uuid,m_int);
		search=sdp_list_append(0,&m_uuid);
	}

	for (i=0 ; i < len ; i++) {
		jbyteArray barr = (jbyteArray)env->GetObjectArrayElement(j_uuids, i);
		int elSize = env->GetArrayLength(barr);
		char *body = (char *)env->GetByteArrayElements (barr, 0);
		uuid_t m_uuid;
		if (elSize == 2) {
			uint16_t m_int=(uint16_t)(body[1] | body[0] << 8);
			sdp_uuid16_create(&m_uuid,m_int);
		} else if (elSize == 4) {
			uint32_t m_int=(uint32_t)(body[3] | body[2] << 8 | body[1] << 16 | body[0] << 24);
			sdp_uuid32_create(&m_uuid,m_int);
		} else if (elSize == 16) {
			sdp_uuid128_create(&m_uuid, (uint128_t *)body);
		} else printf ("UUID of size %d.....ignoring\n", elSize);
		if(i==0) search=sdp_list_append(0,&m_uuid);
		else sdp_list_append(search, &m_uuid);
	}

	jsize len_attr=env->GetArrayLength(j_attrIds);
	jint *attr_ptr=env->GetIntArrayElements(j_attrIds, 0);
	for(int i_attr=0 ; i_attr < len_attr ; i_attr++) {
		if(i_attr==0) attrid=sdp_list_append(0,&(*attr_ptr++));
		else sdp_list_append(attrid, &(*attr_ptr++));
	}
	all_attrid=sdp_list_append(0, &range);
	//	 printf ("my_sdp_service_search_attr_req...start\n");
	if (my_sdp_service_search_attr_req(env, obj,sess, search, SDP_ATTR_REQ_RANGE, all_attrid, &seq, attrid, bdaddr_jstr, transID)) {
		//	   printf ("my_sdp_service_search_attr_req...done error\n");
		send_searchCompleteEvent(env,SERVICE_SEARCH_ERROR, transID);
		sdp_close(sess);
		throwException(env, "Java_de_avetana_bluetooth_stack_BlueZ_listServices: Search failed!!");
		return;
	}

	//	 printf ("my_sdp_service_search_attr_req...done ok\n");
	sdp_list_free(search,0);
	send_searchCompleteEvent(env,SERVICE_SEARCH_COMPLETED, transID);
	sdp_close(sess);

	return;
}

/**
 * Send to the JSR82 implementation the event "the sdp search is no completed". The sentCode
 * variable represents the search result (successful, failed ..etc..)
 */
void send_searchCompleteEvent(JNIEnv* env, int sentCode, int transID) {

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	jclass cls=env->FindClass("de/avetana/bluetooth/stack/BlueZ");
	jmethodID mid=env->GetStaticMethodID(cls,
			"serviceSearchComplete",
			"(II)V");
	env->CallStaticVoidMethod(cls, mid, transID, sentCode);
}

/**
 * Function from the BlueZ librairies, which is not accessible (why? If this function is not redefined here,
 * an "Undefined reference error" occurs at runtime).
 * @return
 */

static int copy_cstate(char *pdata, const sdp_cstate_t *cstate) {

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if (cstate) {
		*pdata++ = cstate->length;
		memcpy(pdata, cstate->data, cstate->length);
		return cstate->length + 1;
	}
	*pdata = 0;
	return 1;
}

/**
 * The function that does the local service search. Part of the code comes from the BlueZ implementation.
 * But Java objects instead of local C structures are used with this function to parse the
 * bytes received from the stack.
 * @return A LocalServiceRecord java object.
 */
jobject my_sdp_service_attr_req(JNIEnv *env, jclass jcls,
		sdp_session_t *session, uint32_t handle,
		sdp_attrreq_type_t reqtype,
		const sdp_list_t *attrids) {
	int status = 0;
	int reqsize = 0, _reqsize;
	int rspsize = 0, rsp_count;
	int attr_list_len = 0;
	int seqlen = 0;
	char *pdata, *_pdata;
	char *reqbuf, *rspbuf;
	sdp_pdu_hdr_t *reqhdr, *rsphdr;
	sdp_cstate_t *cstate = NULL;
	uint8_t cstate_len = 0;
	sdp_buf_t rsp_concat_buf;
	jobject rec = NULL;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if (reqtype != SDP_ATTR_REQ_INDIVIDUAL && reqtype != SDP_ATTR_REQ_RANGE) {
		errno = EINVAL;
		return 0;
	}

	reqbuf = (char *)malloc(SDP_REQ_BUFFER_SIZE);
	rspbuf = (char *)malloc(SDP_RSP_BUFFER_SIZE);
	if (!reqbuf || !rspbuf) {
		errno = ENOMEM;
		status = -1;
		goto end;
	}
	memset((char *)&rsp_concat_buf, 0, sizeof(sdp_buf_t));
	reqhdr = (sdp_pdu_hdr_t *)reqbuf;
	reqhdr->pdu_id = SDP_SVC_ATTR_REQ;

	pdata = reqbuf + sizeof(sdp_pdu_hdr_t);
	reqsize = sizeof(sdp_pdu_hdr_t);

	// add the service record handle
	sdp_put_unaligned(htonl(handle), (uint32_t *)pdata);
	reqsize += sizeof(uint32_t);
	pdata += sizeof(uint32_t);

	// specify the response limit
	sdp_put_unaligned(htons(65535), (uint16_t *)pdata);
	reqsize += sizeof(uint16_t);
	pdata += sizeof(uint16_t);

	// get attr seq PDU form
	seqlen = gen_attridseq_pdu(pdata, attrids,
			reqtype == SDP_ATTR_REQ_INDIVIDUAL? SDP_UINT16 : SDP_UINT32);
	if (seqlen == -1) {
		errno = EINVAL;
		status = -1;
		goto end;
	}
	pdata += seqlen;
	reqsize += seqlen;
	SDPDBG("Attr list length : %d\n", seqlen);

	// save before Continuation State
	_pdata = pdata;
	_reqsize = reqsize;

	do {
		// add NULL continuation state
		reqsize = _reqsize + copy_cstate(_pdata, cstate);

		// set the request header's param length
		reqhdr->tid  = htons(sdp_gen_tid(session));
		reqhdr->plen = htons(reqsize - sizeof(sdp_pdu_hdr_t));

		status = m_sdp_send_req_w4_rsp(session, reqbuf, rspbuf, reqsize, &rspsize);
		if (status < 0)
			goto end;
		rsp_count = 0;
		rsphdr = (sdp_pdu_hdr_t *)rspbuf;
		if (rsphdr->pdu_id == SDP_ERROR_RSP) {
			SDPDBG("PDU ID : 0x%x\n", rsphdr->pdu_id);
			status = -1;
			goto end;
		}
		pdata = rspbuf + sizeof(sdp_pdu_hdr_t);
		rsp_count = ntohs(sdp_get_unaligned((uint16_t *)pdata));
		attr_list_len += rsp_count;
		pdata += sizeof(uint16_t);

		// if continuation state set need to re-issue request before parsing
		cstate_len = *(uint8_t *)(pdata + rsp_count);


		// a split response: concatenate intermediate responses
		// and the last one (which has cstate_len == 0)

		if (cstate_len > 0 || rsp_concat_buf.data_size != 0) {
			char *targetPtr = NULL;

			cstate = cstate_len > 0? (sdp_cstate_t *)(pdata + rsp_count): 0;

			// build concatenated response buffer
			rsp_concat_buf.data = (uint8_t *)realloc(rsp_concat_buf.data, rsp_concat_buf.data_size + rsp_count);
			rsp_concat_buf.buf_size = rsp_concat_buf.data_size + rsp_count;
			targetPtr = (char *) rsp_concat_buf.data + rsp_concat_buf.data_size;
			memcpy(targetPtr, pdata, rsp_count);
			rsp_concat_buf.data_size += rsp_count;
		}
	} while (cstate);

	if (attr_list_len > 0) {
		int scanned = 0;
		if (rsp_concat_buf.data_size != 0)
			pdata = (char *) rsp_concat_buf.data;
		jclass service_cls = env->FindClass("de/avetana/bluetooth/sdp/LocalServiceRecord");
		jmethodID service_constructor = env->GetMethodID(service_cls, "<init>", "()V");
		rec = env->NewObject(service_cls, service_constructor);
		fill_jobject(env, jcls, pdata, &scanned, NULL, &rec);

		if (!rec)
			status = -1;
	}

end:
	if (reqbuf)
		free(reqbuf);
	if (rsp_concat_buf.data)
		free(rsp_concat_buf.data);
	if (rspbuf)
		free(rspbuf);
	return rec;
}


int my_sdp_service_search_attr_req(JNIEnv* env, jclass jobj, sdp_session_t *session, const sdp_list_t *search,
		sdp_attrreq_type_t reqtype, const sdp_list_t *attrids,
		sdp_list_t **rsp, sdp_list_t *attr_list, jstring addr, jint transID) {
	int status = 0;
	int reqsize = 0, _reqsize;
	int rspsize = 0;
	int seqlen = 0, attr_list_len = 0;
	int rsp_count = 0, cstate_len = 0;
	char *pdata, *_pdata;
	char *reqbuf, *rspbuf;
	sdp_pdu_hdr_t *reqhdr, *rsphdr;
	uint8_t dataType;
	sdp_list_t *rec_list = NULL;
	sdp_buf_t rsp_concat_buf;
	sdp_cstate_t *cstate = NULL;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if (reqtype != SDP_ATTR_REQ_INDIVIDUAL && reqtype != SDP_ATTR_REQ_RANGE) {
		// With the list service implementation, this error is not possible!!
		errno = EINVAL;
		return -1;
	}
	reqbuf = (char *)malloc(SDP_REQ_BUFFER_SIZE);
	rspbuf = (char *)malloc(SDP_RSP_BUFFER_SIZE);
	if (!reqbuf || !rspbuf) {
		errno = ENOMEM;
		status = -1;
		goto end;
	}

	memset((char *)&rsp_concat_buf, 0, sizeof(sdp_buf_t));
	reqhdr = (sdp_pdu_hdr_t *)reqbuf;
	reqhdr->pdu_id = SDP_SVC_SEARCH_ATTR_REQ;

	// generate PDU
	pdata = reqbuf + sizeof(sdp_pdu_hdr_t);
	reqsize = sizeof(sdp_pdu_hdr_t);

	// add service class IDs for search
	seqlen = gen_searchseq_pdu(pdata, search);

	if(DEBUG==1)  printf("Data seq added : %d\n", seqlen);

	// now set the length and increment the pointer
	reqsize += seqlen;
	pdata += seqlen;

	sdp_put_unaligned(htons(SDP_MAX_ATTR_LEN), (uint16_t *)pdata);
	reqsize += sizeof(uint16_t);
	pdata += sizeof(uint16_t);

	if(DEBUG==1)  printf("Max attr byte count : %d\n", SDP_MAX_ATTR_LEN);

	// get attr seq PDU form
	seqlen = gen_attridseq_pdu(pdata, attrids,
			reqtype == SDP_ATTR_REQ_INDIVIDUAL?  SDP_UINT16: SDP_UINT32);
	if (seqlen == -1) {
		status = EINVAL;
		goto end;
	}
	pdata += seqlen;
	if(DEBUG==1)  printf("Attr list length : %d\n", seqlen);
	reqsize += seqlen;
	*rsp = 0;

	// save before Continuation State
	_pdata = pdata;
	_reqsize = reqsize;

	do {
		reqhdr->tid = htons(sdp_gen_tid(session));

		// add continuation state (can be null)
		reqsize = _reqsize + copy_cstate(_pdata, cstate);

		// set the request header's param length
		reqhdr->plen = htons(reqsize - sizeof(sdp_pdu_hdr_t));
		rsphdr = (sdp_pdu_hdr_t *)rspbuf;
		status = m_sdp_send_req_w4_rsp(session, reqbuf, rspbuf, reqsize, &rspsize);
		if (status < 0) {
			if(DEBUG==1)  printf("Status : 0x%x\n", rsphdr->pdu_id);
			goto end;
		}

		if (rsphdr->pdu_id == SDP_ERROR_RSP) {
			status = -1;
			goto end;
		}

		pdata = rspbuf + sizeof(sdp_pdu_hdr_t);
		rsp_count = ntohs(sdp_get_unaligned((uint16_t *)pdata));
		attr_list_len += rsp_count;
		pdata += sizeof(uint16_t);	// pdata points to attribute list
		cstate_len = *(uint8_t *)(pdata + rsp_count);

		if(DEBUG==1)  printf("Attrlist byte count : %d\n", attr_list_len);
		if(DEBUG==1)  printf("Response byte count : %d\n", rsp_count);
		if(DEBUG==1)  printf("Cstate length : %d\n", cstate_len);
		/*
		 * This is a split response, need to concatenate intermediate
		 * responses and the last one which will have cstate_len == 0
		 */
		if (cstate_len > 0 || rsp_concat_buf.data_size != 0) {
			char *targetPtr = NULL;

			cstate = cstate_len > 0? (sdp_cstate_t *)(pdata + rsp_count): 0;

			// build concatenated response buffer
			rsp_concat_buf.data = (uint8_t *)realloc(rsp_concat_buf.data, rsp_concat_buf.data_size + rsp_count);
			targetPtr = (char *) rsp_concat_buf.data + rsp_concat_buf.data_size;
			rsp_concat_buf.buf_size = rsp_concat_buf.data_size + rsp_count;
			memcpy(targetPtr, pdata, rsp_count);
			rsp_concat_buf.data_size += rsp_count;
		}
	} while (cstate);

	if (attr_list_len > 0) {
		int scanned = 0;

		if (rsp_concat_buf.data_size != 0)
			pdata = (char*) rsp_concat_buf.data;

		/*
		 * Response is a sequence of sequence(s) for one or
		 * more data element sequence(s) representing services
		 * for which attributes are returned
		 */
		if(DEBUG==1) printf("Sequence len before extracttype=%d\n",seqlen);
		scanned = sdp_extract_seqtype(pdata, &dataType, &seqlen);

		if(DEBUG==1) printf("Bytes scanned : %d\n", scanned);
		if(DEBUG==1)  printf("Seq length : %d\n", seqlen);

		if (scanned && seqlen) {
			pdata += scanned;
			do {
				int recsize = 0;
				jclass service_cls = env->FindClass("de/avetana/bluetooth/sdp/RemoteServiceRecord");
				jmethodID service_constructor = env->GetMethodID(service_cls, "<init>", "(Ljava/lang/String;)V");
				jobject rec = env->NewObject(service_cls, service_constructor, addr);
				fill_jobject(env,jobj, pdata, &recsize, attr_list, &rec);
				if(DEBUG==1)  printf("jobject extracted\n");
				if (rec == NULL) {
					if(DEBUG==1) printf("SVC REC is null\n");
					status = -1;
					goto end;
				}
				jclass cls=env->FindClass("de/avetana/bluetooth/stack/BlueZ");
				jmethodID mid=env->GetStaticMethodID(cls,
						"addService",
						"(ILjavax/bluetooth/ServiceRecord;)V");
				if(DEBUG)  printf("mid=%i", (int)mid);
				if (mid == 0) {
					if(DEBUG==1)  printf("Unable to get method addService!!!!");
				}
				else {
					env->CallStaticVoidMethod(cls, mid, transID, rec);
				}
				scanned += recsize;
				pdata += recsize;

			} while (scanned < attr_list_len);
			if(DEBUG==1)  printf("Successful scan of service attr lists\n");
		}
		*rsp = rec_list;

	}
end:
	if (rsp_concat_buf.data)
		free(rsp_concat_buf.data);
	if (reqbuf)
		free(reqbuf);
	if (rspbuf)
		free(rspbuf);
	return status;
}

int list_contains_attr(sdp_list_t *attr_list, uint16_t comp) {
	sdp_list_t *clone;
	int i;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if(attr_list==NULL) return 1;

	clone=attr_list;
	int seqlen=sdp_list_len(clone);
	for (i = 0; i < seqlen; i++) {
		uint16_t *data = (uint16_t *)clone->data;
		if((*data)==comp) return 1;
		clone = clone->next;
	}
	return 0;
}

void fill_jobject(JNIEnv *env, jclass obj, const char *buf, int *scanned, sdp_list_t *attr_list, jobject *defRecord) {
	int extracted = 0, seqlen = 0;
	uint8_t dtd;
	uint16_t attr;
	const char *p = buf;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	*scanned = sdp_extract_seqtype(buf, &dtd, &seqlen);
	p += *scanned;

	jclass service_cls=env->GetObjectClass(*defRecord);

	while (extracted < seqlen) {
		int n = sizeof(uint8_t), attrlen = 0;

		if(DEBUG==1) printf("SequenceLength: %d ExtractedLength: %d\n", seqlen, extracted);

		dtd = *(uint8_t *)p;
		if(DEBUG==1) printf("DTD of attrID :%d\n",dtd);
		attr = ntohs(sdp_get_unaligned((uint16_t *)(p+n)));
		n += sizeof(uint16_t);
		const char* tmp=p;
		extract_attr_len(p+n,&attrlen);
		if(DEBUG==1) printf("Attr ID : 0x%x Attr bytes length : %d\n", attr, attrlen);
		if(list_contains_attr(attr_list, attr)==1) {
			char *s = (char *)malloc(attrlen);
			memcpy(s, tmp+n, attrlen);
			jbyteArray jbData;
			jbData=env->NewByteArray(attrlen);
			env->SetByteArrayRegion(jbData, 0, attrlen,(jbyte*)s);
			jclass data_class=env->FindClass("javax/bluetooth/DataElement");
			jmethodID data_method = env->GetMethodID(data_class, "<init>", "([B)V");
			jobject j_data= env->NewObject(data_class, data_method, jbData);
			jmethodID mid=env->GetMethodID(service_cls,
					"setAttributeValue",
					"(ILjavax/bluetooth/DataElement;)Z");
			if(mid==0) {
				if(DEBUG==1)  printf("Unable to initialize methode id with setAttributeValue");
			}
			env->CallBooleanMethod(*defRecord, mid, attr,j_data);
		}
		n += attrlen;
		extracted += n;
		p += n;
		if(DEBUG==1)  printf("Attribute extracted %d\n",extracted);
	}
	*scanned += seqlen;
	if(DEBUG==1)  printf("Successful extracting of Svc Rec attributes scanned=%d\n",*scanned);
	if(DEBUG==1)  printf("\n\n");
}



int sdp_send_req(sdp_session_t *session, char *buf, int size)
{
	int sent = 0;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	while (sent < size) {
		int n = send(session->sock, buf + sent, size - sent, 0);
		if (n < 0)
			return -1;
		sent += n;
	}
	return 0;
}

int sdp_read_rsp(sdp_session_t *session, char *buf, int size)
{
	fd_set readFds;
	struct timeval timeout = { SDP_RESPONSE_TIMEOUT, 0 };

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	FD_SET(session->sock, &readFds);
	SDPDBG("Waiting for response\n");
	if (0 == select(session->sock + 1, &readFds, NULL, NULL, &timeout)) {
		SDPERR("Client timed out\n");
		errno = ETIMEDOUT;
		return -1;
	}
	return recv(session->sock, buf, size, 0);
}


// generic send request, wait for response method.

int m_sdp_send_req_w4_rsp(sdp_session_t *session, char *reqbuf, char *rspbuf, int reqsize, int *rspsize)
{
	int n;
	sdp_pdu_hdr_t *reqhdr = (sdp_pdu_hdr_t *)reqbuf;
	sdp_pdu_hdr_t *rsphdr = (sdp_pdu_hdr_t *)rspbuf;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	SDPDBG("");
	if (0 > sdp_send_req(session, reqbuf, reqsize)) {
		SDPERR("Error sending data:%s", strerror(errno));
		return -1;
	}
	n = sdp_read_rsp(session, rspbuf, SDP_RSP_BUFFER_SIZE);
	if (0 > n)
		return -1;
	SDPDBG("Read : %d\n", n);
	if (n == 0 || reqhdr->tid != rsphdr->tid) {
		errno = EPROTO;
		return -1;
	}
	*rspsize = n;
	return 0;
}

int sdp_extract_seqtype(const char *buf, uint8_t *dtdp, int *size)
{
	uint8_t dtd = *(uint8_t *)buf;
	int scanned = sizeof(uint8_t);

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	buf += sizeof(uint8_t);
	*dtdp = dtd;
	switch (dtd) {
		case SDP_SEQ8:
		case SDP_ALT8:
			*size = *(uint8_t *)buf;
			scanned += sizeof(uint8_t);
			break;
		case SDP_SEQ16:
		case SDP_ALT16:
			*size = ntohs(sdp_get_unaligned((uint16_t *)buf));
			scanned += sizeof(uint16_t);
			break;
		case SDP_SEQ32:
		case SDP_ALT32:
			*size = ntohl(sdp_get_unaligned((uint32_t *)buf));
			scanned += sizeof(uint32_t);
			break;
		default:
			SDPERR("Unknown sequence type, aborting\n");
			return 0;
	}
	return scanned;
}

/**
 * Extract the length of an Attribute, whatever its type.
 * @return
 */
int extract_attr_len(const char *p, int *size)
{
	int n = 0;
	uint8_t dtd = *(const uint8_t *)p;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	switch (dtd) {
		case SDP_DATA_NIL:case SDP_BOOL:case SDP_UINT8:case SDP_UINT16:case SDP_UINT32:
		case SDP_UINT64:case SDP_UINT128:case SDP_INT8:case SDP_INT16:case SDP_INT32:
		case SDP_INT64:case SDP_INT128:
			extract_int_len(p, &n);
			break;
		case SDP_UUID16:case SDP_UUID32:case SDP_UUID128:
			extract_uuid_len(p, &n);
			break;
		case SDP_TEXT_STR8:case SDP_TEXT_STR16:case SDP_TEXT_STR32:
		case SDP_URL_STR8:case SDP_URL_STR16:case SDP_URL_STR32:
			extract_str_len(p, &n);
			break;
		case SDP_SEQ8:case SDP_SEQ16:case SDP_SEQ32:case SDP_ALT8:
		case SDP_ALT16:case SDP_ALT32:
			extract_seq_len(p, &n);
			break;
		default:
			SDPERR("Unknown data descriptor : 0x%x terminating\n", dtd);
			return -1;
	}
	*size += n;
	return 0;
}

/**
 * Extract the length of an UUID
 */
int extract_uuid_len(const char *p, int *scanned)
{
	uint8_t type = *(const uint8_t *)p;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	if (!SDP_IS_UUID(type)) {
		SDPERR("Unknown data type : %d expecting a svc UUID\n", type);
		return -1;
	}
	p += sizeof(uint8_t);
	*scanned += sizeof(uint8_t);
	if (type == SDP_UUID16) {
		*scanned += sizeof(uint16_t);
		p += sizeof(uint16_t);
	} else if (type == SDP_UUID32) {
		*scanned += sizeof(uint32_t);
		p += sizeof(uint32_t);
	} else {
		*scanned += sizeof(uint128_t);
		p += sizeof(uint128_t);
	}
	return 0;
}

/**
 * Extract the length of a String
 */
void extract_str_len(const char *p, int *len) {
	int n;
	uint8_t dtd = *(uint8_t *)p;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	p += sizeof(uint8_t);
	*len += sizeof(uint8_t);
	switch (dtd) {
		case SDP_TEXT_STR8:
		case SDP_URL_STR8:
			n = *(uint8_t *)p;
			p += sizeof(uint8_t);
			*len += sizeof(uint8_t) + n;
			break;
		case SDP_TEXT_STR16:
		case SDP_URL_STR16:
			n = ntohs(sdp_get_unaligned((uint16_t *)p));
			p += sizeof(uint16_t);
			*len += sizeof(uint16_t) + n;
			break;
		case SDP_TEXT_STR32:
		case SDP_URL_STR32:
			n = ntohs(sdp_get_unaligned((uint32_t *)p));
			p += sizeof(uint32_t);
			*len += sizeof(uint32_t) + n;
			break;
		default:
			return;
	}
}

/**
 * Extract the length of a Sequence.
 */
void extract_seq_len(const char *p, int *len) {
	int seqlen, n = 0;
	uint8_t dtd;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	*len = sdp_extract_seqtype((char *)p, &dtd, &seqlen);
	if (*len == 0) return;

	p += *len;
	while (n < seqlen) {
		int attrlen = 0;
		int retour=extract_attr_len((char *)p, &attrlen);
		if (retour < 0) break;
		p += attrlen;
		n += attrlen;
	}
	*len += n;
}

void extract_int_len(const char *p, int *len) {
	uint8_t dtd=*(uint8_t *)p;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	p += sizeof(uint8_t);
	*len+= sizeof(uint8_t);
	switch(dtd) {
		case SDP_DATA_NIL:
			break;
		case SDP_BOOL:
		case SDP_INT8:
		case SDP_UINT8:
			*len += sizeof(uint8_t);
			break;
		case SDP_INT16:
		case SDP_UINT16:
			*len += sizeof(uint16_t);
			break;
		case SDP_INT32:
		case SDP_UINT32:
			*len += sizeof(uint32_t);
			break;
		case SDP_INT64:
		case SDP_UINT64:
			*len += sizeof(uint64_t);
			break;
		case SDP_INT128:
		case SDP_UINT128:
			*len += sizeof(uint128_t);
			break;
		default:
			return;
	}
}

int sdp_gen_pdu(sdp_buf_t *buf, sdp_data_t *d)
{
	int pdu_size = 0, data_size = 0;
	unsigned char *src = NULL, is_seq = 0, is_alt = 0;
	uint8_t dtd = d->dtd;
	uint16_t u16;
	uint32_t u32;
	uint64_t u64;
	uint128_t u128;
	char *seqp = (char *) (buf->data + buf->data_size);

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	pdu_size = sdp_set_data_type(buf, dtd);
	switch (dtd) {
		case SDP_DATA_NIL:
			break;
		case SDP_UINT8:
			src = &d->val.uint8;
			data_size = sizeof(uint8_t);
			break;
		case SDP_UINT16:
			u16 = htons(d->val.uint16);
			src = (unsigned char *)&u16;
			data_size = sizeof(uint16_t);
			break;
		case SDP_UINT32:
			u32 = htonl(d->val.uint32);
			src = (unsigned char *)&u32;
			data_size = sizeof(uint32_t);
			break;
		case SDP_UINT64:
			u64 = hton64(d->val.uint64);
			src = (unsigned char *)&u64;
			data_size = sizeof(uint64_t);
			break;
		case SDP_UINT128:
			hton128(&d->val.uint128, &u128);
			src = (unsigned char *)&u128;
			data_size = sizeof(uint128_t);
			break;
		case SDP_INT8:
		case SDP_BOOL:
			src = (unsigned char *)&d->val.int8;
			data_size = sizeof(int8_t);
			break;
		case SDP_INT16:
			u16 = htons(d->val.int16);
			src = (unsigned char *)&u16;
			data_size = sizeof(int16_t);
			break;
		case SDP_INT32:
			u32 = htonl(d->val.int32);
			src = (unsigned char *)&u32;
			data_size = sizeof(int32_t);
			break;
		case SDP_INT64:
			u64 = hton64(d->val.int64);
			src = (unsigned char *)&u64;
			data_size = sizeof(int64_t);
			break;
		case SDP_INT128:
			hton128(&d->val.int128, &u128);
			src = (unsigned char *)&u128;
			data_size = sizeof(uint128_t);
			break;
		case SDP_TEXT_STR8:
		case SDP_URL_STR8:
		case SDP_TEXT_STR16:
		case SDP_TEXT_STR32:
		case SDP_URL_STR16:
		case SDP_URL_STR32:
			src = (unsigned char *)d->val.str;
			data_size = strlen(d->val.str);
			sdp_set_seq_len(seqp, data_size);
			break;
		case SDP_SEQ8:
		case SDP_SEQ16:
		case SDP_SEQ32:
			is_seq = 1;
			data_size = get_data_size(buf, d);
			sdp_set_seq_len(seqp, data_size);
			break;
		case SDP_ALT8:
		case SDP_ALT16:
		case SDP_ALT32:
			is_alt = 1;
			data_size = get_data_size(buf, d);
			sdp_set_seq_len(seqp, data_size);
			break;
		case SDP_UUID16:
			u16 = htons(d->val.uuid.value.uuid16);
			src = (unsigned char *)&u16;
			data_size = sizeof(uint16_t);
			break;
		case SDP_UUID32:
			u32 = htonl(d->val.uuid.value.uuid32);
			src = (unsigned char *)&u32;
			data_size = sizeof(uint32_t);
			break;
		case SDP_UUID128:
			src = (unsigned char *)&d->val.uuid.value.uuid128;
			data_size = sizeof(uint128_t);
			break;
		default:
			break;
	}
	if (!is_seq && !is_alt) {
		if (src && buf) {
			memcpy(buf->data + buf->data_size, src, data_size);
			buf->data_size += data_size;
		} else if (dtd != SDP_DATA_NIL)
			SDPDBG("Gen PDU : Cant copy from NULL source or dest\n");
	}
	pdu_size += data_size;
	return pdu_size;
}

static int get_data_size(sdp_buf_t *buf, sdp_data_t *sdpdata)
{
	sdp_data_t *d;
	int n = 0;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	for (d = sdpdata->val.dataseq; d; d = d->next)
		n += sdp_gen_pdu(buf, d);
	return n;
}

int sdp_set_data_type(sdp_buf_t *buf, uint8_t dtd)
{
	int orig = buf->data_size;
	uint8_t *p = (uint8_t *)buf->data + buf->data_size;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	*p++ = dtd;
	buf->data_size += sizeof(uint8_t);
	switch (dtd) {
		case SDP_SEQ8:
		case SDP_TEXT_STR8:
		case SDP_URL_STR8:
		case SDP_ALT8:
			buf->data_size += sizeof(uint8_t);
			break;
		case SDP_SEQ16:
		case SDP_TEXT_STR16:
		case SDP_URL_STR16:
		case SDP_ALT16:
			buf->data_size += sizeof(uint16_t);
			break;
		case SDP_SEQ32:
		case SDP_TEXT_STR32:
		case SDP_URL_STR32:
		case SDP_ALT32:
			buf->data_size += sizeof(uint32_t);
			break;
	}
	return buf->data_size - orig;
}

void sdp_set_seq_len(char *ptr, int length)
{
	uint8_t dtd = *(uint8_t *)ptr++;

	//printf("Function called: %s, %i\n"__FILE__, __LINE__);

	switch (dtd) {
		case SDP_SEQ8:
		case SDP_ALT8:
		case SDP_TEXT_STR8:
		case SDP_URL_STR8:
			*(uint8_t *)ptr = (uint8_t)length;
			break;
		case SDP_SEQ16:
		case SDP_ALT16:
		case SDP_TEXT_STR16:
		case SDP_URL_STR16:
			sdp_put_unaligned(htons(length), (uint16_t *)ptr);
			break;
		case SDP_SEQ32:
		case SDP_ALT32:
		case SDP_TEXT_STR32:
		case SDP_URL_STR32:
			sdp_put_unaligned(htons(length), (uint32_t *)ptr);
			break;
	}
}


