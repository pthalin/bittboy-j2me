/**

Provides the interface to SDL event management defined int SDL_events.h

<h2>Package Specification</h2>

NOTE: The following is not yet implemented:
<UL>
  <LI>SDL_SysWMEvent</I>
  <LI>SDL_UserEvent</I>
  <LI>SDL_PumpEvents</I>
  <LI>SDL_PeekEvents</I>
  <LI>SDL_PushEvent</I>
  <LI>SDL_SetEventFilter</I>
  <LI>SDL_GetKeyState</I>
</UL>

<P>
Detailed documentation can be found here:

<ul>
  <li><a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_20API">SDL API</a>
</ul>



<P>
Event management under sdljava has been hightly optimized.  <B>NO NEW OBJECTS</B> will
ever be created by the event methods.  Each time an event is returned from the SDL
layer it will be the same C-level event structure (SDL_Event).  When it is received on the
java side after the native method invocation the SDLEvent class handles the event by
placing into the appropriate static instance variable.
<P>
For instance if a keyboard event is received in waitEvent SDLEvent simply does the following:

<code>
<pre>
sdlKeyboardEvent.setSwigKeyboardEvent(swigEvent.getKey());
return sdlKeyboardEvent;
</code>
</pre>
The event is simply set on the already created static instance and returned to the caller.
<P>
The event handling should be quite fast and usable from java.  Please note that due to the
optimizations the event code (or any code within sdljava for that matter) should not be called
concurrently from multiple threads.

<h3>Usage</h3>
<P>
<B>You can use SDLEvent.waitEvent() to be passed incoming events.</B>  Also look at the example
in SDLEventTest.java in testsrc/sdljava/event directory.

@see sdljava.x.swig.SDLEventType

*/
package sdljava.event;