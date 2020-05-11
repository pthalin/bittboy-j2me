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
// Desc: Client library in C to access  player_driver_camerav4l2 interface
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
///////////////////////////////////////////////////////////////////////////
#ifndef __PLAYERV2L2C__H_
#define __PLAYERV2L2C__H_

#include <playerc.h>
/** @file playerv4l2c.h

@brief Client library in C to access  @p player_driver_camerav4l2 interface

This set of functions wraps request interface to camerav4l2 driver.

@par Authors

Marcin Rudowski


 */


/** @addtogroup libraries Client level libraries */
/** @{ */
/** @defgroup playerv4l2c c library
@{

Client library in C, to access some additional features of @ref player_driver_camerav4l2
driver. It uses Request interface to pass parameters and get values. Most
functions have dual versions, one with predefined constants and second with
string literal. This allow to easilly extend supported modes by modyfing driver
only and passing new modes by string value;
String parameters are the same as described in driver confuguration description.

@par Compile-time dependencies

- &lt;playerc.h&gt;

@par Authors

Marcin Rudowski

 */
/** @defgroup playerv4l2c_core Core functionality */
/** @{ */



#ifndef __PLAYERV2L2__H_

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

/** @brief Selects input source by id */
int playerv4l2c_camera_selectSourceI(playerc_camera_t *device, int src_id);

/** @brief Selects input source by name */
int playerv4l2c_camera_selectSourceS(playerc_camera_t *device, const char *src_name);

/** @brief Sets new capture frame size */
int playerv4l2c_camera_setFrameSize(playerc_camera_t *device, int w, int h);



/** @brief Selects norm (pal, ntsc, secam), */
int playerv4l2c_camera_setNormI(playerc_camera_t *device, int normID);

/** @brief Selects norm (pal, ntsc, secam), */
int playerv4l2c_camera_setNormS(playerc_camera_t *device, const char * normID);

/** @brief Selects field interleaving method */
int playerv4l2c_camera_setFieldTypeI(playerc_camera_t *device, int fielsType);

/** @brief Selects field interleaving method */
int playerv4l2c_camera_setFieldTypeS(playerc_camera_t *device, const char * fielsType);

/** @brief Selects pixel format (RGB, Gray, ...) */
int playerv4l2c_camera_setPixelFormatI(playerc_camera_t *device, int pixFormat);

/** @brief Selects pixel format (RGB, Gray, ...) */
int playerv4l2c_camera_setPixelFormatS(playerc_camera_t *device, const char * pixFormat);

/** @brief for RGB/BGR formats, if swap!=0, then swap Red and Blue */
int playerv4l2c_camera_setSwapRB(playerc_camera_t *device, int swap);



/** @brief get number of supported controls */
int playerv4l2c_camera_countControls(playerc_camera_t *device );

/** @brief set new value for control. Value is range 0.0 - 1.0.
    Controll is identified by internal number in table of availabel controls
    for selected source. It should be in range 0..playerv4l2c_camera_countControls()-1
*/
int playerv4l2c_camera_setControlValueI(playerc_camera_t *device, int id, double value);

/** @brief set new value for control. Value is range 0.0 - 1.0.
    Controll is identified by name ("brighteness", "exposure"). Available controls
    depends on v4l2 device capabilities (refer to driver documentation or logs in player server)
*/
int playerv4l2c_camera_setControlValueS(playerc_camera_t *device, const char *name, double value);

/** @brief returns current control value in range 0-1.
    values below 0, means error while querying driver */
double playerv4l2c_camera_getControlValueI(playerc_camera_t *device, int id);

/** @brief returns current control value in range 0-1.
    values below 0, means error while querying driver */
double playerv4l2c_camera_getControlValueS(playerc_camera_t *device, const char *name);



/// @brief sets brighteness value in range 0-1
int playerv4l2c_camera_setBrighteness(playerc_camera_t *device, double value);

/// @brief gets current brighteness value
double playerv4l2c_camera_getBrighteness(playerc_camera_t *device);

/// @brief sets saturation value in range 0-1
int playerv4l2c_camera_setSaturation(playerc_camera_t *device, double value);

/// @brief gets current saturation value
double playerv4l2c_camera_getSaturation(playerc_camera_t *device);

/// @brief sets contrast value in range 0-1
int playerv4l2c_camera_setContrast(playerc_camera_t *device, double value);

/// @brief gets current contrast value
double playerv4l2c_camera_getContrast(playerc_camera_t *device);

/// @brief sets hue value in range 0-1
int playerv4l2c_camera_setHue(playerc_camera_t *device, double value);

/// @brief gets current hue value
double playerv4l2c_camera_getHue(playerc_camera_t *device);



/** @brief retrieves current input name */
const char * playerv4l2c_camera_getInputName(playerc_camera_t *device);

/** @brief get device name */
const char * playerv4l2c_camera_getName(playerc_camera_t *device);



/** @brief get current field settings */
const char * playerv4l2c_camera_getFieldS(playerc_camera_t *device);

/** @brief get current field settings */
int playerv4l2c_camera_getFieldI(playerc_camera_t *device);

/** @brief get current pixel format */
const char * playerv4l2c_camera_getPixelFormatS(playerc_camera_t *device);

/** @brief get current field settings */
int playerv4l2c_camera_getPixelFormatI(playerc_camera_t *device);

/** @} */
/** @} */
/** @} */

#endif

