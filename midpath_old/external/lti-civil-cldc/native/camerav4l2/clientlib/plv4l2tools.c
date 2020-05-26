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
// Desc: Helper functions to change names to proper enum values in camv4l2 client libs
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
///////////////////////////////////////////////////////////////////////////
#include <string.h>
#include "playerv4l2c.h"
#include "plv4l2tools.h"

// converters:

typedef struct  {
    const char *name;
    int id;
} Map_S2I;


Map_S2I normy[] = {
    { // first is default
        "PAL",
        PLAYER_V4L2_STD_PAL,
    },{
        "NTSC",
        PLAYER_V4L2_STD_NTSC,
    },{
        "SECAM",
        PLAYER_V4L2_STD_SECAM,
    },{
        0,-1// do not delete this
    }
};

Map_S2I formaty[] = {
    { /* 24  RGB-8-8-8     */
        "RGB24",
        PLAYER_V4L2_FMT_RGB24,
    },{/* 32  RGB-8-8-8-8   */
        "RGB32",
        PLAYER_V4L2_FMT_RGB32,
    },{/* 24  BGR-8-8-8     */
        "BGR24",
        PLAYER_V4L2_FMT_BGR24,
    },{/* 32  BGR-8-8-8-8   */
        "BGR32",
        PLAYER_V4L2_FMT_BGR32,
    },{/*  8  Greyscale     */
        "GREY",
        PLAYER_V4L2_FMT_GREY,
    },{/* 16  RGB-5-6-5     */
        "RGB565",
        PLAYER_V4L2_FMT_RGB565,
    },{/* see http://www.siliconimaging.com/RGB%20Bayer.htm */
        "SBGGR8",
        PLAYER_V4L2_FMT_SBGGR8,
    },{/*  8  RGB-3-3-2     */
        "RGB332",
        PLAYER_V4L2_FMT_RGB332,
    },{/* 16  RGB-5-5-5     */
        "RGB555",
        PLAYER_V4L2_FMT_RGB555,
    },{/* 16  RGB-5-5-5 BE  */
        "RGB555X",
        PLAYER_V4L2_FMT_RGB555X,
    },{/* 16  RGB-5-6-5 BE  */
        "RGB565X",
        PLAYER_V4L2_FMT_RGB565X,
    },{/*  9  YVU 4:1:0     */
        "YVU410",
        PLAYER_V4L2_FMT_YVU410,
    },{/* 12  YVU 4:2:0     */
        "YVU420",
        PLAYER_V4L2_FMT_YVU420,
    },{/* 16  YUV 4:2:2     */
        "YUYV",
        PLAYER_V4L2_FMT_YUYV,
    },{/* 16  YUV 4:2:2     */
        "UYVY",
        PLAYER_V4L2_FMT_UYVY,
    },{/* 16  YVU422 planar */
        "YUV422P",
        PLAYER_V4L2_FMT_YUV422P,
    },{/* 16  YVU411 planar */
        "YUV411P",
        PLAYER_V4L2_FMT_YUV411P,
    },{/* 12  YUV 4:1:1     */
        "Y41P",
        PLAYER_V4L2_FMT_Y41P,
    },{/* 12  Y/CbCr 4:2:0  */
        "NV12",
        PLAYER_V4L2_FMT_NV12,
    },{/* 12  Y/CrCb 4:2:0  */
        "NV21",
        PLAYER_V4L2_FMT_NV21,
    },{
        0,-1// do not delete this
    }
};

////////////////////////////////////////////////////////////////////////////////
// list of all known fields interleaving types
Map_S2I fieldy[] = {
    { // first is default
        "ANY",
        PLAYER_V4L2_FIELD_ANY,
    },{
        "NONE",
        PLAYER_V4L2_FIELD_NONE,
    },{
        "TOP",
        PLAYER_V4L2_FIELD_TOP,
    },{
        "BOTTOM",
        PLAYER_V4L2_FIELD_BOTTOM,
    },{
        "INTERLEACED",
        PLAYER_V4L2_FIELD_INTERLACED,
    },{
        "SEQ_TB",
        PLAYER_V4L2_FIELD_SEQ_TB,
    },{
        "SEQ_BT",
        PLAYER_V4L2_FIELD_SEQ_BT,
    },{
        "ALTERNATE",
        PLAYER_V4L2_FIELD_ALTERNATE,
    },{
        0,-1// do not delete this
    }
};

int convS2I(const char *n, Map_S2I *m){
    int i=0;
    if (n==0)
        return 0;
    while(m[i].name) {
        if (strcmp(m[i].name, n)==0)
            return m[i].id;
        i++;
    }
    return -1;
}

const char * convI2S(int n, Map_S2I *m) {
    int i=0;
    while(m[i].name)
        if (m[i].id == n)
            return m[i].name;
    return "";
}

int convPixS2I(const char *n){
    return convS2I(n, formaty);
}

const char * convPixI2S(int n){
    return convI2S(n, formaty);
}

int convNormS2I(const char *n){
    return convS2I(n, normy);
}

const char * convNormI2S(int n){
    return convI2S(n, normy);
}

int convFieldS2I(const char *n){
    return convS2I(n, fieldy);
}

const char * convFieldI2S(int n){
    return convI2S(n, fieldy);
}

