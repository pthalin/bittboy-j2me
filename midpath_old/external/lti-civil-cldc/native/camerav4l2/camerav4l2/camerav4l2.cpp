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
// Desc: Video for Linux 2 capture driver
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
///////////////////////////////////////////////////////////////////////////

/** @addtogroup drivers Drivers */
/** @{ */
/** @defgroup player_driver_camerav4l2 camerav4l2


The camerav4l2 driver captures images from V4l2-compatible cameras. It uses
MMAP capture method.

@par Compile-time dependencies

- &lt;linux/videodev2.h&gt;

@par Provides

- @ref player_interface_camera

@par Requires

- none known

@par Configuration requests

Requests are passed in text format (ctype strings: char *, ended whith '\0').
Parameters are passed in form readible by scanf("%f") or scanf("%d).
There could be only one space between command name and parameters.
This interface is not intend to be used by application. This should be
wrapped by proper ClientProxy subclass.

Below are examples of all requests:
- 's 640 480'
    change size of video capture, takes 2 integer arguments
- 'i 2' (or 'i USB')
    change input source, takes one String/Integer argument (look for config file expl)
- 'n pal'
    change video norm, takes one String argument (look for config file expl)
- 'm RGB24'
    change video mode, takes one String argument (look for config expl)
- 'f INTERLEACED'
    selects type of field interleaving, takes one String argument (look for config file expl)
- 'w 1'
    changes swap_rb setting, takes one Integer argument (look for config file expl)
- 'c 0.5 brightenes
    changes control value. Takes two arguments.first Double (new Value in range 0-1) and rest after one space
    is name of control. Order is reversed when compared to config file, to easily parse command
- 'ci 0.5 2
    changes control value. Takes two arguments.first Double (new Value in range 0-1) and next is Integer
    and points to position in actual control list ( depends on device and source )

Requests, that returns some reply. Size of reply depends on size of passed strings (names of device or controls).
- 'g' - get device name; after g, there should be end of string: '\0';
        (ex. "AverTV Studio 303 (M126)")
- 'gc' - get actual number of controls (reply is like result of printf("%d", num)
- 'gc name' - get value of given control
- 'gci ID' - get value of given control identified by id in range 0..max_id-1, where
  max_id is returned by 'gc' request. Value is in range 0.0 - 1.0, and is result of
  printf("%f", val) command. Value <0 mean error.
- 'gcn ID' - get given control name (ID is like in gci request).
- 'gf' - get field setting (example reply content: "ANY")
- 'gn' - get norm setting (ex. "PAL")
- 'gm' - get pixel format setting (ex. "RGB24")
- 'gi' - get source input name (ex. "S-Video"). This name can be used later with 'i' command

@par Configuration file options

All Strings except dev_file are case insensitive. All options added in this module
aren't mendatory and have default value.

- dev_file (string)
  - Default: "/dev/video0"
  - Device to read video data from.

- input (integer/string)
  - Default: 0
  - Some capture cards have multiple input sources; use this field to
    select which one is used. Can be name of source (list is printed on opening device).

- norm (string)
  - Default: "pal"
  - Capture format; "ntsc" or "pal"

- size (integer tuple)
  - Default: varies with norm
  - Desired image size.   This may not be honoured if the driver does
    not support the requested size).

- mode (string)
  - Default: "RGB24"
  - Desired capture mode.  Can be one of:
    - "RGB24" - 24  RGB-8-8-8
    - "RGB32" - 32  RGB-8-8-8-8
    - "BGR24" - 24  BGR-8-8-8
    - "BGR32" - 32  BGR-8-8-8-8
    - "GREY" - 8  Greyscale
    - "RGB565" - 16  RGB-5-6-5
    - "SBGGR8" - see http://www.siliconimaging.com/RGB%20Bayer.htm
    - "RGB332" - 8  RGB-3-3-2
    - "RGB555" - 16  RGB-5-5-5
    - "RGB555X" - 16  RGB-5-5-5 BE
    - "RGB565X" - 16  RGB-5-6-5 BE
    - "YVU410" - 9  YVU 4:1:0
    - "YVU420" - 12  YVU 4:2:0
    - "YUYV" - 16  YUV 4:2:2
    - "UYVY" - 16  YUV 4:2:2
    - "YUV422P" - 16  YVU422 planar
    - "YUV411P" - 16  YVU411 planar
    - "Y41P" - 12  YUV 4:1:1
    - "NV12" - 12  Y/CbCr 4:2:0
    - "NV21" - 12  Y/CrCb 4:2:0
  - Note that not all capture modes are supported by Player's internal image
    format; in these modes, images will be translated to the closest matching
    internal format (ex., RGB32 -> RGB888).
  - Depth set in player frame is rounded to full byte (ex. 12b in v4l2 -> 16b in player).

- swap_rb (integer)
  - default 0 (no flip)
  - value !=0 means that in RGB/BGR (24/32 only) formats, R and B should be swapped

- field (string)
  - Default: "ANY"
  - Desired field interleaving type.  Can be one of:
    - ANY
    - NONE
    - TOP
    - BOTTOM
    - INTERLEACED
    - SEQ_TB
    - SEQ_BT
    - ALTERNATE
  - Search for explanation of each type in v4l2 spec.

- controls ( String + Double tuple )
  - Driver specific (default: last used values)
  - List of pairs: Control_name(String) Control_value(Double)
  of v4l2 driver specific names. Values are in range 0-1. Below 0, means
  use driver default.
  - control names and ranges for values are driver specific
  but are printed of opening device. Non existing controls will
  be ignored. Control values are applied on each device open.
  According to v4l2, each source can have different control set.
  Not specyfing control, means to leave it without change on open.

- show_fps (integer)
  - default -1 (disable)
  - selects level of debug (-d lev when starting player). Value should be
    in range -1..9
    on selected debuglevel, stats about frames per second will be printed
    once per 2s.

Note that some of these options may not be honoured by the underlying
V4L2 kernel driver (it may not support a given image size, for
example).

@par Mini Example

@verbatim
driver
(
  name "camerav4l2"
  plugin "camerav4l2.so"
  provides ["camera:0"]
)
@endverbatim


@par Full example


@verbatim
driver
(
  name "camerav4l2"
  plugin "camerav4l2.so"
  provides ["camera:0"]

  dev_file "/dev/video0"
  input 0
  size [160 120]
  mode "RGB24"
  show_fps 2
  controls [
      "red balance" 0.0667
      "blue balance" 0.3333
      "exposure" 0.0
      "global gain" 0.2903
      "DAC magnitude" 0.501
      "green balance" 0.1333
      "exposure2" 0.0328
      "iso" 0.0
    ]
    field "any"
    swap_rb 0
)
@endverbatim


@par Authors

Marcin Rudowski

 */
/** @} */




/*! \mainpage Camerav4l2 - Player module
 *
 * @author Marcin Rudowski
 *
 * This is a module to Player server that allows capturing from V4L2 compatible device.
 * It is compatible with existing @p player_interface_camera interface, allowing
 * to use new framegrabbers with existing code. Beside that, new features were
 * added (see @ref player_driver_camerav4l2 for full usage description):
 * \li rich configuration with player *.cfg file
 * \li wide range of pixel format modes (all supported by v4l2 device driver). Image
 *      data are passed without any convertion, and new modes need client handling.
 * \li all settings except device path can be changed while usage, without need to
 *      restart Player server with new config file. This can be done by apropriate @ref libraries
 * \li support for selecting field interleaving type and video norm (pal/secam/ntsc)
 * \li reduced server cpu usage by minimizing copying and conversions. Only raw data in format
       generated by v4l2 driver is passed to client. Any conversions (ex. YUV->RGB) can
       be done outside capture module, in new module or in client application.
 *
 * New functions to control capture parameters are accessible by @ref libraries
 * in C and C++. C++ one extends existing CameraProxy with new methods and attributes
 * to take benefit of driver abilities.
 *
 * Note, that only some @ref PixelFormats modes are compatible with existing @p player_interface_camera
 * interface. When using new types, please use additional functions of client libraries.
 * Pixel format values passed with frame are set to be compatible with existing
 * interface and doesn't hide new modes.
 *
 * Usefull links:
 * \li camerav4l2 module and libraries sources: http://home.elka.pw.edu.pl/~mrudows1/camerav4l2/camerav4l2.tgz
 * \li Player server manual: http://playerstage.sourceforge.net/doc/Player-1.6.5/player-html/
 * \li V4L2 specification: http://www.linuxtv.org/downloads/video4linux/API/V4L2_API/
 *
 */



#include <player.h>

#include <errno.h>
#include <string.h>
#include <math.h>
//#include <stdlib.h>       // for atoi(3)
#include <netinet/in.h>   // for htons(3)
#include <unistd.h>

#include <player/driver.h>
#include <player/devicetable.h>
#include <player/drivertable.h>
#include <player/playertime.h>
#include <player/error.h>

#include "v4l2capture.h"  // help for capturing and accessind device

// Time for timestamps
extern PlayerTime *GlobalTime;


/// @brief Driver for capturing frames from V4L2 compatible device
class CameraV4L2 : public Driver
{
    /// @brief Constructor
    public: CameraV4L2( ConfigFile* cf, int section);

    /// @brief Setup/shutdown routines.
    public: virtual int Setup();
    /// @brief Setup/shutdown routines.
    public: virtual int Shutdown();

    /// @brief Main function for device thread.
    private: virtual void Main();

    /// @brief Process requests.
    private: int HandleRequests();

    /// @brief Update the device data (the data going back to the client).
    private: void WriteData();

    /// @brief Video device
    private: const char *device;

    /// @brief Input source. If sourceCh==0, then source matters
    private: const char *sourceCh;
    private: int source;

    /// @brief The signal norm: VIDEO_NORM_PAL or VIDEO_NORM_NTSC.
    private: v4l2_std_id norm;

    /// @brief Pixel depth
    private: int depth;

    /// @brief Image dimensions
    private: int width, height;

    /// @brief field type
    enum v4l2_field fieldType;

    unsigned int v4l2_type_id;

    int flip_rb;

    int showFPS;

    /// @brief Frame grabber interface
    private: FRAMEGRABBER2* fg;

    /// @brief Capture timestamp
    private: uint32_t tsec, tusec;

    /// @brief Data to send to server
    private: player_camera_data_t data;


    private:
        /// @brief some parsing functions, to change string to apropriate settings
        int selectNorm(const char *snorm);
        int selectFormat(const char *sformat);
        int selectField(const char *sfield);

        int numOfCtls;
        const char **ctlNames;
        double *ctlVals;

        int tryPixelSettings(FRAMEGRABBER2* fg, int w, int h, unsigned int fmt, enum v4l2_field fld);

        /// @brief handle command with optional reply
        int handleCommand(char *cmd, int len);
        int handleCommand(char *cmd, int len, char *reply, int rlen);

        /// @brief backup for rollback, when failed to set new parameters
        int back_source;
        v4l2_std_id back_norm;
        int back_depth;
        int back_width;
        int back_height;
        enum v4l2_field back_fieldType;
        unsigned int back_v4l2_type_id;
};


/// @brief Initialization function
Driver* CameraV4L2_Init( ConfigFile* cf, int section)
{
    return ((Driver*) (new CameraV4L2( cf, section)));
}


/// @brief Driver registration function
void CameraV4L2_Register(DriverTable* table)
{
    table->AddDriver("camerav4l2", CameraV4L2_Init);
}


////////////////////////////////////////////////////////////////////////////////
// Extra stuff for building a shared object.

/* need the extern to avoid C++ name-mangling  */
extern "C" {
    int player_driver_init(DriverTable* table)
    {
        puts("Camerav4l2: Registering driver");
        CameraV4L2_Register(table);
        puts("Camerav4l2 driver registered");
        return(0);
    }
}



////////////////////////////////////////////////////////////////////////////////
/// @brief list of all known norms (pal/secam/ntsc/...)
/// Add new norms here (ex pal_dk, ...)
struct NormsList {
    const char *name;
    v4l2_std_id id;
    int width;
    int height;
} normy[] = {
    { // first is default
        "PAL",
        V4L2_STD_PAL,
        768,
        575,
    },{
        "NTSC",
        V4L2_STD_NTSC,
        640,
        480,
    },{
        "SECAM",
        V4L2_STD_SECAM,
        640,//??
        480,
    },{
        0, 0, 0, 0// do not delete this
    }
};

////////////////////////////////////////////////////////////////////////////////
/// @brief list of all known file formats.
/// Add new formats here (if only v4l2 device supports it)
struct FormatsList {
    const char *name;
    unsigned int player_type_id;
    unsigned int v4l2_type_id;
    unsigned int bits;
} formaty[] = {
    { /* 24  RGB-8-8-8     */
        // first and default value
        "RGB24",                        //ffname
        PLAYER_CAMERA_FORMAT_RGB888,    //player_type_id
        V4L2_PIX_FMT_RGB24,             //v4l2_type_id
        24,                             //bits
    },{/* 32  RGB-8-8-8-8   */
        "RGB32",                         //ffname
        PLAYER_CAMERA_FORMAT_RGB888,     //player_type_id
        V4L2_PIX_FMT_RGB32,              //v4l2_type_id
        32,                              //bits
    },{/* 24  BGR-8-8-8     */
        "BGR24",                        //ffname
        PLAYER_CAMERA_FORMAT_RGB888,    //player_type_id
        V4L2_PIX_FMT_BGR24,             //v4l2_type_id
        24,                             //bits
    },{/* 32  BGR-8-8-8-8   */
        "BGR32",                         //ffname
        PLAYER_CAMERA_FORMAT_RGB888,     //player_type_id
        V4L2_PIX_FMT_BGR32,              //v4l2_type_id
        32,                              //bits
    },{/*  8  Greyscale     */
        "GREY",                         //ffname
        PLAYER_CAMERA_FORMAT_MONO8,     //player_type_id
        V4L2_PIX_FMT_GREY,              //v4l2_type_id
        8,                              //bits
    },{/* 16  RGB-5-6-5     */
        "RGB565",                        //ffname
        PLAYER_CAMERA_FORMAT_RGB565,     //player_type_id
        V4L2_PIX_FMT_RGB565,             //v4l2_type_id
        16,                              //bits
    },{/* see http://www.siliconimaging.com/RGB%20Bayer.htm */
        "SBGGR8",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO8,     //player_type_id
        V4L2_PIX_FMT_SBGGR8,             //v4l2_type_id
        8,                              //bits
    },{/*  8  RGB-3-3-2     */
        "RGB332",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO8,     //player_type_id
        V4L2_PIX_FMT_RGB332,             //v4l2_type_id
        8,                              //bits
    },{/* 16  RGB-5-5-5     */
        "RGB555",                        //ffname
        PLAYER_CAMERA_FORMAT_RGB565,     //player_type_id
        V4L2_PIX_FMT_RGB555,             //v4l2_type_id
        16,                              //bits
    },{/* 16  RGB-5-5-5 BE  */
        "RGB555X",                        //ffname
        PLAYER_CAMERA_FORMAT_RGB565,     //player_type_id
        V4L2_PIX_FMT_RGB555X,             //v4l2_type_id
        16,                              //bits
    },{/* 16  RGB-5-6-5 BE  */
        "RGB565X",                        //ffname
        PLAYER_CAMERA_FORMAT_RGB565,     //player_type_id
        V4L2_PIX_FMT_RGB565X,             //v4l2_type_id
        16,                              //bits
    },{/*  9  YVU 4:1:0     */
        "YVU410",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_YVU410,             //v4l2_type_id
        16,                              //bits
    },{/* 12  YVU 4:2:0     */
        "YVU420",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_YVU420,             //v4l2_type_id
        16,                              //bits
    },{/* 16  YUV 4:2:2     */
        "YUYV",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_YUYV,             //v4l2_type_id
        16,                              //bits
    },{/* 16  YUV 4:2:2     */
        "UYVY",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_UYVY,             //v4l2_type_id
        16,                              //bits
    },{/* 16  YVU422 planar */
        "YUV422P",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_YUV422P,             //v4l2_type_id
        16,                              //bits
    },{/* 16  YVU411 planar */
        "YUV411P",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_YUV411P,             //v4l2_type_id
        16,                              //bits
    },{/* 12  YUV 4:1:1     */
        "Y41P",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_Y41P,             //v4l2_type_id
        16,                              //bits
    },{/* 12  Y/CbCr 4:2:0  */
        "NV12",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_NV12,             //v4l2_type_id
        16,                              //bits
    },{/* 12  Y/CrCb 4:2:0  */
        "NV21",                        //ffname
        PLAYER_CAMERA_FORMAT_MONO16,     //player_type_id
        V4L2_PIX_FMT_NV21,             //v4l2_type_id
        16,                              //bits
    },{
//        "",                        //ffname
//        PLAYER_CAMERA_FORMAT_???,     //player_type_id
//        V4L2_PIX_FMT_???,             //v4l2_type_id
//        ???,                              //bits
//    },{
        0,0,0,0// do not delete this
    }
//           from player.h
// /** Image format : 8-bit monochrome. */
// #define PLAYER_CAMERA_FORMAT_MONO8  1
// /** Image format : 16-bit monochrome (network byte order). */
// #define PLAYER_CAMERA_FORMAT_MONO16 2
// /** Image format : 16-bit color (5 bits R, 6 bits G, 5 bits B). */
// #define PLAYER_CAMERA_FORMAT_RGB565 4
// /** Image format : 24-bit color (8 bits R, 8 bits G, 8 bits B). */
// #define PLAYER_CAMERA_FORMAT_RGB888 5


//           from videodev2.h
// #define V4L2_PIX_FMT_RGB332  v4l2_fourcc('R','G','B','1')
// #define V4L2_PIX_FMT_RGB555  v4l2_fourcc('R','G','B','O')
// #define V4L2_PIX_FMT_RGB565  v4l2_fourcc('R','G','B','P')
// #define V4L2_PIX_FMT_RGB555X v4l2_fourcc('R','G','B','Q')
// #define V4L2_PIX_FMT_RGB565X v4l2_fourcc('R','G','B','R')
// #define V4L2_PIX_FMT_BGR24   v4l2_fourcc('B','G','R','3')
// #define V4L2_PIX_FMT_RGB24   v4l2_fourcc('R','G','B','3')
// #define V4L2_PIX_FMT_BGR32   v4l2_fourcc('B','G','R','4')
// #define V4L2_PIX_FMT_RGB32   v4l2_fourcc('R','G','B','4')
// #define V4L2_PIX_FMT_GREY    v4l2_fourcc('G','R','E','Y')
// #define V4L2_PIX_FMT_YVU410  v4l2_fourcc('Y','V','U','9')
// #define V4L2_PIX_FMT_YVU420  v4l2_fourcc('Y','V','1','2')
// #define V4L2_PIX_FMT_YUYV    v4l2_fourcc('Y','U','Y','V')
// #define V4L2_PIX_FMT_UYVY    v4l2_fourcc('U','Y','V','Y')
// #define V4L2_PIX_FMT_YUV422P v4l2_fourcc('4','2','2','P')
// #define V4L2_PIX_FMT_YUV411P v4l2_fourcc('4','1','1','P')
// #define V4L2_PIX_FMT_Y41P    v4l2_fourcc('Y','4','1','P')
//
// /* two planes -- one Y, one Cr + Cb interleaved  */
// #define V4L2_PIX_FMT_NV12    v4l2_fourcc('N','V','1','2')
// #define V4L2_PIX_FMT_NV21    v4l2_fourcc('N','V','2','1')
//
// /*  The following formats are not defined in the V4L2 specification */
// #define V4L2_PIX_FMT_YUV410  v4l2_fourcc('Y','U','V','9') /*  9  YUV 4:1:0     */
// #define V4L2_PIX_FMT_YUV420  v4l2_fourcc('Y','U','1','2') /* 12  YUV 4:2:0     */
// #define V4L2_PIX_FMT_YYUV    v4l2_fourcc('Y','Y','U','V') /* 16  YUV 4:2:2     */
// #define V4L2_PIX_FMT_HI240   v4l2_fourcc('H','I','2','4') /*  8  8-bit color   */
};

////////////////////////////////////////////////////////////////////////////////
/// @brief list of all known fields interleaving types
struct FieldsList {
    const char *name;
    enum v4l2_field fieldType;
} fieldy[] = {
    { // first is default
        "ANY",
        V4L2_FIELD_ANY,
    },{
        "NONE",
        V4L2_FIELD_NONE,
    },{
        "TOP",
        V4L2_FIELD_TOP,
    },{
        "BOTTOM",
        V4L2_FIELD_BOTTOM,
    },{
        "INTERLEACED",
        V4L2_FIELD_INTERLACED,
    },{
        "SEQ_TB",
        V4L2_FIELD_SEQ_TB,
    },{
        "SEQ_BT",
        V4L2_FIELD_SEQ_BT,
    },{
        "ALTERNATE",
        V4L2_FIELD_ALTERNATE,
    },{
        0,V4L2_FIELD_NONE// do not delete this
    }
};


/**
 * Apply setting according to snorm value read from config or request;
 * changed are: this->norm, this->width, this->height
 * @param snorm one of values mentioned in config for "norm" setting
 * @return 0 on success
 */
int CameraV4L2::selectNorm(const char *snorm){
    int i=0;
    for(i=0; normy[i].name; i++)
        if (strcasecmp(snorm, normy[i].name) == 0)
            break;

    if (normy[i].name){
        this->norm = normy[i].id;
        this->width = normy[i].width;
        this->height = normy[i].height;
    } else {
        PLAYER_ERROR2("norm: %s is not supported (add it yourself to normy struct in %s)",
                      snorm, __FILE__);
        return -1;
    }
    return 0;
}

/**
 * Apply setting according to mode value from config or request;
 * changed are: this->data.format, this->depth, this->v4l2_type_id
 * @param sformat one of values mentioned in config for "mode" setting
 * @return 0 on success
 */
int CameraV4L2::selectFormat(const char *sformat){
    int i;
    for(i=0; formaty[i].name; i++)
        if (strcasecmp(sformat, formaty[i].name) == 0)
            break;

    if (formaty[i].name){
        this->data.format = formaty[i].player_type_id;
        this->depth = formaty[i].bits;
        this->v4l2_type_id = formaty[i].v4l2_type_id;
    } else {
        PLAYER_ERROR2("image pallete %s is not supported (add it yourself to formaty struct in %s)",
                      sformat, __FILE__);
        return -1;
    }
    return 0;
}

/**
 * Apply setting according to field value read from config or request;
 * changed are: this->fieldType
 * @param sfield one of values mentioned in config for "field" setting
 * @return 0 on success
 */
int CameraV4L2::selectField(const char *sfield){
    int i;
    for(i=0; fieldy[i].name; i++)
        if (strcasecmp(sfield, fieldy[i].name) == 0)
            break;
    if (fieldy[i].name){
        this->fieldType = fieldy[i].fieldType;
    } else {
        PLAYER_ERROR2("Field type %s is not supported",
                      sfield, __FILE__);
        return -1;
    }
    return 0;
}

////////////////////////////////////////////////////////////////////////////////
// Constructor
CameraV4L2::CameraV4L2( ConfigFile* cf, int section)
    : Driver(cf, section, PLAYER_CAMERA_CODE, PLAYER_ALL_MODE,
             sizeof(player_camera_data_t), 128, 10, 10)
{
    showFPS = 0;
    sourceCh = 0;
    const char *schary;

  // Camera defaults to /dev/video0 and NTSC
    this->device = cf->ReadString(section, "dev_file", "/dev/video0");

  // Input source
    this->sourceCh = NULL;
    schary = cf->ReadString(section, "input", NULL);
    if (schary!=NULL && schary[0]>='0' && schary[0]<='9')
        this->source = cf->ReadInt(section, "input", 0);
    else {
        this->source = -1;
        this->sourceCh = schary;
    }

  // NTSC or PAL
    schary = cf->ReadString(section, "norm", "pal");
    if (selectNorm(schary)!=0)
        selectNorm(normy[0].name);

  // Size
    this->width = cf->ReadTupleInt(section, "size", 0, this->width);
    this->height = cf->ReadTupleInt(section, "size", 1, this->height);

  // Palette type
    schary = cf->ReadString(section, "mode", "RGB24");
    if (selectFormat(schary)!=0)
        selectFormat(formaty[0].name);

    schary = cf->ReadString(section, "field", "ANY");
    if (selectField(schary)!=0)
        selectField(fieldy[0].name);

    flip_rb = cf->ReadInt(section, "swap_rb", 0);

    showFPS = cf->ReadInt(section, "show_fps", -1);
    if (showFPS>9)
        showFPS = 9;

    //printf("#controls: %d", cf->GetTupleCount(section, "controls") );
    numOfCtls = cf->GetTupleCount(section, "controls")/2;
    if (numOfCtls>0){
        int i;
        double k=0;
        ctlNames = new const char*[numOfCtls];
        ctlVals = new double[numOfCtls];
        for(i=0; i<numOfCtls; i++){
            ctlNames[i] = cf->ReadTupleString(section, "controls", i*2+1, "null");
            k = cf->ReadTupleFloat(section, "controls", i*2, 200.0);
            if (k>1.0){
                numOfCtls = i;
                PLAYER_WARN1("Wrong value format for control %s, need val <= 1.0", ctlNames[i]);
            } else {
                ctlVals[i]=(double)k;//??
            }
        }
    }

    back_source = source;
    back_norm = norm;
    back_depth = depth;
    back_width = width;
    back_height = height;
    back_fieldType = fieldType;
    back_v4l2_type_id = v4l2_type_id;

    puts("Camerav4l2: Driver object created");

    return;
}


////////////////////////////////////////////////////////////////////////////////
// Set up the device (called by server thread).
int CameraV4L2::Setup()
{
    int err=0;
    int i;

    source = back_source;
    norm = back_norm;
    depth = back_depth;
    width = back_width;
    height = back_height;
    fieldType = back_fieldType;
    v4l2_type_id = back_v4l2_type_id;

    PLAYER_MSG1(0,"Camerav4l2: Setting up device %s", this->device);
    this->fg = fg2_createFrameGrabber();
    // set initial settings
    if (fg2_open(this->fg, this->device)!=0)
    {
        PLAYER_ERROR1("unable to open %s", this->device);
        fg2_delete(&fg);
        return -1;
    }

    PLAYER_MSG0(1,"Camerav4l2: device opened, aplying settings");

    err |= fg2_set_source(this->fg, this->source, this->sourceCh);
    err |= fg2_set_source_norm(this->fg, this->norm);
    if (err!=0){
        PLAYER_ERROR("Camerav4l2: failed source or norm");
        fg2_delete(&fg);
        return -1;
    }
    for(i=0; i<numOfCtls; i++){
        fg2_setControlValue(fg, ctlNames[i], ctlVals[i]);
    }

    if (fg2_setPixelSettings(fg, this->width, this->height, this->v4l2_type_id, this->fieldType, this->depth)==0) {
    } else {
        PLAYER_ERROR("Camerav4l2: nie udalo sie ustawic podanych parametrow");
        //fg2_delete(&fg);
        //return -1;
    }

    // finally start streaming with new settings
    if (fg2_startCapture(fg)!=0){
        fg2_delete(&fg);
        return -1;
    }

  // Start the driver thread.
    this->StartThread();

    return 0;
}


////////////////////////////////////////////////////////////////////////////////
// Shutdown the device (called by server thread).
int CameraV4L2::Shutdown()
{
    //printf("CameraV4L2::Shutdown()");
  // Stop the driver thread.
    StopThread();

    fg2_delete(&fg);
    return 0;
}


////////////////////////////////////////////////////////////////////////////////
// Main function for device thread
void CameraV4L2::Main()
{

    struct timeval time;
    long sumTime=0;
    int fpsFrames = 0;
    struct timeval timePrev;

    GlobalTime->GetTime(&timePrev);


    int frameno;

    frameno = 0;

    while (true)
    {
    // Go to sleep for a while (this is a polling loop).
        //usleep(50000);

    // Test if we are supposed to cancel this thread.
        pthread_testcancel();

    // Process any pending requests.
        HandleRequests();

    // Get the time
        GlobalTime->GetTime(&time);

        sumTime += (time.tv_sec-timePrev.tv_sec)*1000000 + (time.tv_usec-timePrev.tv_usec);
        timePrev = time;
        fpsFrames++;
        if (sumTime > 2000000){
            if (showFPS>=0)
                PLAYER_MSG2(showFPS, "Fps: %f (%d)", 1000000.0*fpsFrames/sumTime, fpsFrames);
            sumTime = 0;
            fpsFrames = 0;
        }
        if (sumTime<0){
            sumTime = 0;
            fpsFrames = 0;
        }

        this->tsec = time.tv_sec;
        this->tusec = time.tv_usec;



        //printf("Time between

    // Write data to server
        this->WriteData();

        frameno++;
    }
}

/**
 * try given parameters, and back to previous when failed and return !=0 on failure
 * @param fg Handler
 * @param w width
 * @param h height
 * @param fmt v4l2_type_id
 * @param fld fieldType
 * @return 0 ok, -1 bad
 */
int CameraV4L2::tryPixelSettings(FRAMEGRABBER2* fg, int w, int h, unsigned int fmt, enum v4l2_field fld)
{
    int ret=0;
    if (fg2_setPixelSettings(fg, w, h,fmt, fld, this->depth)!=0){
        fg2_setPixelSettings(fg, this->width, this->height,
                             this->v4l2_type_id, fieldType, this->depth);
        ret = -1;
    } else {
        this->width = w;
        this->height = h;
        this->v4l2_type_id = fmt;
        fieldType = fld;
    }

    return ret;
}


////////////////////////////////////////////////////////////////////////////////
/// Process incoming requests
int CameraV4L2::HandleRequests()
{
    void *client;
    char request[PLAYER_MAX_REQREP_SIZE];
    char outBuf[PLAYER_MAX_REQREP_SIZE];
    int len;


    while ((len = GetConfig(&client, &request, sizeof(request),NULL)) > 0)
    {
        int ret = -1;
        PLAYER_MSG2(2,"Got Reguest %c (size: %d)", request[0], len);
        if (len>1 && (request[0]=='g' || request[0]=='G')){
            // pobranie jakiejs wartosci
            PLAYER_MSG0(2,"Get type request");
            ret = handleCommand(request, len, outBuf, PLAYER_MAX_REQREP_SIZE-1);

            if (ret==0) {
                outBuf[PLAYER_MAX_REQREP_SIZE-1] = '\0';
                PLAYER_MSG1(2,"Sending Ack: %s", outBuf);
                if (PutReply(client, PLAYER_MSGTYPE_RESP_ACK, outBuf, strlen(outBuf)+1,NULL) != 0)
                    PLAYER_ERROR("PutReply() failed");
            } else {
                PLAYER_MSG0(2,"Sendinf NACK");
                if (PutReply(client, PLAYER_MSGTYPE_RESP_NACK,NULL) != 0)
                    PLAYER_ERROR("PutReply() failed");
            }

        } else {
            if (len>0) {
                ret = handleCommand(request, len);
            }

            if (ret == 0){
                PLAYER_MSG0(2,"Sending Ack");
                if (PutReply(client, PLAYER_MSGTYPE_RESP_ACK,NULL) != 0)
                    PLAYER_ERROR("PutReply() failed");
            } else {
                PLAYER_MSG0(2,"Sending Nack");
                if (PutReply(client, PLAYER_MSGTYPE_RESP_NACK,NULL) != 0)
                    PLAYER_ERROR("PutReply() failed");
            }
        }
        // if framegrabber is slow(1-10s), it is worth handling all commands at once
        //usleep(30000);
    }
    return 0;
}

/**
 * handle command without reply
 * @param cmd command in text representation
 * @param len length of cmd (including '\0')
 * @return 0 ok, -1 failure
 */
int CameraV4L2::handleCommand(char *cmd, int len){
    int w;
    int h;
    enum v4l2_field fld;
    int ret=-1;
    unsigned int a,b,c; // backup
    double dblVal;

    ret = -1; // error on default
    PLAYER_MSG1(2,"Got command %s", cmd);
    switch(cmd[0]){
        case 's':
        case 'S':
            if (sscanf(cmd+1," %d %d", &w, &h)!=2)
                break;
            fg2_stopCapture(fg);
            ret = tryPixelSettings(fg, w, h, this->v4l2_type_id, fieldType);
            fg2_startCapture(fg);
            break;
        case 'i':
        case 'I':
                // todo: rollback if needed
            if (cmd[2]>='0' && cmd[2]<='9')
                sscanf(cmd+2,"%d", &(this->source));
            else {
                this->source = -1;
            }

            fg2_stopCapture(fg);
            ret = fg2_set_source(this->fg, this->source, cmd+2);
            fg2_startCapture(fg);
            break;
        case 'n':
        case 'N':
            a = this->norm;
            b = this->width;
            c = this->height;

            if (cmd[1]!=' ' || selectFormat(cmd+2)!=0)
                break;
            fg2_stopCapture(fg);
            ret = fg2_set_source_norm(this->fg, this->norm);
            ret |= tryPixelSettings(fg, this->width, this->height, this->v4l2_type_id, fieldType);
            if (ret!=0) {
                this->norm = a;
                this->width = b;
                this->height = c;
                    // known working?
                fg2_set_source_norm(this->fg, this->norm);
                tryPixelSettings(fg, this->width, this->height, this->v4l2_type_id, fieldType);
            }
            fg2_startCapture(fg);
            break;
        case 'm':
        case 'M':
            a = this->data.format;
            b = this->depth;
            c = this->v4l2_type_id;

            if (cmd[1]!=' ' || selectFormat(cmd+2)!=0)
                break;
            fg2_stopCapture(fg);
            ret = tryPixelSettings(fg, this->width, this->height, this->v4l2_type_id, fieldType);
            if (ret!=0) {
                this->data.format = a;
                this->depth = b;
                this->v4l2_type_id = c;
                ret = tryPixelSettings(fg, this->width, this->height, this->v4l2_type_id, fieldType);
            }
            fg2_startCapture(fg);
            break;
        case 'w':
        case 'W':
                // todo: rollback if needed
            if (cmd[2]>='0' && cmd[2]<='9'){
                sscanf(cmd+2,"%d", &(this->flip_rb));
                ret = 0;
            }
            break;
        case 'f':
        case 'F':
            fld = fieldType;

            if (cmd[1]!=' ' || selectField(cmd+2)!=0)
                break;
            fg2_stopCapture(fg);
            ret = tryPixelSettings(fg, this->width, this->height, this->v4l2_type_id, fieldType);
            fg2_startCapture(fg);
            if (ret!=0) {
                fieldType = fld;
            }
            break;
        case 'c':
        case 'C':
            if (cmd[1] == 'i' || cmd[1] == 'I') {
                if (sscanf(cmd+2, "%lf %u", &dblVal, &a)!=2)
                    break;
                ret = fg2_setControlValueI(fg, a, dblVal);
                break;
            }
            for(w=2; w<len-1; w++)
                if (cmd[w]==' ' || cmd[w+1]=='\0')
                    break;
            if (sscanf(cmd+2,"%lf",&dblVal) != 1)
                break;
            ret = fg2_setControlValue(fg, cmd+w+1, dblVal);
            break;
        default:
            PLAYER_WARN1("Unknown command %s",cmd);
            break;
    }
    return ret;
}



/**
 * handle command and generate reply
 * @param cmd command in text representation
 * @param len length of cmd (including '\0')
 * @param reply buffer for reply
 * @param rlen length of buffer for reply
 * @return 0 ok, -1 failure
 */
int CameraV4L2::handleCommand(char *cmd, int len, char *reply, int rlen){
    //enum v4l2_field fld;
    int ret=-1;
    unsigned int a; // backup
    double dblVal;

    ret = -1; // error on default
    PLAYER_MSG1(2,"Got command get type %s", cmd);
    switch(cmd[1]){
        case 'c': // controls
        case 'C':
            if (len<3)
                break;
            if (cmd[2] == '\0'){
                // count controls
                PLAYER_MSG0(2,"counting controls");
                a = fg2_countControls(fg);
                ret = 0;
                snprintf(reply, rlen, "%d", a);
                break;
            }
            if (len<4)
                break;
            if (cmd[2] == 'i' || cmd[2] == 'I') {
                if (sscanf(cmd+3, "%u", &a)!=1)
                    break;
                dblVal = fg2_getControlValueI(fg, a);
                ret = 0;
                snprintf(reply, rlen, "%f", dblVal);
                break;
            }
            if (cmd[2] == 'n' || cmd[2] == 'N' && cmd[3]==' '){
                if (sscanf(cmd+3, "%u", &a)!=1)
                    break;
                // get name
                const char *name = fg2_getControlName(fg, a);
                if (name==0)
                    break;
                ret = 0;
                snprintf(reply, rlen, "%s", name);
                break;
            }
            if (cmd[2] != ' ')
                break;

            dblVal = fg2_getControlValue(fg, cmd + 3);
            ret = 0;
            snprintf(reply, rlen, "%f", dblVal);
            break;
        case 'f': // fields
        case 'F':
        {
            int i=0;
            for(i=0; fieldy[i].name; i++)
                if (fieldy[i].fieldType == this->fieldType)
                    break;
            if (fieldy[i].name) {
                snprintf(reply, rlen, "%s", fieldy[i].name);
                ret = 0;
            } else {
                PLAYER_WARN1("Unknown field type: %d", this->fieldType);
            }
            break;
        }
        case 'm': // modes
        case 'M':
        {
            int i=0;
            for(i=0; formaty[i].name; i++)
                if (formaty[i].v4l2_type_id == this->v4l2_type_id)
                    break;
            if (formaty[i].name) {
                snprintf(reply, rlen, "%s", formaty[i].name);
                ret = 0;
            } else {
                PLAYER_WARN1("Unknown pixel format type: %d", this->v4l2_type_id);
            }
            break;
        }
        case 'n': // norms
        case 'N':
        {
            int i=0;
            for(i=0; normy[i].name; i++)
                if (normy[i].id == this->norm)
                    break;
            if (formaty[i].name) {
                snprintf(reply, rlen, "%s", normy[i].name);
                ret = 0;
            } else {
                PLAYER_WARN1("Unknown pixel format type: %d", this->norm);
            }
            break;
        }
        case 'i': // source name
        case 'I':
        {
            ret = 0;
            snprintf(reply, rlen, "%s", fg2_get_source_name(fg));
            break;
        }
        case '\0': // driver name
        {
            ret = 0;
            snprintf(reply, rlen, "%s", fg2_get_device_name(fg));
            break;
        }
        default:
            PLAYER_ERROR1("Unknown command2 %s",cmd);
            break;
    }
    return ret;
}


////////////////////////////////////////////////////////////////////////////////
/**
 @brief Update the device data (the data going back to the client).
*/
void CameraV4L2::WriteData()
{
    size_t image_size, size;
    unsigned char * ptr1, * ptr2;

    struct my_buffer *v4lBuffer = getFrameBuffer(fg);
    if (v4lBuffer==NULL)
        exit(1);
    ptr1 = (unsigned char *)v4lBuffer->start;
    ptr2 = this->data.image;

  // Compute size of image
    image_size = this->width * this->height * this->depth / 8;

  // Set the image properties
    this->data.width = htons(this->width);
    this->data.height = htons(this->height);
    this->data.bpp = this->depth;
    this->data.compression = PLAYER_CAMERA_COMPRESS_RAW;
    this->data.image_size = htonl(image_size);

    if (image_size > sizeof(this->data.image)){
        PLAYER_ERROR2("image_size <= sizeof(this->data.image) failed: %d > %d",
               image_size, sizeof(this->data.image));
    }
    assert(image_size <= sizeof(this->data.image));
    if (image_size > (size_t) v4lBuffer->length){
        PLAYER_WARN("Frame size is smaller then expected");
        image_size = (size_t) v4lBuffer->length;
    }
    //assert(image_size <= (size_t) v4lBuffer->length);


    if (!flip_rb) {
        memcpy(ptr2, ptr1, image_size);
    } else {
        int imgSize = ((this->width) * (this->height));
        int i;
        switch (v4l2_type_id)
        {
            case V4L2_PIX_FMT_RGB24:
            case V4L2_PIX_FMT_BGR24:
                for (i = 0; i < imgSize; i++)
                {
                    ptr2[0] = ptr1[2];
                    ptr2[1] = ptr1[1];
                    ptr2[2] = ptr1[0];
                    ptr1 += 3;
                    ptr2 += 3;
                }
                break;
            case V4L2_PIX_FMT_RGB32:
            case V4L2_PIX_FMT_BGR32:
                for (i = 0; i < imgSize; i++)
                {
                    ptr2[0] = ptr1[2];
                    ptr2[1] = ptr1[1];
                    ptr2[2] = ptr1[0];
                    ptr2[3] = ptr1[3];
                    ptr1 += 4;
                    ptr2 += 4;
                }
                break;
            default:
                memcpy(ptr2, ptr1, image_size);
        }
    }

  // Copy data to server
    size = sizeof(this->data) - sizeof(this->data.image) + image_size;

    struct timeval timestamp;
    timestamp.tv_sec = this->tsec;
    timestamp.tv_usec = this->tusec;
    PutData((void*) &this->data, size, &timestamp);

    giveBackFrameBuffer(fg, v4lBuffer);

    return;
}


