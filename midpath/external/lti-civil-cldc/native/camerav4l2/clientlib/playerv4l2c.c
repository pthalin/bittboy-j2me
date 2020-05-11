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
// Desc: Client library in C to access player_driver_camerav4l2 interface
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
///////////////////////////////////////////////////////////////////////////
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "playerv4l2c.h"
#include "plv4l2tools.h"

#define TMP_BUFF_SIZE 128

#define WYKONAJ_REQUEST(req, len, rep_data, rep_len) playerc_client_request(device->info.client, &(device->info), \
                           (req), (len), (rep_data), (rep_len))


// Selects input source
int playerv4l2c_camera_selectSourceI(playerc_camera_t *device, int src_id)
{
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE,"i %d", src_id);

    return WYKONAJ_REQUEST(req, strlen(req)+1, 0, 0);
}

// Selects input source
int playerv4l2c_camera_selectSourceS(playerc_camera_t *device, const char *src_name)
{
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE,"i %s", src_name);

    return WYKONAJ_REQUEST(req, strlen(req)+1, NULL, 0);
}

int playerv4l2c_camera_setFrameSize(playerc_camera_t *device, int w, int h)
{
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE,"s %d %d", w, h);

    return WYKONAJ_REQUEST(req, strlen(req)+1, NULL, 0);
}

// pal, ntsc,
int playerv4l2c_camera_setNormI(playerc_camera_t *device, int normID)
{
    return playerv4l2c_camera_setNormS(device, convNormI2S(normID));
}

// pal, ntsc,
int playerv4l2c_camera_setNormS(playerc_camera_t *device, const char * normID)
{
    char req[TMP_BUFF_SIZE+1];
    if (normID == 0)
        return -1;
    snprintf(req, TMP_BUFF_SIZE,"n %s", normID);

    return WYKONAJ_REQUEST(req, strlen(req)+1, NULL, 0);
}

//
int playerv4l2c_camera_setFieldTypeI(playerc_camera_t *device, int fielsType)
{
    return playerv4l2c_camera_setFieldTypeS(device, convFieldI2S(fielsType));
}

//
int playerv4l2c_camera_setFieldTypeS(playerc_camera_t *device, const char * fielsType)
{
    char req[TMP_BUFF_SIZE+1];
    if (fielsType == 0)
        return -1;

    snprintf(req, TMP_BUFF_SIZE,"f %s", fielsType);

    return WYKONAJ_REQUEST(req, strlen(req)+1, NULL, 0);
}


int playerv4l2c_camera_setPixelFormatI(playerc_camera_t *device, int pixFormat)
{
    return playerv4l2c_camera_setPixelFormatS(device, convPixI2S(pixFormat));
}

int playerv4l2c_camera_setPixelFormatS(playerc_camera_t *device, const char * pixFormat)
{
    char req[TMP_BUFF_SIZE+1];
    if (pixFormat == 0)
        return -1;

    snprintf(req, TMP_BUFF_SIZE,"m %s", pixFormat);

    return WYKONAJ_REQUEST(req, strlen(req)+1, NULL, 0);
}

// for RGB/BGR formats, if swap!=0, then swap Red and Blue
int playerv4l2c_camera_setSwapRB(playerc_camera_t *device, int swap)
{
    char *req = 0;

    if (swap)
        req = "w 1";
    else
        req = "w 0";

    return WYKONAJ_REQUEST(req, strlen(req)+1, NULL, 0);
}

// get number of supported controls
int playerv4l2c_camera_countControls(playerc_camera_t *device)
{
    char tmpBuf[TMP_BUFF_SIZE];
    int out;

    char *req = "gc"; // get Count

    out = WYKONAJ_REQUEST(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE+1);

    if (out != 0)
        return -1;

    if (sscanf(tmpBuf, "%d", &out)!=1)
        return -1;
    return out;
}

// set new value for control. Value is range 0.0 - 1.0.
int playerv4l2c_camera_setControlValueI(playerc_camera_t *device, int id, double value)
{
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE,"ci %f %d", value, id);

    return WYKONAJ_REQUEST(req, strlen(req)+1, NULL, 0);
}

// set new value for control. Value is range 0.0 : 1.0.
int playerv4l2c_camera_setControlValueS(playerc_camera_t *device, const char *name, double value)
{
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE,"c %f %s", value, name);

    return WYKONAJ_REQUEST(req, strlen(req)+1, NULL, 0);
}

// returns current control value in range 0-1. values below 0, means error
// while querying driver
double playerv4l2c_camera_getControlValueI(playerc_camera_t *device, int id)
{
    char tmpBuf[TMP_BUFF_SIZE];
    double out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gci %d", id);

    WYKONAJ_REQUEST(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE+1);

    if (sscanf(tmpBuf, "%lf", &out)!=1)
        return -1.0;
    return out;
}

// returns current control value in range 0-1. values below 0, means error
// while querying driver
double playerv4l2c_camera_getControlValueS(playerc_camera_t *device, const char *name)
{
    char tmpBuf[TMP_BUFF_SIZE];
    double out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gc %s", name);

    WYKONAJ_REQUEST(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE+1);

    if (sscanf(tmpBuf, "%lf", &out)!=1)
        return -1.0;
    return out;
}



int playerv4l2c_camera_setBrighteness(playerc_camera_t *device, double value)
{
    return playerv4l2c_camera_setControlValueS(device, "Brighteness", value);
}

double playerv4l2c_camera_getBrighteness(playerc_camera_t *device)
{
    return playerv4l2c_camera_getControlValueS(device, "Brighteness");
}


int playerv4l2c_camera_setSaturation(playerc_camera_t *device, double value)
{
    return playerv4l2c_camera_setControlValueS(device, "Saturation", value);
}

double playerv4l2c_camera_getSaturation(playerc_camera_t *device)
{
    return playerv4l2c_camera_getControlValueS(device, "Saturation");
}


int playerv4l2c_camera_setContrast(playerc_camera_t *device, double value)
{
    return playerv4l2c_camera_setControlValueS(device, "Contrast", value);
}

double playerv4l2c_camera_getContrast(playerc_camera_t *device)
{
    return playerv4l2c_camera_getControlValueS(device, "Contrast");
}


int playerv4l2c_camera_setHue(playerc_camera_t *device, double value)
{
    return playerv4l2c_camera_setControlValueS(device, "Hue", value);
}

double playerv4l2c_camera_getHue(playerc_camera_t *device)
{
    return playerv4l2c_camera_getControlValueS(device, "Hue");
}




/** @brief retrieves current input name */
const char * getInputName(playerc_camera_t *device)
{
    static char tmpBuf[TMP_BUFF_SIZE+1];
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gi");

    out = WYKONAJ_REQUEST(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE);

    if (out != 0)
        return 0;

    return tmpBuf;
}
/** @brief get device name */
const char * playerv4l2c_camera_getName(playerc_camera_t *device)
{
    static char tmpBuf[TMP_BUFF_SIZE+1];
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "g");

    out = WYKONAJ_REQUEST(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE);

    if (out != 0)
        return 0;

    return tmpBuf;
}


/** @brief get current field settings */
const char * getFieldS(playerc_camera_t *device)
{
    static char tmpBuf[TMP_BUFF_SIZE+1];
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gf");

    out = WYKONAJ_REQUEST(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE);

    if (out != 0)
        return 0;

    return tmpBuf;
}

/** @brief get current field settings */
int getFieldI(playerc_camera_t *device)
{
    const char *tst = getFieldS(device);
    if (tst==0)
        return -1;

    return convFieldS2I(tst);
}



/** @brief get current field settings */
const char * getPixelFormatS(playerc_camera_t *device){
    static char tmpBuf[TMP_BUFF_SIZE+1];
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gm");

    out = WYKONAJ_REQUEST(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE);

    if (out != 0)
        return 0;

    return tmpBuf;
}

/** @brief get current field settings */
int getPixelFormatI(playerc_camera_t *device) {
    const char *tst = getPixelFormatS(device);
    if (tst==0)
        return -1;

    return convPixS2I(tst);
}


/** @brief get current norm settings */
const char * getNormS(playerc_camera_t *device){
    static char tmpBuf[TMP_BUFF_SIZE+1];
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gn");

    out = WYKONAJ_REQUEST(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE);

    if (out != 0)
        return 0;

    return tmpBuf;
}

/** @brief get current norm settings */
int getNormI(playerc_camera_t *device) {
    const char *tst = getNormS(device);
    if (tst==0)
        return -1;

    return convNormS2I(tst);
}
