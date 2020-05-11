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
#ifndef __PLV4L2TOOLS__H_
#define __PLV4L2TOOLS__H_

#ifdef __cplusplus
extern "C"
{
#endif


int convPixS2I(const char *n);
const char * convPixI2S(int n);

int convNormS2I(const char *n);
const char * convNormI2S(int n);

int convFieldS2I(const char *n);
const char * convFieldI2S(int n);


#ifdef __cplusplus
}

#endif
#endif

