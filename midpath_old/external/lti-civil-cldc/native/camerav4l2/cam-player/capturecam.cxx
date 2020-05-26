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
// Desc: Example application using camerav4l2 player module and library
// Author: Marcin Rudowski
// Email: mar_rud@poczta.onet.pl
// Date: 19 Jun 2006
//
///////////////////////////////////////////////////////////////////////////
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>


#include "capturecam.h"




bool CaptureCam::ProcessImage(unsigned char *p)
{
    // do any processing,
    depth = crp->depth;
    //printf("Depth: %d\n", depth);
    if (crp->pixelFormat == PLAYER_V4L2_FMT_RGB565) {
        // rgb565 encode
        unsigned char *p1 = p, *p2 = processed;
        //printf("processing\n");
        for(int i=0; i<crp->width*crp->height; i++){
            unsigned char r,g,b;
            r = p1[0] << 3;
            g = (((p1[0]) >>5)<<2) | ( (p1[1]) << 5 );
            b = (p1[1] >> 3) << 3;
            if (crp->swapRBFlag) {
                p2[0] = b;
                p2[1] = g;
                p2[2] = r;
            } else {
                p2[0] = r;
                p2[1] = g;
                p2[2] = b;
            }
            p2+=3;
            p1+=2;
        }
        depth = 24;
        out = processed;
    } else {
        out = p;
    }

    return true;
}

// do synchronizacji z rzeczywistym urzadzeniem
long timestamp, timestampold;
bool CaptureCam::ReadImage(void)
{
    do {
        if(robot->Read(false, (ClientProxy **)&crp))
            exit(1);
        timestamp = crp->timestamp.tv_usec;
    } while(timestamp == timestampold);

    if (timestamp != timestampold) {
        timestampold = timestamp;
        frameNo++;
        ProcessImage((unsigned char*) crp->image);
        return true;
    }


    return false;
}



// Returns true if new img was read.
bool CaptureCam::Retrieve(void)
{
    if (!capturing)  return false;
    return ReadImage();
}


bool CaptureCam::StopCapture(void)
{
    printf("Ending Capture   --------\n");
    capturing = false;

    return true;
}


bool CaptureCam::StartCapture(void)
{
    printf("Starting Capture ++++++++\n");
    capturing = true;

    return true;
}



CaptureCam::~CaptureCam()
{
    crp->Close();
    delete crp;
    delete robot;
}


CaptureCam::CaptureCam(const char *adress, int port, int width, int height) :
  w(width),
  h(height),
  capturing(false)
{
    formatPix = "";
    frameNo = 0;
  // open device
    printf("Creating client connection\n");
    if (port>0)
        robot = new PlayerClient(adress, port);
    else
        robot = new PlayerClient(adress);
    robot->SetDataMode(PLAYER_DATAMODE_PULL_ALL);

    //sleep(2);
    printf("Creating camera proxy\n");
    crp = new CameraV4L2Proxy(robot, 0, 'a');
    //sleep(2);
    if (crp->GetAccess()!='a')
    //if (!robot->Connected ())
    {
        printf("Nie poloczono\n");
        //sleep(3);
        delete crp;
        //delete robot;
        exit(1);
    } else {
        printf("Ok polaczone\n");
    }

    robot->SetFrequency(35);
    robot->SetDataMode(PLAYER_DATAMODE_PULL_ALL);
    //robot->SetDataMode(PLAYER_DATAMODE_PUSH_ASYNC);

    out = processed = new unsigned char[1024*768*4];
}


void CaptureCam::setPulMode(int mode){
    const static unsigned char modes[] = {
        PLAYER_DATAMODE_PUSH_ALL,
        PLAYER_DATAMODE_PULL_ALL,
        PLAYER_DATAMODE_PUSH_NEW,
        PLAYER_DATAMODE_PULL_NEW,
        PLAYER_DATAMODE_PUSH_ASYNC,
    };

    robot->SetFrequency(35);

    if (mode<5)
        robot->SetDataMode(modes[mode]);
}
