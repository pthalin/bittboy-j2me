#include <stdio.h>
#include <stdlib.h>

#include "yuv2rgb.h"

#ifdef __cplusplus__
extern "C" {
#endif /* __cplusplus__ */

// YUV to RGB conversion added May 31 2005 KAL:
unsigned char clip(int value)
{	if (value <= 0)
		return 0;
	else if (value >= 255)
		return 255;
	else
		return (unsigned char) (value);
}

extern double round(double value);

void yuv2rgb(unsigned char y, unsigned char u, unsigned char v, unsigned char *pr, unsigned char *pg, unsigned char *pb)
{
/**
TODO: optimize.
From http://msdn.microsoft.com/library/default.asp?url=/library/en-us/dnwmt/html/YUVFormats.asp


C = Y - 16
D = U - 128
E = V - 128

R = clip( round( 1.164383 * C                   + 1.596027 * E  ) )
G = clip( round( 1.164383 * C - (0.391762 * D) - (0.812968 * E) ) )
B = clip( round( 1.164383 * C +  2.017232 * D                   ) )

where clip() denotes clipping to a range of [0..255]. These formulas can be reasonably approximated by the following:

R = clip(( 298 * C           + 409 * E + 128) >> 8)
G = clip(( 298 * C - 100 * D - 208 * E + 128) >> 8)
B = clip(( 298 * C + 516 * D           + 128) >> 8)

*/

	float c, d, e;
	
	c = y - 16;
	d = u - 128;
	e = v - 128;

	*pr = clip( (int) round( 1.164383 * c                   + 1.596027 * e  ) );
	*pg = clip( (int) round( 1.164383 * c - (0.391762 * d) - (0.812968 * e) ) );
	*pb = clip( (int) round( 1.164383 * c +  2.017232 * d                   ) );

}

void yuv2rgb_buf(unsigned char *src, int width, int height, unsigned char *dst)
{
	int u_offset = width * height;
	int v_offset = u_offset + width * height / 4;	// TODO: handle rows not divisible by 2.
	int uv_width = width / 2;
	int i;
    for ( i = 0; i < ( width * height ); i++ )
    {
		int px = i % width;
		int py = i / width;
		int uv_x = px / 2; // x in u/v planes
		int uv_y = py / 2; // y in u/v planes
		unsigned char y, u, v;
		unsigned char r, g, b;
        y = src[i];

        u = src[u_offset + uv_width * uv_y + uv_x];
        v = src[v_offset + uv_width * uv_y + uv_x];
		yuv2rgb(y, u, v, &r, &g, &b);

		*dst++ = b;
		*dst++ = g;
		*dst++ = r;
	}

}
