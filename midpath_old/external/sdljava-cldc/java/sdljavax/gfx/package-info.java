/**

Provides the interface to SDL_gfx routines defined in SDL_gfxPrimitives.h and SDL_rotozoom.h.
<P>
<I>NOTE: Support for the SDL_imageFilter.h routines is not yet implemented but planned.</I>
<P>
<I>NOTE: Support for the framerate routines is not implemented, I'm not sure if it makes sense
to provide there here</I>

<h2>Package Specification</h2>

<B><I>Note: all ___Color routines expect the color to be in format 0xRRGGBBAA </B></I>

<H3>Notes on Rotozoomer</h3>
The rotozoom without interpolation code should be fast enough even for some realtime effects if the CPU is fast or bitmaps small. With interpolation the routines are typically used for pre-rendering stuff in higher quality (i.e. smoothing) - that's also a reason why the API differs from SDL_BlitRect() and creates a new target surface each time rotozoom is called. The final rendering speed is dependent on the target surface size as it is beeing xy-scanned when rendering the new surface.
<P>
Note also that the smoothing toggle is dependent on the input surface bit depth. 8bit surfaces will never be smoothed - only 32bit surfaces will.
<P>
Note that surfaces of other bit depth then 8 and 32 will be converted on the fly to a 32bit surface using a blit into a temporary surface. This impacts performance somewhat.

<ul>
  <li><a href="http://www.ferzkopp.net/~aschiffler/Software/SDL_gfx-2.0/">SDL GFX</a>
</ul>


*/
package sdljavax.gfx;