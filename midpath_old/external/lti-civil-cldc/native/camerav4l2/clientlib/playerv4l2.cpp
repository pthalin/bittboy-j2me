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
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "playerv4l2.h"
#include "plv4l2tools.h"


#define TMP_BUFF_SIZE 128

#define WYKONAJ_REQUEST(req, len) this->client->Request(this->m_device_id, (req), (len) )
#define WYKONAJ_REQUEST4(req, len, rep, rplen) this->client->Request(this->m_device_id, (req), (len), &rephdr, (rep), (rplen))

CameraV4L2Proxy::CameraV4L2Proxy (PlayerClient *pc, unsigned short index,
                     unsigned char access)
    : CameraProxy (pc, index, access )
{
    swapRBFlag = 0;
    refreshState();

    getBrighteness();
    getContrast();
    getSaturation();
    getHue();
}


void CameraV4L2Proxy::refreshState(){
    getPixelFormatS();
    pixelFormat = (PixelFormats)convPixS2I(pixelFormatS);

    getNormS();
    norm = (NormStandards)convNormS2I(normS);

    getFieldS();
    field = (FieldTypes)convFieldS2I(fieldS);

    getName();

    getInputName();

    noOfControls = countControls();
}


// interface that all proxies must provide
void CameraV4L2Proxy::FillData(player_msghdr_t hdr, const char* buffer)
{
    CameraProxy::FillData(hdr, buffer);
}

// Selects input source
int CameraV4L2Proxy::selectSourceI(int src_id)
{
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE,"i %d", src_id);

    int out =  WYKONAJ_REQUEST(req, strlen(req)+1);
    refreshState();

    return out;
}

// Selects input source
int CameraV4L2Proxy::selectSourceS(const char *src_name)
{
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE,"i %s", src_name);

    int out =  WYKONAJ_REQUEST(req, strlen(req)+1);
    refreshState();

    return out;
}

int CameraV4L2Proxy::setFrameSize(int w, int h)
{
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE,"s %d %d", w, h);

    return WYKONAJ_REQUEST(req, strlen(req)+1);
}

// pal, ntsc,
int CameraV4L2Proxy::setNormI(int normID)
{
    return setNormS(convNormI2S(normID));
}

// pal, ntsc,
int CameraV4L2Proxy::setNormS(const char * normID)
{
    if (normID == 0)
        return -1;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "n %s", normID);

    int out =  WYKONAJ_REQUEST(req, strlen(req)+1);
    refreshState();

    return out;
}

//
int CameraV4L2Proxy::setFieldTypeI(int fielsType)
{
    return setFieldTypeS(convFieldI2S(fielsType));
}

//
int CameraV4L2Proxy::setFieldTypeS(const char * fielsType)
{
    if (fielsType == 0)
        return -1;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "f %s", fielsType);

    int out =  WYKONAJ_REQUEST(req, strlen(req)+1);
    getFieldS();
    field = (FieldTypes)convFieldS2I(fieldS);


    return out;
}


int CameraV4L2Proxy::setPixelFormatI(int pixFormat)
{
    return setPixelFormatS(convPixI2S(pixFormat));
}

int CameraV4L2Proxy::setPixelFormatS(const char * pixFormat)
{
    if (pixFormat == 0)
        return -1;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "m %s", pixFormat);
    int out = WYKONAJ_REQUEST(req, strlen(req)+1);
    getPixelFormatS();
    pixelFormat = (PixelFormats)convPixS2I(pixelFormatS);

    return out;
}

// for RGB/BGR formats, if swap!=0, then swap Red and Blue
int CameraV4L2Proxy::setSwapRB(int swap)
{
    char *req = 0;

    if (swap)
        req = "w 1";
    else
        req = "w 0";

    int out = WYKONAJ_REQUEST(req, strlen(req)+1);

    if (out == 0)
        swapRBFlag = swap;

    return out;
}

// get number of supported controls
int CameraV4L2Proxy::countControls()
{
    player_msghdr_t rephdr;
    char tmpBuf[TMP_BUFF_SIZE+1];
    int out;

    char *req = "gc"; // get Count

    out = WYKONAJ_REQUEST4(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE);

    if (out != 0)
        return -1;

    if (sscanf(tmpBuf, "%d", &out)!=1)
        return -1;
    return out;
}

// set new value for control. Value is range 0.0 - 1.0.
int CameraV4L2Proxy::setControlValueI(int id, double value)
{
    char req[TMP_BUFF_SIZE+1];
    int out;

    snprintf(req, TMP_BUFF_SIZE,"ci %f %d", value, id);
    out = WYKONAJ_REQUEST(req, strlen(req)+1);
    if (out==0) {
        getBrighteness();
        getContrast();
        getSaturation();
        getHue();
    }
    return out;
}

// set new value for control. Value is range 0.0 : 1.0.
int CameraV4L2Proxy::setControlValueS(const char *name, double value)
{
    char req[TMP_BUFF_SIZE+1];
    int out;

    snprintf(req, TMP_BUFF_SIZE,"c %f %s", value, name);
    out = WYKONAJ_REQUEST(req, strlen(req)+1);
    if (out==0) {
        getBrighteness();
        getContrast();
        getSaturation();
        getHue();
    }
    return out;
}

// returns current control value in range 0-1. values below 0, means error
// while querying driver
double CameraV4L2Proxy::getControlValueI(int id)
{
    player_msghdr_t rephdr;
    char tmpBuf[TMP_BUFF_SIZE+1];
    double out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gci %d", id);
    tmpBuf[0]='\0';

    if (WYKONAJ_REQUEST4(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE) == 0 &&
                sscanf(tmpBuf, "%lf", &out)!=1)
        return -1.0;
    return out;
}

// returns current control value in range 0-1. values below 0, means error
// while querying driver
double CameraV4L2Proxy::getControlValueS(const char *name)
{
    player_msghdr_t rephdr;
    char tmpBuf[TMP_BUFF_SIZE+1];
    double out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gc %s", name);

    if (WYKONAJ_REQUEST4(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE) == 0 &&
                sscanf(tmpBuf, "%lf", &out)!=1)
        return -1.0;
    return out;
}

const char * CameraV4L2Proxy::getControlName(int id){
    player_msghdr_t rephdr;
    static char tmpBuf[TMP_BUFF_SIZE+1];
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gcn %d", id);

    out = WYKONAJ_REQUEST4(req, strlen(req)+1, tmpBuf, TMP_BUFF_SIZE);

    if (out != 0)
        return 0;

    return tmpBuf;
}


int CameraV4L2Proxy::setBrighteness(double value)
{
    return setControlValueS("Brighteness", value);
}

double CameraV4L2Proxy::getBrighteness()
{
    return brighteness = getControlValueS("Brighteness");
}


int CameraV4L2Proxy::setSaturation(double value)
{
    return setControlValueS("Saturation", value);
}

double CameraV4L2Proxy::getSaturation()
{
    return saturation = getControlValueS("Saturation");
}


int CameraV4L2Proxy::setContrast(double value)
{
    return setControlValueS("Contrast", value);
}

double CameraV4L2Proxy::getContrast()
{
    return contrast = getControlValueS("Contrast");
}


int CameraV4L2Proxy::setHue(double value)
{
    return setControlValueS("Hue", value);
}

double CameraV4L2Proxy::getHue()
{
    return hue = getControlValueS("Hue");
}




/** @brief retrieves current input name */
const char * CameraV4L2Proxy::getInputName(){
    player_msghdr_t rephdr;
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gi");

    out = WYKONAJ_REQUEST4(req, strlen(req)+1, inputName, sizeof(inputName));

    if (out != 0)
        return 0;

    return inputName;
}



/** @brief get device name */
const char * CameraV4L2Proxy::getName(){
    player_msghdr_t rephdr;
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "g");

    out = WYKONAJ_REQUEST4(req, strlen(req)+1, name, sizeof(name));

    if (out != 0)
        return 0;

    return name;
}

/** @brief get current field settings */
const char * CameraV4L2Proxy::getFieldS(){
    player_msghdr_t rephdr;
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gf");

    out = WYKONAJ_REQUEST4(req, strlen(req)+1, fieldS, sizeof(fieldS));

    if (out != 0)
        return 0;

    return fieldS;
}

/** @brief get current field settings */
const char * CameraV4L2Proxy::getPixelFormatS(){
    player_msghdr_t rephdr;
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gm");

    out = WYKONAJ_REQUEST4(req, strlen(req)+1, pixelFormatS, sizeof(pixelFormatS));

    if (out != 0)
        return 0;

    return pixelFormatS;
}

/** @brief get current field settings */
const char * CameraV4L2Proxy::getNormS(){
    player_msghdr_t rephdr;
    int out;
    char req[TMP_BUFF_SIZE+1];
    snprintf(req, TMP_BUFF_SIZE, "gn");

    out = WYKONAJ_REQUEST4(req, strlen(req)+1, normS, sizeof(normS));

    if (out != 0)
        return 0;

    return normS;
}

