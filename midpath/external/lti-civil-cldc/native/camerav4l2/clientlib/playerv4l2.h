/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
///////////////////////////////////////////////////////////////////////////
//
// Desc: Client library in C++ to access player_driver_camerav4l2 interface
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
///////////////////////////////////////////////////////////////////////////
#ifndef __PLAYERV2L2__H_
#define __PLAYERV2L2__H_

#include <playerclient.h>

/** @file playerv4l2.h

@brief Client library in C++ to access  @p player_driver_camerav4l2 interface

Class @p CameraV4L2Proxy wraps request interface to camerav4l2 driver.

@par Authors

Marcin Rudowski


 */


/** @addtogroup libraries Client level libraries */
/** @{ */
/** @defgroup playerv4l2 c++ library
@{
Client library in C++, to access some additional features of @p player_driver_camerav4l2
driver. It uses Request interface to pass parameters and get values. Most
functions have dual versions, one with predefined constants and second with
string literal. This allow to easilly extend supported modes by modyfing driver
only and passing new modes by string value;
String parameters are the same as described in driver confuguration description.

It is implemented as @p CameraV4L2Proxy, subclass of standard @p CameraProxy class.

@par Compile-time dependencies

- &lt;playerc.h&gt;

@par Authors

Marcin Rudowski

 */
/** @defgroup playerv4l2_core Core functionality */
/** @{ */


#ifndef __PLAYERV2L2C__H_

/// @brief list of known field interleaving modes
enum FieldTypes {
    PLAYER_V4L2_FIELD_ANY=0,
    PLAYER_V4L2_FIELD_NONE,
    PLAYER_V4L2_FIELD_TOP,
    PLAYER_V4L2_FIELD_BOTTOM,
    PLAYER_V4L2_FIELD_INTERLACED,
    PLAYER_V4L2_FIELD_SEQ_TB,
    PLAYER_V4L2_FIELD_SEQ_BT,
    PLAYER_V4L2_FIELD_ALTERNATE
};

/// @brief list of known video norms (pal/secam/ntsc)
enum NormStandards {
    PLAYER_V4L2_STD_PAL=0,
    PLAYER_V4L2_STD_NTSC,
    PLAYER_V4L2_STD_SECAM,
    PLAYER_V4L2_STD_OTHER=-1
};

/// @brief list of known pixel formats
enum PixelFormats {
    PLAYER_V4L2_FMT_RGB24=0,
    PLAYER_V4L2_FMT_RGB32,
    PLAYER_V4L2_FMT_BGR24,
    PLAYER_V4L2_FMT_BGR32,
    PLAYER_V4L2_FMT_GREY,
    PLAYER_V4L2_FMT_RGB565,
    PLAYER_V4L2_FMT_SBGGR8,
    PLAYER_V4L2_FMT_RGB332,
    PLAYER_V4L2_FMT_RGB555,
    PLAYER_V4L2_FMT_RGB555X,
    PLAYER_V4L2_FMT_RGB565X,
    PLAYER_V4L2_FMT_YVU410,
    PLAYER_V4L2_FMT_YVU420,
    PLAYER_V4L2_FMT_YUYV,
    PLAYER_V4L2_FMT_UYVY,
    PLAYER_V4L2_FMT_YUV422P,
    PLAYER_V4L2_FMT_YUV411P,
    PLAYER_V4L2_FMT_Y41P,
    PLAYER_V4L2_FMT_NV12,
    PLAYER_V4L2_FMT_NV21,
    PLAYER_V4L2_FMT_OTHER=-1
};
#endif

/**

The @p CameraV4L2Proxy class can be used to get images from a @ref
player_interface_camera device. It adds additional metods to @p CameraProxy
that use camerav4l2 driver capabilities */
class CameraV4L2Proxy : public CameraProxy
{

    public:
        /// PLAYER_V4L2_FMT_RGB*, PLAYER_V4L2_FMT_BGR*, ...
        PixelFormats pixelFormat;

        /// "RGB*", "BGR*", "GREY", ...
        char pixelFormatS[64];

        /// !=0 means swap R and B in RGB/BGR 24/32 formats
        int swapRBFlag;

        /// no of avaible controls
        int noOfControls;

        /// tv norm (pal/secam/ntsc)
        NormStandards norm;

        /// tv norm in char * form (usefull for some specific norms, like pal_dk)
        char normS[64];

        /// field interleaving type
        FieldTypes field;

        /// field interleaving type
        char fieldS[64];

        /// name of capture card
        char name[64];

        /// name of current input
        char inputName[64];

        /// common controls proxy values
        double brighteness;
        double contrast;
        double saturation;
        double hue;

        // Constructor.  Leave the access field empty to start unconnected.
        CameraV4L2Proxy (PlayerClient *pc, unsigned short index,
                     unsigned char access='c');

        virtual ~CameraV4L2Proxy(){
        }

        // interface that all proxies must provide
        void FillData(player_msghdr_t hdr, const char* buffer);

        /** @brief Selects input source by id */
        int selectSourceI(int src_id);

        /** @brief Selects input source by name */
        int selectSourceS(const char *src_name);

        /** @brief Sets new capture frame size */
        int setFrameSize(int w, int h);

        /** @brief Selects norm (pal, ntsc, secam), */
        int setNormI(int normID);

        /** @brief Selects norm (pal, ntsc, secam), */
        int setNormS(const char *normID);

        /** @brief Selects field interleaving method */
        int setFieldTypeI(int fielsType);

        /** @brief Selects field interleaving method */
        int setFieldTypeS(const char * fielsType);

        /** @brief Selects pixel format (RGB, Gray, ...) */
        int setPixelFormatI(int pixFormat);

        /** @brief Selects pixel format (RGB, Gray, ...) */
        int setPixelFormatS(const char * pixFormat);

        /** @brief for RGB/BGR formats, if swap!=0, then swap Red and Blue */
        int setSwapRB(int swap);

        /** @brief set new value for control. Value is range 0.0 - 1.0.
        Controll is identified by internal number in table of availabel controls
        for selected source. It should be in range 0..countControls()-1
        */
        int setControlValueI(int id, double value);

        /** @brief set new value for control. Value is range 0.0 - 1.0.
        Controll is identified by name ("brighteness", "exposure"). Available controls
        depends on v4l2 device capabilities (refer to driver documentation or logs in player server)
        */
        int setControlValueS(const char *name, double value);

        /** @brief returns current control value in range 0-1. values below 0, means error while querying driver */
        double getControlValueI(int id);

        /** @brief returns current control value in range 0-1. values below 0, means error while querying driver */
        double getControlValueS(const char *name);

        /** @brief get name of given control id. id is 0..countControls()-1 */
        const char * getControlName(int id);

        /// set brighteness value
        int setBrighteness(double value);
        /// get actual brighteness (if changed externally)
        double getBrighteness();

        int setSaturation(double value);
        /// get actual saturation (if changed externally)
        double getSaturation();

        int setContrast(double value);
        /// get actual contrast (if changed externally)
        double getContrast();

        int setHue(double value);
        /// get actual hue (if changed externally)
        double getHue();


    private:
        /// @brief refresh public attributes except controls (Hue/Brighteness/..)
        void refreshState();

        /** @brief retrieves current input name */
        const char * getInputName();

        /// @brief retrieves device name
        const char * getName();

        /** @brief get current field settings */
        const char * getFieldS();

        /** @brief get current pixel format */
        const char * getPixelFormatS();

        /// @brief get norm setting (pal etc.)
        const char * getNormS();

        /** @brief  number of avaible controls */
        int countControls();
};

/** @} */
/** @} */
/** @} */


#endif

