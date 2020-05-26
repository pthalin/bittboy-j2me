/**

Provides the interface to SDL TTF routines defined in SDL_ttf.h

<h2>Package Specification</h2>

<P>
Note:  Full character set support has been implemented.  If your java code is
compilied with a proper encoding the text should be correctly displayed.  Interally
sdljava is calling RenderUTF8 routined int SDL_ttf and therefore should be able to
display the various character sets.
<P>
Detailed documentation can be found here:

<ul>
  <li><a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_5fttf">SDL TTF API</a>
  <li><a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_20API">SDL API</a>
</ul>


*/
package sdljava.ttf;