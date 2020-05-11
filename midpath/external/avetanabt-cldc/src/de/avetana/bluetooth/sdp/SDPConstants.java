package de.avetana.bluetooth.sdp;

/**
 * The class used to easily access all SDP assigned numbers.
 *
 * <br><br><b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
 *
 * This file is part of the Avetana bluetooth API for Linux.<br><br>
 *
 * The Avetana bluetooth API for Linux is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version. <br><br>
 *
 * The Avetana bluetooth API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br><br>
 *
 * The development of the Avetana bluetooth API is based on the work of
 * Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
 * on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
 * on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
 * Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
 * are explicitly mentioned.<br><br><br><br>
 *
 *
 * <b>Description:</b><br>
 * This class stores all SDP assigned numbers defined by the bluetooth SIG. For more information please refer
 * to the web site https://www.bluetooth.org/foundry/assignnumb/document/service_discovery <br>
 * The names of the constants were choosen to be easily interpretable and to be similar to the names choosen
 * by the BlueZ development team.<br>
 *
 *
 * @author Julien Campana
 */
public class SDPConstants {
	public static final short UUID_SDP = 0x0001;
	public static final short UUID_UDP = 0x0002;
	public static final short UUID_RFCOMM = 0x0003;
	public static final short UUID_TCP = 0x0004;
	public static final short UUID_TCS_BIN = 0x0005;
	public static final short UUID_TCS_AT = 0x0006;
	public static final short UUID_OBEX = 0x0008;
	public static final short UUID_IP = 0x0009;
	public static final short UUID_FTP = 0x000A;
	public static final short UUID_HTTP = 0x000C;
	public static final short UUID_WSP = 0x000E;
	public static final short UUID_BNEP = 0x000F;
	public static final short UUID_UPNP = 0x0010;
	public static final short UUID_HIDP = 0x0011;
	public static final short UUID_HARDCOPYCONTROLCHANNEL = 0x0012;
	public static final short UUID_HARDCOPYDATACHANNEL = 0x0014;
	public static final short UUID_HARDCOPYNOTIFICATION = 0x0016;
	public static final short UUID_AVCTP = 0x0017;
	public static final short UUID_AVDTP = 0x0019;
	public static final short UUID_CMTP = 0x001B;
	public static final short UUID_UDI_C_Plane = 0x001D;
	public static final short UUID_L2CAP = 0x0100;

	public static final short UUID_SDP_SERVER = 0x1000;
	public static final short UUID_BROWSE_GROUP_DESCRIPTOR = 0x1001;
	public static final short UUID_PUBLICBROWSE_GROUP = 0x1002;
	public static final short UUID_SERIAL_PORT = 0x1101;
	public static final short UUID_LAN_ACCESS_PPP = 0x1102;
	public static final short UUID_DIALUP_NETWORKING = 0x1103;
	public static final short UUID_IR_MC_SYNC = 0x1104;
	public static final short UUID_OBEX_OBJECT_PUSH = 0x1105;
	public static final short UUID_OBEX_FILE_TRANSFER = 0x1106;
	public static final short UUID_IR_MC_SYNC_COMMAND = 0x1107;
	public static final short UUID_HEADSET = 0x1108;
	public static final short UUID_CORDLESS_TELEPHONY = 0x1109;
	public static final short UUID_AUDIO_SOURCE = 0x110A;
	public static final short UUID_AUDIO_SINK = 0x110B;
	public static final short UUID_AV_REMOTE_CTL_TARGET = 0x110C;
	public static final short UUID_ADVANCED_AUDIO_DISTRIB = 0x110D;
	public static final short UUID_AV_REMOTE_CTL = 0x110E;
	public static final short UUID_VIDEO_CONFERENCING = 0x110F;
	public static final short UUID_INTERCOM = 0x1110;
	public static final short UUID_FAX = 0x1111;
	public static final short UUID_HEADSET_AUDIO_GATEWAY = 0x1112;
	public static final short UUID_WAP = 0x1113;
	public static final short UUID_WAP_CLIENT = 0x1114;
	public static final short UUID_PANU = 0x1115;
	public static final short UUID_NAP = 0x1116;
	public static final short UUID_GN = 0x1117;
	public static final short UUID_DIRECT_PRINTING = 0x1118;
	public static final short UUID_REFERENCE_PRINTING = 0x1119;
	public static final short UUID_IMG = 0x111A;
	public static final short UUID_IMG_RESPONDER = 0x111B;
	public static final short UUID_IMG_AUTO_ARCHIVE = 0x111C;
	public static final short UUID_IMG_REFERENCE_OBJECTS = 0x111D;
	public static final short UUID_HANDSFREE = 0x111E;
	public static final short UUID_HANDSFREE_AUDIO_GATEWAY = 0x111F;
	public static final short UUID_DIRECT_PRINT = 0x1120;
	public static final short UUID_REFLECTED_UI = 0x1121;
	public static final short UUID_BASIC_PRINTING = 0x1122;
	public static final short UUID_PRINTING_STATUS = 0x1123;
	public static final short UUID_HI_DEVICE = 0x1124;
	public static final short UUID_HARD_COPY_CABLE_REPLACE = 0x1125;
	public static final short UUID_HCR_PRINT = 0x1126;
	public static final short UUID_HCR_SCAN = 0x1127;
	public static final short UUID_COMMON_ISDN_ACCESS = 0x1128;
	public static final short UUID_VIDEO_CONF_GW = 0x1129;
	public static final short UUID_UDI_MT = 0x112A;
	public static final short UUID_UDI_TA = 0x112B;
	public static final short UUID_AUDIO_VIDEO = 0x112C;
	public static final short UUID_SIM_ACCESS = 0x112D;
	public static final short UUID_PNP_INFO = 0x1200;
	public static final short UUID_GENERIC_NETWORKING = 0x1201;
	public static final short UUID_GENERIC_FILE_TRANSFER = 0x1202;
	public static final short UUID_GENERIC_AUDIO = 0x1203;
	public static final short UUID_GENERIC_TELEPHONY = 0x1204;
	public static final short UUID_UPNP_SERVICE = 0x1205;
	public static final short UUID_UPNP_IP_SERVICE = 0x1206;
	public static final short UUID_ESDP_UPNP_IP_PAN = 0x1300;
	public static final short UUID_ESDP_UPNP_IP_LAP = 0x1301;
	public static final short UUID_ESDP_UPNP_L2CAP = 0x1302;

	public static final short ATTR_RECORD_HANDLE = 0x0000;
	public static final short ATTR_SERVICE_CLASS_ID_LIST = 0x0001;
	public static final short ATTR_RECORD_STATE = 0x0002;
	public static final short ATTR_SERVICE_ID = 0x0003;
	public static final short ATTR_PROTO_DESC_LIST = 0x0004;
	public static final short ATTR_BROWSE_GRP_LIST = 0x0005;
	public static final short ATTR_LANG_BASE_ATTR_ID_LIST = 0x0006;
	public static final short ATTR_SVCINFO_TTL = 0x0007;
	public static final short ATTR_SERVICE_AVAILABILITY = 0x0008;
	public static final short ATTR_PFILE_DESC_LIST = 0x0009;
	public static final short ATTR_DOC_URL = 0x000A;
	public static final short ATTR_CLNT_EXEC_URL = 0x000B;
	public static final short ATTR_ICON_URL = 0x000C;

	public static final short ATTR_IP_SUBNET = 0x0200;
	public static final short ATTR_SERVICE_VERSION = 0x0300;
	public static final short ATTR_ATTR_EXTERNAL_NETWORK = 0x0301;
	public static final short ATTR_SUPPORTED_DATA_STORES_LIST = 0x0301;
	public static final short ATTR_REMOTE_AUDIO_VOLUME_CONTROL = 0x0302;
	public static final short ATTR_SUPPORTED_FORMATS_LIST = 0x0303;
	public static final short ATTR_SECURITY_DESC = 0x030A;
	public static final short ATTR_NET_ACCESS_TYPE = 0x030B;
	public static final short ATTR_MAX_NET_ACCESSRATE = 0x030C;
	public static final short ATTR_IP4_SUBNET = 0x030D;
	public static final short ATTR_IP6_SUBNET = 0x030E;
	public static final short ATTR_SUPPORTED_FEATURES = 0x0311;
	public static final short ATTR_SUPPORTED_FUNCTION = 0x0312;
	public static final short ATTR_TOTAL_IMAGING_DATA_CAPACITY = 0x0313;

	private static final short LanguageBaseAttributeIDList = 0x0100;

	public static final short ATTR_SVCNAME = 0x0000 + LanguageBaseAttributeIDList;
	public static final short ATTR_SVCDESC = 0x0001 + LanguageBaseAttributeIDList;
	public static final short ATTR_PROVNAME_PRIMARY = 0x0002 + LanguageBaseAttributeIDList;
}