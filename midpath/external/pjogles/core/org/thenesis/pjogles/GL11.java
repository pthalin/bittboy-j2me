/*
 * PJOGLES - Copyright (C) 2008 Guillaume Legris, Mathieu Legris
 * 
 * OGLJava - Copyright (C) 2004 Tom Dinneen
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA  
 */

package org.thenesis.pjogles;

public interface GL11 extends GL11Constants {
	
//  public boolean glXMakeCurrent(GLContext gc, Drawable drawable);
	//
	//    public void glXSwapBuffers(X11 x11, Display display, Drawable drawable);

	/////////////////////// Misc ///////////////////////

	/**
	 * <strong>glRenderMode</strong> sets the rasterization mode. It takes one
	 * argument, <strong>mode</strong>, which can assume one of three predefined
	 * values:
	 * <p/>
	 * <ul>
	 * <li> <strong>GL_RENDER</strong> Render mode. Primitives are rasterized,
	 * producing pixel fragments, which are written
	 * into the frame buffer.  This is the normal
	 * mode and also	the default mode.
	 * <li> <strong>GL_SELECT</strong>	  Selection mode.  No pixel fragments are
	 * produced, and	no change to the frame buffer
	 * contents is made.  Instead, a	record of the
	 * names	of primitives that would have been
	 * drawn	if the render mode had been <strong>GL_RENDER</strong>
	 * is returned in a select buffer, which	must
	 * be created (see {@link #glSelectBuffer glSelectBuffer}) before
	 * selection mode is entered.
	 * <li> <strong>GL_FEEDBACK</strong> Feedback mode.  No pixel fragments are
	 * produced, and	no change to the frame buffer
	 * contents is made.  Instead, the coordinates
	 * and attributes of vertices that would	have
	 * been drawn if	the render mode	had been
	 * <strong>GL_RENDER</strong> is returned	in a feedback buffer,
	 * which	must be	created	(see {@link #glFeedbackBuffer glFeedbackBuffer})
	 * before feedback mode is entered.
	 * </ul>
	 * <p/>
	 * See {@link #glSelectBuffer glSelectBuffer} and {@link #glFeedbackBuffer glFeedbackBuffer}
	 * for more details concerning selection and feedback operation.
	 * <p/>
	 * <strong>NOTES - </strong> If an error is generated, <strong>glRenderMode</strong> returns 0 regardless
	 * of the current render mode.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if <em>mode</em> is not one of the three
	 * accepted values.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glSelectBuffer</strong> is
	 * called while the render mode is <strong>GL_SELECT</strong>, or	if
	 * <strong>glRenderMode</strong> is called with argument <strong>GL_SELECT</strong> before
	 * <strong>glSelectBuffer</strong> is called at least once.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glFeedbackBuffer</strong>	is
	 * called while the render mode is <strong>GL_FEEDBACK</strong>, or if
	 * <strong>glRenderMode</strong> is called with argument <strong>GL_FEEDBACK</strong> before
	 * <strong>glFeedbackBuffer</strong> is called at	least once.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glRenderMode</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_RENDER_MODE</strong>
	 * </ul>
	 *
	 * @param mode Specifies the rasterization mode. Three values are
	 *             accepted:  <strong>GL_RENDER</strong>, <strong>GL_SELECT</strong>, and <strong>GL_FEEDBACK</strong>. The
	 *             default	value is <strong>GL_RENDER</strong>.
	 * @return The return value of <strong>glRenderMode</strong> is determined by the	render
	 *         mode at the time <strong>glRenderMode</strong>	is called, rather than by
	 *         <em>mode</em>. The values returned for the three render modes	are as
	 *         follows:
	 *         <ul>
	 *         <li><strong>GL_RENDER</strong>	  0.
	 *         <p/>
	 *         <li><strong>GL_SELECT</strong>	  The number of	hit records transferred	to the
	 *         select buffer.
	 *         <p/>
	 *         <li><strong>GL_FEEDBACK</strong>	  The number of	values (not vertices)
	 *         transferred to the feedback buffer.
	 *         </ul>
	 * @see #glSelectBuffer glSelectBuffer
	 * @see #glFeedbackBuffer glFeedbackBuffer
	 * @see #glInitNames glInitNames
	 * @see #glLoadName glLoadName
	 * @see #glPassThrough glPassThrough
	 * @see #glPushName glPushName
	 */
	public int glRenderMode(int mode);

	/**
	 * Certain aspects of <strong>GL</strong>	behavior, when there is	room for
	 * interpretation, can be controlled with hints.	 A hint	is
	 * specified with two arguments.	 <em>target</em>	is a symbolic constant
	 * indicating the behavior to be	controlled, and	<em>mode</em> is
	 * another symbolic constant indicating the desired behavior.
	 * The initial value for	each target is <strong>GL_DONT_CARE</strong>. mode can
	 * be one of the following:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_FASTEST</strong>	      The most efficient option	should be
	 * chosen.
	 * <p/>
	 * <li><strong>GL_NICEST</strong>	      The most correct,	or highest quality,
	 * option should be chosen.
	 * <p/>
	 * <li><strong>GL_DONT_CARE</strong>	      No preference.
	 * </ul>
	 * <p/>
	 * Though the implementation aspects that can be	hinted are
	 * well defined,	the interpretation of the hints	depends	on the
	 * implementation.  The hint aspects that can be	specified with
	 * target, along	with suggested semantics, are as follows:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_FOG_HINT</strong>	      Indicates	the accuracy of	fog
	 * calculation.  If per-pixel fog
	 * calculation is not efficiently supported
	 * by the GL	implementation,	hinting
	 * <strong>GL_DONT_CARE</strong> or <strong>GL_FASTEST</strong> can result in
	 * per-vertex calculation of	fog effects.
	 * <p/>
	 * <li><strong>GL_LINE_SMOOTH_HINT</strong> Indicates	the sampling quality of
	 * antialiased lines.  If a larger filter
	 * function is applied, hinting <strong>GL_NICEST</strong>
	 * can result in more pixel fragments being
	 * generated	during rasterization,
	 * <p/>
	 * <li><strong>GL_PERSPECTIVE_CORRECTION_HINT</strong>
	 * Indicates	the quality of color and
	 * texture coordinate interpolation.	 If
	 * perspective-corrected parameter
	 * interpolation is not efficiently
	 * supported	by the GL implementation,
	 * hinting <strong>GL_DONT_CARE</strong> or <strong>GL_FASTEST</strong> can
	 * result in	simple linear interpolation of
	 * colors and/or texture coordinates.
	 * <p/>
	 * <li><strong>GL_POINT_SMOOTH_HINT</strong>
	 * Indicates	the sampling quality of
	 * antialiased points.  If a	larger filter
	 * function is applied, hinting <strong>GL_NICEST</strong>
	 * can result in more pixel fragments being
	 * generated	during rasterization,
	 * <p/>
	 * <li><strong>GL_POLYGON_SMOOTH_HINT</strong>
	 * Indicates	the sampling quality of
	 * antialiased polygons.  Hinting <strong>GL_NICEST</strong>
	 * can result in more pixel fragments being
	 * generated	during rasterization, if a
	 * larger filter function is	applied.
	 * </ul>
	 * <p/>
	 * <strong>NOTES - </strong> The interpretation of	hints depends on the implementation.
	 * Some implementations ignore <strong>glHint</strong> settings.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if either target	or mode	is not
	 * an accepted value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glHint</strong> is executed
	 * between the execution of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 *
	 * @param target Specifies a symbolic constant	indicating the
	 *               behavior to be controlled. <strong>GL_FOG_HINT</strong>,
	 *               <strong>GL_LINE_SMOOTH_HINT</strong>, <strong>GL_PERSPECTIVE_CORRECTION_HINT</strong>,
	 *               <strong>GL_POINT_SMOOTH_HINT</strong>,	and <strong>GL_POLYGON_SMOOTH_HINT</strong> are
	 *               accepted.
	 * @param mode   Specifies a symbolic constant indicating the desired
	 *               behavior. <strong>GL_FASTEST</strong>, <strong>GL_NICEST</strong>, and <strong>GL_DONT_CARE</strong>
	 *               are accepted.
	 */
	public void glHint(int target, int mode);

	/**
	 * When colors are written to the frame buffer, they are
	 * written into the color buffers specified by <strong>glDrawBuffer</strong>.
	 * The specifications are as follows:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_NONE</strong>		   No color buffers are	written.
	 * <p/>
	 * <li><strong>GL_FRONT_LEFT</strong>		   Only	the front left color buffer is
	 * written.
	 * <p/>
	 * <li><strong>GL_FRONT_RIGHT</strong>	   Only	the front right	color buffer
	 * is written.
	 * <p/>
	 * <li><strong>GL_BACK_LEFT</strong>		   Only	the back left color buffer is
	 * written.
	 * <p/>
	 * <li><strong>GL_BACK_RIGHT</strong>		   Only	the back right color buffer is
	 * written.
	 * <p/>
	 * <li><strong>GL_FRONT</strong>		   Only	the front left and front right
	 * color buffers are written.  If
	 * there is no front right color
	 * buffer, only	the front left color
	 * buffer is written.
	 * <p/>
	 * <li><strong>GL_BACK</strong>		   Only	the back left and back right
	 * color buffers are written.  If
	 * there is no back right color
	 * buffer, only	the back left color
	 * buffer is written.
	 * <p/>
	 * <li><strong>GL_LEFT</strong>		   Only	the front left and back	left
	 * color buffers are written.  If
	 * there is no back left color buffer,
	 * only	the front left color buffer is
	 * written.
	 * <p/>
	 * <li><strong>GL_RIGHT</strong>		   Only	the front right	and back right
	 * color buffers are written.  If
	 * there is no back right color
	 * buffer, only	the front right	color
	 * buffer is written.
	 * <p/>
	 * <li><strong>GL_FRONT_AND_BACK</strong>	   All the front and back color
	 * buffers (front left,	front right,
	 * back	left, back right) are written.
	 * If there are	no back	color buffers,
	 * only	the front left and front right
	 * color buffers are written.  If
	 * there are no	right color buffers,
	 * only	the front left and back	left
	 * color buffers are written.  If
	 * there are no	right or back color
	 * buffers, only the front left	color
	 * buffer is written.
	 * <p/>
	 * <li><strong>GL_AUXi</strong>	Only	auxiliary color	buffer i is
	 * written.
	 * </ul>
	 * <p/>
	 * If more than one color buffer is selected for	drawing, then
	 * blending or logical operations are computed and applied
	 * independently	for each color buffer and can produce
	 * different results in each buffer.
	 * <p/>
	 * Monoscopic contexts include only left buffers, and
	 * stereoscopic contexts include both left and right buffers.
	 * Likewise, single-buffered contexts include only front
	 * buffers, and double-buffered contexts include	both front and
	 * back buffers. The context is	selected at <strong>GL</strong> initialization.
	 * <p/>
	 * <strong>NOTES - </strong> It is	always the case	that <strong>GL_AUXi</strong> = <strong>GL_AUX0</strong> + i.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if mode is not an accepted
	 * value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if none of the buffers
	 * indicated by mode exists.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glDrawBuffer</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding	execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_DRAW_BUFFER</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_AUX_BUFFERS</strong>
	 * </ul>
	 *
	 * @param mode Specifies up to	four color buffers to be drawn into.
	 *             Symbolic constants <strong>GL_NONE</strong>, <strong>GL_FRONT_LEFT</strong>,
	 *             <strong>GL_FRONT_RIGHT</strong>,	<strong>GL_BACK_LEFT</strong>, <strong>GL_BACK_RIGHT</strong>, <strong>GL_FRONT</strong>,
	 *             <strong>GL_BACK</strong>, <strong>GL_LEFT</strong>, <strong>GL_RIGHT</strong>, <strong>GL_FRONT_AND_BACK</strong>, and
	 *             <strong>GL_AUXi</strong>, where i is between 0 and <strong>GL_AUX_BUFFERS</strong>
	 *             -1, are	accepted (<strong>GL_AUX_BUFFERS</strong> is not the upper
	 *             limit; use glGet to query the number of	available aux
	 *             buffers).  The initial value is	<strong>GL_FRONT</strong> for single-buffered contexts, and <strong>GL_BACK</strong> for double-buffered
	 *             contexts.
	 * @see #glBlendFunc glBlendFunc
	 * @see #glColorMask glColorMask
	 * @see #glIndexMask glIndexMask
	 * @see #glLogicOp glLogicOp
	 * @see #glReadBuffer glReadBuffer
	 */
	public void glDrawBuffer(int mode);

	/**
	 * <strong>glReadBuffer</strong> specifies a color buffer as the source for
	 * subsequent {@link #glReadPixels glReadPixels}, {@link #glCopyTexImage1D glCopyTexImage1D}, {@link #glCopyTexImage2D glCopyTexImage2D},
	 * {@link #glCopyTexSubImage1D glCopyTexSubImage1D}, {@link #glCopyTexSubImage2D glCopyTexSubImage2D}, and	{@link #glCopyPixels glCopyPixels}
	 * commands. mode accepts one of	twelve or more predefined
	 * values (<strong>GL_AUX0</strong> through <strong>GL_AUX3</strong> are	always defined). In a
	 * fully	configured system, <strong>GL_FRONT</strong>, <strong>GL_LEFT</strong>, and
	 * <strong>GL_FRONT_LEFT</strong>	all name the front left	buffer,	<strong>GL_FRONT_RIGHT</strong>
	 * and <strong>GL_RIGHT</strong> name the	front right buffer, and	<strong>GL_BACK_LEFT</strong>
	 * and <strong>GL_BACK</strong> name the back left buffer.
	 * <p/>
	 * Nonstereo double-buffered configurations have	only a front
	 * left and a back left buffer.	Single-buffered	configurations
	 * have a front left and	a front	right buffer if	stereo,	and
	 * only a front left buffer if nonstereo.  It is	an error to
	 * specify a nonexistent buffer to <strong>glReadBuffer</strong>.
	 * <p/>
	 * <em>mode</em> is initially <strong>GL_FRONT</strong> in	single-buffered
	 * configurations, and <strong>GL_BACK</strong> in double-buffered
	 * configurations.
	 *
	 * @param mode Specifies a color buffer. Accepted values are
	 *             <strong>GL_FRONT_LEFT</strong>, <strong>GL_FRONT_RIGHT</strong>, <strong>GL_BACK_LEFT</strong>,
	 *             <strong>GL_BACK_RIGHT</strong>, <strong>GL_FRONT</strong>, <strong>GL_BACK</strong>, <strong>GL_LEFT</strong>, <strong>GL_RIGHT</strong>,
	 *             and <strong>GL_AUXi</strong>, where i is between 0 and <strong>GL_AUX_BUFFERS</strong> - 1.
	 */
	public void glReadBuffer(int mode);

	/**
	 * <strong>glScissor</strong> defines a rectangle, called the scissor box, in
	 * window coordinates.  The first two arguments, x and y,
	 * specify the lower left corner of the box.  width and height
	 * specify the width and height of the box.
	 * <p/>
	 * To enable and disable the scissor test, call <strong>glEnable</strong> and
	 * <strong>glDisable</strong> with argument <strong>GL_SCISSOR_TEST</strong>. The test is
	 * initially disabled. While the test is enabled, only pixels
	 * that lie within the scissor box can be modified by drawing
	 * commands. Window coordinates have integer values at the
	 * shared corners of frame buffer pixels. <strong>glScissor(0,0,1,1)</strong>
	 * allows modification of only the lower left pixel in the
	 * window, and <strong>glScissor(0,0,0,0)</strong> doesn't allow modification of
	 * any pixels in the window.
	 * <p/>
	 * When the scissor test is disabled, it is as though the
	 * scissor box includes the entire window.
	 * <p/>
	 * <strong>NOTES - </strong> When a GL context is first attached to a window, width and
	 * height are set to the dimensions of that window.
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_VALUE</strong> is generated if either width or height is
	 * negative.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if none of the buffers
	 * indicated by mode exists.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glScissor</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_SCISSOR_BOX</strong>
	 * <li><strong>glIsEnabled</strong> with argument <strong>GL_SCISSOR_TEST</strong>
	 * </ul>
	 *
	 * @param x Specifies the x coordinate of the lower left corner of the scissor box, initially 0.
	 * @param y Specifies the y coordinate of the lower left corner of the scissor box, initially 0.
	 * @param width Specifies the width of the scissor box.
	 * @param height Specifies the height of the scissor box.
	 *
	 * @see #glEnable glEnable
	 * @see #glViewport glViewport
	 */
	public void glScissor(int x, int y, int width, int height);

	/////////////////////// Masks ///////////////////////

	/**
	 * <strong>glColorMask</strong> specifies whether	the individual color
	 * components in the frame buffer can or	cannot be written.  If
	 * red is <strong>GL_FALSE</strong>, for example, no change is made to the red
	 * component of any pixel in any of the color buffers,
	 * regardless of the drawing operation attempted.
	 * <p/>
	 * Changes to individual bits of components cannot be
	 * controlled.  Rather, changes are either enabled or disabled
	 * for entire color components.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glColorMask</strong> is executed
	 * between the execution of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_COLOR_WRITEMASK</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_RGBA_MODE</strong>
	 * </ul>
	 *
	 * @param red   Specify	whether	red can or cannot be written into
	 *              the frame buffer.  The initial value
	 *              is	<strong>GL_TRUE</strong>, indicating that the
	 *              red color component can be	written.
	 * @param green Specify	whether	green can or cannot be written into
	 *              the frame buffer.  The initial value
	 *              is	<strong>GL_TRUE</strong>, indicating that the
	 *              green color component can be	written.
	 * @param blue  Specify	whether	blue can or cannot be written into
	 *              the frame buffer.  The initial value
	 *              is	<strong>GL_TRUE</strong>, indicating that the
	 *              blue color component can be	written.
	 * @param alpha Specify	whether	alpha can or cannot be written into
	 *              the frame buffer.  The initial value
	 *              is	<strong>GL_TRUE</strong>, indicating that the
	 *              alpha color component can be written.
	 * @see #glColor4f glColor4f
	 * @see #glColorPointer glColorPointer
	 * @see #glDepthMask glDepthMask
	 * @see #glIndexf glIndexf
	 * @see #glIndexPointer glIndexPointer
	 * @see #glIndexMask glIndexMask
	 * @see #glStencilMask glStencilMask
	 */
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha);

	/////////////////////// Clear ///////////////////////

	/**
	 * <strong>glClearColor</strong> specifies the red, green, blue, and alpha
	 * values used by {@link #glClear glClear} to clear the color buffers. Values
	 * specified by <strong>glClearColor</strong> are clamped to the range [0,1].
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glClearColor</strong> is
	 * executed between the execution of <strong>glBegin</strong> and the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul><li><strong>glGet</strong> with argument <strong>GL_COLOR_CLEAR_VALUE</strong></ul>
	 *
	 * @param red   Specifies the red value used when the color
	 *              buffers	are cleared.  The initial
	 *              value is 0.
	 * @param green Specifies the green value used when the color
	 *              buffers	are cleared.  The initial
	 *              value is 0.
	 * @param blue  Specifies the blue value used when the color
	 *              buffers	are cleared.  The initial
	 *              value is 0.
	 * @param alpha Specifies the alpha value used when the color
	 *              buffers	are cleared.  The initial
	 *              value is 0.
	 * @see #glClear glClear
	 */
	public void glClearColor(float red, float green, float blue, float alpha);

	/**
	 * <strong>glClearIndex</strong> specifies the index used by {@link #glClear glClear} to clear
	 * the color index buffers. c is not clamped.  Rather, c is
	 * converted to a fixed-point value with	unspecified precision
	 * to the right of the binary point.  The integer part of this
	 * value is then masked with 2m-1, where m is the number of
	 * bits in a color index stored in the frame buffer.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glClearIndex</strong> is
	 * executed between the execution of <strong>glBegin</strong> and the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_INDEX_CLEAR_VALUE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_INDEX_BITS</strong>
	 * </ul>
	 *
	 * @param c Specifies the index used when the color index buffers are
	 *          cleared. The initial value is 0.
	 * @see #glClear glClear
	 */
	public void glClearIndex(final float c);

	/**
	 * <strong>glClear</strong> sets the bitplane area of the	window to values
	 * previously selected by {@link #glClearColor glClearColor}, {@link #glClearIndex glClearIndex},
	 * {@link #glClearDepth glClearDepth}, {@link #glClearStencil glClearStencil}, and {@link #glClearAccum glClearAccum}.
	 * Multiple color buffers can be cleared simultaneously by selecting
	 * more than one buffer at a time using {@link #glDrawBuffer glDrawBuffer}.
	 * <p/>
	 * The pixel ownership test, the scissor	test, dithering, and
	 * the buffer writemasks affect the operation of <strong>glClear</strong>.  The
	 * scissor box bounds the cleared region.  Alpha function,
	 * blend function, logical operation, stenciling, texture
	 * mapping, and depth-buffering are ignored by <strong>glClear</strong>.
	 * <p/>
	 * <strong>glClear</strong> takes	a single argument that is the bitwise <strong>OR</strong> of
	 * several values indicating which buffer is to be cleared.
	 * <p/>
	 * The values are as follows:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_COLOR_BUFFER_BIT</strong> Indicates the buffers currently enabled for	color writing.
	 * <li><strong>GL_DEPTH_BUFFER_BIT</strong> Indicates the depth	buffer.
	 * <li><strong>GL_ACCUM_BUFFER_BIT</strong>	Indicates the accumulation buffer.
	 * <li><strong>GL_STENCIL_BUFFER_BIT</strong> Indicates the stencil buffer.
	 * </ul>
	 * <p/>
	 * The value to which each buffer is cleared depends on the
	 * setting of the clear value for that buffer.
	 * <p/>
	 * <strong>NOTES - </strong> If a buffer is not present, then a <strong>glClear</strong>
	 * directed at that  buffer has no effect.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_VALUE</strong> is generated	if any bit other than the four
	 * defined bits is set in mask.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glClear</strong> is executed
	 * between the execution of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_ACCUM_CLEAR_VALUE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_DEPTH_CLEAR_VALUE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_INDEX_CLEAR_VALUE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_COLOR_CLEAR_VALUE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_STENCIL_CLEAR_VALUE</strong>
	 * </ul>
	 *
	 * @param mask Bitwise <strong>OR</strong> of masks that indicate the buffers to be
	 *             cleared. The four masks are <strong>GL_COLOR_BUFFER_BIT</strong>,
	 *             <strong>GL_DEPTH_BUFFER_BIT</strong>, <strong>GL_ACCUM_BUFFER_BIT</strong>, and
	 *             <strong>GL_STENCIL_BUFFER_BIT</strong>.
	 * @see #glClearAccum glClearAccum
	 * @see #glClearColor glClearColor
	 * @see #glClearDepth glClearDepth
	 * @see #glClearIndex glClearIndex
	 * @see #glClearStencil glClearStencil
	 * @see #glDrawBuffer glDrawBuffer
	 * @see #glScissor glScissor
	 */
	public void glClear(int mask);

	/////////////////////// Transforms ///////////////////////

	/**
	 * <strong>glTranslatef</strong> produces a translation by (x, y, z).  The current
	 * matrix (see {@link #glMatrixMode glMatrixMode}) is multiplied by this translation matrix, with
	 * the product replacing the current matrix, as if {@link #glMultMatrixf glMultMatrixf}
	 * were called with the following matrix for its argument:
	 * <pre>
	 *     ( 1  0  0  x )
	 *     ( 0  1  0  y )
	 *     ( 0  0  1  z )
	 *     ( 0  0  0  1 ) </pre>
	 * If the matrix mode is either <strong>GL_MODELVIEW</strong> or <strong>GL_PROJECTION</strong>,
	 * all objects drawn after a call to <strong>glTranslatef</strong> are
	 * translated.
	 * <p/>
	 * Use {@link #glPushMatrix glPushMatrix} and {@link #glPopMatrix glPopMatrix} to save and restore the
	 * untranslated coordinate system.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glTranslatef</strong> is executed
	 * between the execution of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * </ul>
	 *
	 * @param x Specifies the x coordinate of a translation vector.
	 * @param y Specifies the y coordinate of a translation vector.
	 * @param z Specifies the z coordinate of a translation vector.
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrixf
	 * @see #glPushMatrix glPushMatrix
	 * @see #glRotatef glRotatef
	 * @see #glScalef glScalef
	 */
	public void glTranslatef(float x, float y, float z);

	/**
	 * <strong>glScalef</strong> produces a nonuniform	scaling	along the x, y,	and z
	 * axes.	The three parameters indicate the desired scale	factor
	 * along	each of	the three axes.
	 * <p/>
	 * The current matrix (see {@link #glMatrixMode glMatrixMode})	is multiplied by this
	 * scale	matrix,	and the	product	replaces the current matrix as
	 * if <strong>glScalef</strong> were called with the following matrix as its
	 * argument:
	 * <pre>
	 *     ( x  0  0  0 )
	 *     ( 0  y  0  0 )
	 *     ( 0  0  z  0 )
	 *     ( 0  0  0  1 ) </pre>
	 * If the matrix mode is either <strong>GL_MODELVIEW</strong> or <strong>GL_PROJECTION</strong>,
	 * all objects drawn after <strong>glScalef</strong> is called are	scaled.
	 * <p/>
	 * Use {@link #glPushMatrix glPushMatrix} and {@link #glPopMatrix glPopMatrix} to save and restore the
	 * unscaled coordinate system.
	 * <p/>
	 * <strong>NOTES - </strong> If scale factors other than 1 are applied to the modelview
	 * matrix and lighting is enabled, lighting often appears
	 * wrong. In that case, enable automatic normalization of
	 * normals by calling {@link #glEnable glEnable} with the argument <strong>GL_NORMALIZE</strong>.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glScalef</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * </ul>
	 *
	 * @param x Specifies the scale factors along the x axes.
	 * @param y Specifies the scale factors along the y axes.
	 * @param z Specifies the scale factors along the z axes.
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrixf
	 * @see #glPushMatrix glPushMatrix
	 * @see #glRotatef glRotatef
	 * @see #glTranslatef glTranslatef
	 */
	public void glScalef(float x, float y, float z);

	/**
	 * <strong>glRotatef</strong> produces a rotation of angle	degrees	around the
	 * vector (x, y, z). The current matrix (see {@link #glMatrixMode glMatrixMode}) is
	 * multiplied by a rotation matrix with the product replacing
	 * the current matrix, as if {@link #glMultMatrixf glMultMatrixf} were called with the
	 * following matrix as its argument:
	 * <pre>
	 *     ( xx(1-c)+c   xy(1-c)-zs  xz(1-c)+ys  0 )
	 *     ( yx(1-c)+zs  yy(1-c)+c   yz(1-c)-xs  0 )
	 *     ( xz(1-c)-ys  yz(1-c)+xs  zz(1-c)+c   0 )
	 *     (     0           0           0       1 ) </pre>
	 * Where c = cos(angle), s = sine(angle), and ||(x, y, z)|| = 1
	 * (if not, then <strong>GL</strong> will normalize this vector).
	 * <p/>
	 * If the matrix mode is either <strong>GL_MODELVIEW</strong> or <strong>GL_PROJECTION</strong>,
	 * all objects drawn after <strong>glRotatef</strong> is called are rotated. Use
	 * {@link #glPushMatrix glPushMatrix} and {@link #glPopMatrix glPopMatrix} to save and restore the
	 * unrotated coordinate system.
	 * <p/>
	 * <strong>NOTES - </strong> This rotation	follows	the right-hand rule, so	if the vector
	 * (x, y, z) points toward	the user, the rotation will be counterclockwise.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glRotatef</strong>	is executed
	 * between the execution of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * </ul>
	 *
	 * @param angle Specifies the angle of rotation, in degrees.
	 * @param x     Specify the x coordinate of	a vector.
	 * @param y     Specify the y coordinate of	a vector.
	 * @param z     Specify the z coordinate of	a vector.
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrixf
	 * @see #glPushMatrix glPushMatrix
	 * @see #glScalef glScalef
	 * @see #glTranslatef glTranslatef
	 */
	public void glRotatef(float angle, float x, float y, float z);

	/////////////////////// Matrix ///////////////////////

	/**
	 * <strong>glMatrixMode</strong> sets the current matrix mode. <strong>mode</strong> can assume
	 * one of three values:
	 * <ul>
	 * <li><strong>GL_MODELVIEW</strong> Applies subsequent matrix operations to the modelview matrix stack.
	 * <li><strong>GL_PROJECTION</strong> Applies subsequent matrix operations to the projection matrix stack.
	 * <li><strong>GL_TEXTURE</strong> Applies subsequent matrix operations to the texture matrix stack.
	 * </ul>
	 * <p/>
	 * To find out which matrix stack is currently the target of
	 * all matrix operations, call <strong>glGet</strong> with argument
	 * <strong>GL_MATRIX_MODE</strong>. The initial value is <strong>GL_MODELVIEW</strong>.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if mode is not an accepted value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glMatrixMode</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * </ul>
	 *
	 * @param mode Specifies which	matrix stack is	the target for
	 *             subsequent matrix operations. Three values are
	 *             accepted: <strong>GL_MODELVIEW</strong>, <strong>GL_PROJECTION</strong>,	and
	 *             <strong>GL_TEXTURE</strong>. The initial value is <strong>GL_MODELVIEW</strong>.
	 * @see #glLoadMatrixf glLoadMatrixf
	 * @see #glPushMatrix glPushMatrix
	 */
	public void glMatrixMode(int mode);

	/**
	 * <strong>glLoadIdentity</strong> replaces the current matrix with the identity
	 * matrix.  It is semantically equivalent to calling
	 * {@link #glLoadMatrixf glLoadMatrixf} with the identity matrix
	 * <pre>
	 *     ( 1  0  0  0 )
	 *     ( 0  1  0  0 )
	 *     ( 0  0  1  0 )
	 *     ( 0  0  0  1 ) </pre>
	 * but in some cases it is more efficient.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glLoadIdentity</strong> is
	 * executed between the execution of <strong>glBegin</strong> and the
	 * correspondingexecution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * </ul>
	 *
	 * @see #glLoadMatrixf glLoadMatrixf
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrixf
	 * @see #glPushMatrix glPushMatrix
	 */
	public void glLoadIdentity();

	/**
	 * <strong>glLoadMatrix</strong> replaces the current matrix with	the one	whose
	 * elements are specified by m.	The current matrix is the
	 * projection matrix, modelview matrix, or texture matrix,
	 * depending on the current matrix mode (see {@link #glMatrixMode glMatrixMode}).
	 * <p/>
	 * The current matrix, M,  defines a transformation of
	 * coordinates.	For instance, assume M refers to the modelview
	 * matrix.  If  v = (v[0], v[1], v[2], v[3]) is the	set of object
	 * coordinates of a vertex, and m points to an array of 16
	 * floating-point values m[0], m[1], ..., m[15], then the modelview transformation M(v)
	 * does the following:
	 * <pre>
	 *            ( m[0]  m[4]  m[8]   m[12] )   ( v[0] )
	 *            ( m[1]  m[5]  m[9]   m[13] )   ( v[1] )
	 *     M(v) = ( m[2]  m[6]  m[10]  m[14] ) x ( v[2] )
	 *            ( m[3]  m[7]  m[11]  m[15] )   ( v[3] ) </pre>
	 * Where 'x' denotes matrix multiplication.
	 * <p/>
	 * Projection and texture transformations are similarly defined.
	 * <p/>
	 * <strong>NOTES - </strong> While the elements of	the matrix may be specified with
	 * single or double precision, the GL implementation may	store
	 * or operate on	these values in	less than single precision.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glLoadMatrix</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * </ul>
	 *
	 * @param m Specifies an array to 16 consecutive values, which are
	 *          used as the elements of a 4x4 column-major matrix.
	 * @see #glLoadIdentity glLoadIdentity
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrixf
	 * @see #glPushMatrix glPushMatrix
	 */
	public void glLoadMatrixf(float m[]);

	/**
	 * <strong>glPushMatrix</strong> pushes the current matrix stack down by one,
	 * duplicating the current matrix. That is, after a
	 * <strong>glPushMatrix</strong> call, the matrix on top of the stack is
	 * identical to the one below it.
	 * <p/>
	 * There is a stack of matrices for each of the matrix modes.
	 * In <strong>GL_MODELVIEW</strong> mode, the stack depth is at least 32.	 In
	 * the other two modes, <strong>GL_PROJECTION</strong> and <strong>GL_TEXTURE</strong>, the depth
	 * is at least 2. The current matrix in	any mode is the	matrix
	 * on the top of the stack for that mode.
	 * <p/>
	 * Initially, each of the stacks contains one matrix, an
	 * identity matrix.
	 * <p/>
	 * It is an error to push a full matrix stack, or to pop a
	 * matrix stack that contains only a single matrix. In either
	 * case, the error flag is set and no other change is made to
	 * <strong>GL</strong> state.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_STACK_OVERFLOW</strong> is generated if <strong>glPushMatrix</strong> is called
	 * while the current matrix stack is full.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glPushMatrix</strong>
	 * is executed between the execution of <strong>glBegin</strong> and
	 * the corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MAX_MODELVIEW_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MAX_PROJECTION_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MAX_TEXTURE_STACK_DEPTH</strong>
	 * </ul>
	 *
	 * @see #glFrustum glFrustum
	 * @see #glLoadIdentity glLoadIdentity
	 * @see #glLoadMatrixf glLoadMatrixf
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrixf
	 * @see #glOrtho glOrtho
	 * @see #glRotatef glRotatef
	 * @see #glScalef glScalef
	 * @see #glTranslatef glTranslatef
	 * @see #glViewport glViewport
	 */
	public void glPushMatrix();

	/**
	 * <strong>glPopMatrix</strong> pops the current matrix stack, replacing the
	 * current matrix with the one below it on the stack.
	 * <p/>
	 * There is a stack of matrices for each of the matrix modes.
	 * In <strong>GL_MODELVIEW</strong> mode, the stack depth is at least 32.	 In
	 * the other two modes, <strong>GL_PROJECTION</strong> and <strong>GL_TEXTURE</strong>, the depth
	 * is at least 2. The current matrix in	any mode is the	matrix
	 * on the top of the stack for that mode.
	 * <p/>
	 * Initially, each of the stacks contains one matrix, an
	 * identity matrix.
	 * <p/>
	 * It is an error to push a full matrix stack, or to pop a
	 * matrix stack that contains only a single matrix. In either
	 * case, the error flag is set and no other change is made to
	 * <strong>GL</strong> state.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_STACK_UNDERFLOW</strong> is	generated if <strong>glPopMatrix</strong> is called
	 * while the current matrix stack contains only a single matrix.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glPopMatrix</strong>
	 * is executed between the execution of <strong>glBegin</strong> and
	 * the corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MAX_MODELVIEW_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MAX_PROJECTION_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MAX_TEXTURE_STACK_DEPTH</strong>
	 * </ul>
	 *
	 * @see #glFrustum glFrustum
	 * @see #glLoadIdentity glLoadIdentity
	 * @see #glLoadMatrixf glLoadMatrixf
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrixf
	 * @see #glOrtho glOrtho
	 * @see #glRotatef glRotatef
	 * @see #glScalef glScalef
	 * @see #glTranslatef glTranslatef
	 * @see #glViewport glViewport
	 */
	public void glPopMatrix();

	/**
	 * <strong>glMultMatrix</strong> multiplies the current matrix with the one
	 * specified using <strong>m</strong>, and replaces the current matrix with the
	 * product.
	 * <p/>
	 * The current matrix is determined by the current matrix mode
	 * (see {@link #glMatrixMode glMatrixMode}). It is either the projection matrix,
	 * modelview matrix, or the texture matrix.
	 * EXAMPLES
	 * If the current matrix	is C, and the coordinates to be
	 * transformed are, v=(v[0],v[1],v[2],v[3]).  Then the current
	 * transformation is C x	v, or
	 * <pre>
	 *      ( c[0]  c[4]  c[8]   c[12] )   ( v[0] )
	 *      ( c[1]  c[5]  c[9]   c[13] )   ( v[1] )
	 *      ( c[2]  c[6]  c[10]  c[14] ) x ( v[2] )
	 *      ( c[3]  c[7]  c[11]  c[15] )   ( v[3] ) </pre>
	 * <p/>
	 * Calling glMultMatrix with an argument of
	 * m = m[0], m[1], ..., m[15] replaces the current transformation
	 * with (C x M) x v, or
	 * <pre>
	 *     ( c[0]  c[4]  c[8]   c[12] )   ( m[0]  m[4]  m[8]   m[12] )   ( v[0] )
	 *     ( c[1]  c[5]  c[9]   c[13] )   ( m[1]  m[5]  m[9]   m[13] )   ( v[1] )
	 *     ( c[2]  c[6]  c[10]  c[14] ) x ( m[2]  m[6]  m[10]  m[14] ) x ( v[2] )
	 *     ( c[3]  c[7]  c[11]  c[15] )   ( m[3]  m[7]  m[11]  m[15] )   ( v[3] ) </pre>
	 * Where 'x' denotes matrix multiplication, and v is represented as a 4 x 1 matrix.
	 * <p/>
	 * <strong>NOTES - </strong> While the elements of the matrix may be specified with
	 * single or double precision, the GL may store or operate on
	 * these values in less than single precision.
	 * <p/>
	 * In many computer languages 4x4 arrays are represented	in
	 * row-major order. The transformations just described
	 * represent these matrices in column-major order.  The order
	 * of the multiplication is important. For example, if the
	 * current transformation is a rotation, and <strong>glMultMatrixf</strong> is
	 * called with a translation matrix, the translation is done
	 * directly on the coordinates to be transformed, while the
	 * rotation is done on the results of that translation.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glMultMatrixf</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * </ul>
	 *
	 * @param m Specifies an array to 16 consecutive values, which are
	 *          used as the elements of a 4x4 column-major matrix.
	 * @see #glLoadIdentity glLoadIdentity
	 * @see #glLoadMatrixf glLoadMatrixf
	 * @see #glMatrixMode glMatrixMode
	 * @see #glPushMatrix glPushMatrix
	 */
	public void glMultMatrixf(float m[]);

	/////////////////////// Geometry ///////////////////////

	/**
	 * <strong>glBegin</strong> and <strong>glEnd</strong> delimit the vertices that define a
	 * primitive or a group of like primitives. <strong>glBegin</strong> accepts a
	 * single argument that specifies in which of ten ways the
	 * vertices are interpreted. Taking n as an integer count
	 * starting at one, and N as the total number of vertices
	 * specified, the interpretations are as follows:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_POINTS</strong> Treats each vertex as a single point.
	 * Vertex defines point n. N points are drawn.
	 * <li><strong>GL_LINES</strong>  Treats each pair	of vertices as an
	 * independent line	segment.  Vertices
	 * 2n-1 and	2n define line n.  N/2 lines
	 * are drawn.
	 * <li><strong>GL_LINE_STRIP</strong> Draws a connected group of line
	 * segments	from the first vertex to the
	 * last.  Vertices n and n+1 define	line
	 * n.  N-1 lines are drawn.
	 * <li><strong>GL_LINE_LOOP</strong> Draws a connected group of line
	 * segments	from the first vertex to the
	 * last, then back to the first.  Vertices
	 * n and n+1 define	line n.	 The last
	 * line, however, is defined by vertices N
	 * and 1.  N lines are drawn.
	 * <li><strong>GL_TRIANGLES</strong> Treats each triplet of vertices as an
	 * independent triangle.  Vertices 3n-2,
	 * 3n-1, and 3n define triangle n.	N/3
	 * triangles are drawn.
	 * <li><strong>GL_TRIANGLE_STRIP</strong> Draws a connected group of triangles.
	 * One triangle is defined for each	vertex
	 * presented after the first two vertices.
	 * For odd n, vertices n, n+1, and n+2
	 * define triangle n.  For even n,
	 * vertices	n+1, n,	and n+2	define
	 * triangle	n.  N-2	triangles are drawn.
	 * <li><strong>GL_TRIANGLE_FAN</strong> Draws a connected group of triangles.
	 * One triangle is defined for each	vertex
	 * presented after the first two vertices.
	 * Vertices	1, n+1,	and n+2	define
	 * triangle	n. N-2	triangles are drawn.
	 * <li><strong>GL_QUADS</strong> Treats each group of four vertices as
	 * an independent quadrilateral.  Vertices
	 * 4n-3, 4n-2, 4n-1, and 4n	define
	 * quadrilateral n. N/4 quadrilaterals
	 * are drawn.
	 * <li><strong>GL_QUAD_STRIP</strong> Draws a connected group of
	 * quadrilaterals.	One quadrilateral is
	 * defined for each	pair of	vertices
	 * presented after the first pair.
	 * Vertices	2n-1, 2n, 2n+2,	and 2n+1
	 * define quadrilateral n.	N/2-1
	 * quadrilaterals are drawn.  Note that
	 * the order in which vertices are used to
	 * construct a quadrilateral from strip
	 * demos.data is different from that used	with
	 * independent demos.data.
	 * <li><strong>GL_POLYGON</strong> Draws a single, convex polygon. Vertices	1 through N define this  polygon.
	 * </ul>
	 * <p/>
	 * Only a subset of GL commands can be used between <strong>glBegin</strong> and
	 * <strong>glEnd</strong>.  The commands are <strong>glVertex</strong>, <strong>glColor</strong>, <strong>glIndex</strong>,
	 * <strong>glNormal</strong>, <strong>glTexCoord</strong>, <strong>glEvalCoord</strong>, <strong>glEvalPoint</strong>,
	 * <strong>glArrayElement</strong>, <strong>glMaterial</strong>, and <strong>glEdgeFlag</strong>.  Also, it	is
	 * acceptable to use <strong>glCallList</strong> or <strong>glCallLists</strong> to execute
	 * display lists that include only the preceding commands. If
	 * any other <strong>GL</strong> command is executed between <strong>glBegin</strong> and <strong>glEnd</strong>,
	 * the error flag is setand the	command	is ignored.
	 * <p/>
	 * Regardless of the value chosen for mode, there is no limit
	 * to the number of vertices that can be defined between
	 * <strong>glBegin</strong> and <strong>glEnd</strong>.
	 * Lines, triangles, quadrilaterals, and
	 * polygons that are incompletely specified are not drawn.
	 * Incomplete specification results when either too few
	 * vertices are provided to specify even a single primitive or
	 * when an incorrect multiple of vertices is specified. The
	 * incomplete primitive is ignored; the rest are drawn.
	 * <p/>
	 * The minimum specification of vertices for each primitive is
	 * as follows: 1 for a point, 2 for a line, 3 for a triangle,
	 * 4 for a quadrilateral, and 3 for a polygon.  Modes that
	 * require a certain multiple of vertices are <strong>GL_LINES</strong> (2),
	 * <strong>GL_TRIANGLES</strong> (3), <strong>GL_QUADS</strong> (4), and <strong>GL_QUAD_STRIP</strong> (2).
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if mode is set to an unaccepted value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glBegin</strong> is executed
	 * between a <strong>glBegin</strong> and the corresponding execution of <strong>glEnd</strong>.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glEnd</strong> is executed
	 * without being preceded by a <strong>glBegin</strong>.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if a command other than
	 * <strong>glVertex</strong>, <strong>glColor</strong>, <strong>glIndex</strong>, <strong>glNormal</strong>,
	 * <strong>glTexCoord</strong>,
	 * <strong>glEvalCoord</strong>, <strong>glEvalPoint</strong>, <strong>glArrayElement</strong>, <strong>glMaterial</strong>,
	 * <strong>glEdgeFlag</strong>, <strong>glCallList</strong>, or <strong>glCallLists</strong> is executed between
	 * the executionof <strong>glBegin</strong> and the corresponding execution
	 * <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * Execution of <strong>glEnableClientState</strong>, <strong>glDisableClientState</strong>,
	 * <strong>glEdgeFlagPointer</strong>, <strong>glTexCoordPointer</strong>, 	<strong>glColorPointer</strong>,
	 * <strong>glIndexPointer</strong>, <strong>glNormalPointer</strong>,
	 * <strong>glVertexPointer</strong>, <strong>glInterleavedArrays</strong>, or <strong>glPixelStore</strong>	is not
	 * allowed after a call to<strong>glBegin</strong> and before the corresponding
	 * call to <strong>glEnd</strong>, but an error may or may not be generated.
	 *
	 * @param mode Specifies the primitive	or primitives that will	be
	 *             created	from vertices presented	between	<strong>glBegin</strong>	and
	 *             the subsequent <strong>glEnd</strong>. Ten symbolic constants are
	 *             accepted: <strong>GL_POINTS</strong>, <strong>GL_LINES</strong>,	<strong>GL_LINE_STRIP</strong>,
	 *             <strong>GL_LINE_LOOP</strong>, <strong>GL_TRIANGLES</strong>, <strong>GL_TRIANGLE_STRIP</strong>,
	 *             <strong>GL_TRIANGLE_FAN</strong>, <strong>GL_QUADS</strong>, <strong>GL_QUAD_STRIP</strong>, and
	 *             <strong>GL_POLYGON</strong>.
	 * @see #glArrayElement glArrayElement
	 * @see #glCallList glCallList
	 * @see #glCallLists glCallLists
	 * @see #glColor4f glColor4f
	 * @see #glEdgeFlag glEdgeFlag
	 * @see #glEvalCoord glEvalCoord
	 * @see #glEvalPoint glEvalPoint
	 * @see #glIndexf glIndexf
	 * @see #glMaterial glMaterial
	 * @see #glNormal3f glNormal3f
	 * @see #glTexCoord glTexCoord
	 * @see #glVertex4f glVertex4f
	 */
	public void glBegin(int mode);

	/**
	 * See {@link #glBegin glBegin}
	 */
	public void glEnd();

	/**
	 * <strong>glFlush</strong> empties all command
	 * buffers, causing all issued commands to be executed as
	 * quickly as they are accepted by the actual rendering engine.
	 * <p/>
	 * Different <strong>GL</strong> implementations buffer commands in several
	 * different locations, including network buffers and the
	 * graphics accelerator itself.
	 * Though this execution may not be completed in any particular
	 * time period, it does complete in finite time.
	 * <p/>
	 * Because any <strong>GL</strong> program might be executed over a network, or
	 * on an accelerator that buffers commands, all programs should
	 * call <strong>glFlush</strong> whenever they count on having all of their
	 * previously issued commands completed. For example, call
	 * <strong>glFlush</strong> before waiting for user input that depends on the
	 * generated image.
	 * <p/>
	 * <strong>NOTES - </strong> <strong>glFlush</strong> can return at	any time. It does not wait until the
	 * execution of all previously issued GL	commands is complete.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glFlush</strong> is executed
	 * between the executionof <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>
	 * </ul>
	 *
	 * @see #glFinish glFinish
	 */
	public void glFlush();

	/**
	 * <strong>glFinish</strong> does not return until the effects of all previously
	 * called <strong>GL</strong> commands are complete. Such effects include all
	 * changes to <strong>GL</strong> state, all changes to connection state, and
	 * all changes to the frame buffer contents.
	 * <p/>
	 * <strong>NOTES - </strong> <strong>glFinish</strong> requires a round trip to the server.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glFinish</strong> is executed
	 * between the execution of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 *
	 * @see #glFlush glFlush
	 */
	public void glFinish();

	/**
	 * See {@link #glVertex4f glVertex4f}.
	 *
	 * @param x Specifies the x coordinate of a vertex.
	 * @param y Specifies the y coordinate of a vertex.
	 */
	public void glVertex2i(int x, int y);

	/**
	 * See {@link #glVertex4f glVertex4f}
	 *
	 * @param x Specifies the x coordinate of a vertex.
	 * @param y Specifies the y coordinate of a vertex.
	 */
	public void glVertex2f(float x, float y);

	/**
	 * See {@link #glVertex4f glVertex4f}.
	 *
	 * @param v Specifies an array of two
	 *          elements. The elements of the array correspond to x and y. z defaults to 0 and w to 1.
	 */
	public void glVertex2fv(float[] v);

	/**
	 * See {@link #glVertex4f glVertex4f}.
	 *
	 * @param x Specifies the x coordinate of a vertex.
	 * @param y Specifies the y coordinate of a vertex.
	 * @param z Specifies the z coordinate of a vertex.
	 */
	public void glVertex3i(int x, int y, int z);

	/**
	 * See {@link #glVertex4f glVertex4f}.
	 *
	 * @param v Specifies an array of three
	 *          elements. The elements of the array correspond to x, y and z. w defaults to 1.
	 */
	public void glVertex3fv(float[] v);

	/**
	 * See {@link #glVertex4f glVertex4f}.
	 *
	 * @param x Specifies the x coordinate of a vertex.
	 * @param y Specifies the y coordinate of a vertex.
	 * @param z Specifies the z coordinate of a vertex.
	 */
	public void glVertex3f(float x, float y, float z);

	/**
	 * See {@link #glVertex4f glVertex4f}.
	 *
	 * @param v Specifies an array of four
	 *          elements. The elements of the array correspond to x, y, z, and w.
	 */
	public void glVertex4fv(float[] v);

	/**
	 * <strong>glVertex</strong> commands are used within <strong>glBegin</strong>/<strong>glEnd</strong>
	 * pairs to specify point, line, and polygon vertices. The current
	 * color, normal, and texture coordinates are associated with
	 * the vertex when <strong>glVertex</strong> is called.
	 * <p/>
	 * When only <strong>x</strong> and <strong>y</strong> are specified, <strong>z</strong> defaults to 0 and <strong>w</strong>
	 * defaults to 1. When <strong>x</strong>, <strong>y</strong>, and <strong>z</strong> are specified, <strong>w</strong> defaults
	 * to 1.
	 * <p/>
	 * <strong>NOTES - </strong> Invoking <strong>glVertex</strong> outside of a <strong>glBegin</strong>/<strong>glEnd</strong> pair results in
	 * undefined behavior.
	 *
	 * @param x Specifies the x coordinate of a vertex.
	 * @param y Specifies the y coordinate of a vertex.
	 * @param z Specifies the z coordinate of a vertex.
	 * @param w Specifies the w coordinate of a vertex.
	 * @see #glBegin glBegin
	 * @see #glCallList glCallList
	 * @see #glColor4f glColor4f
	 * @see #glEdgeFlag glEdgeFlag
	 * @see #glEvalCoord glEvalCoord
	 * @see #glIndexf glIndexf
	 * @see #glMaterial glMaterial
	 * @see #glNormal3f glNormal3f
	 * @see #glRectf glRectf
	 * @see #glTexCoord glTexCoord
	 * @see #glVertexPointer glVertexPointer
	 */
	public void glVertex4f(float x, float y, float z, float w);

	/**
	 * See {@link #glColor4f glColor4f}.
	 *
	 * @param red   Specifies a red color value in the range 0 to 255.
	 * @param green Specifies a green color value in the range 0 to 255.
	 * @param blue  Specifies a blue color value in the range 0 to 255.
	 */
	public void glColor3ub(byte red, byte green, byte blue);

	/**
	 * See {@link #glColor4f glColor4f}.
	 *
	 * @param v Specifies an array of three color values corresponding to red, green and blue
	 *          in the range 0 to 255.
	 */
	public void glColor3ubv(byte[] v);

	/**
	 * See {@link #glColor4f glColor4f}.
	 *
	 * @param red   Specifies a red color value in the range 0 to 1.
	 * @param green Specifies a red color value in the range 0 to 1.
	 * @param blue  Specifies a red color value in the range 0 to 1.
	 */
	public void glColor3f(float red, float green, float blue);

	/**
	 * See {@link #glColor4f glColor4f}.
	 *
	 * @param v Specifies an array of three color values corresponding to red, green and blue
	 *          in the range 0 to 1.
	 */
	public void glColor3fv(float[] v);

	/**
	 * See {@link #glColor4f glColor4f}.
	 *
	 * @param v Specifies an array of four color values corresponding to red, green  blue and alpha
	 *          in the range 0 to 1.
	 */
	public void glColor4fv(float[] v);

	/**
	 * The <strong>GL</strong> stores both a current single-valued color index and a
	 * current four-valued RGBA color.  <strong>glColor</strong> sets a new four-
	 * valued RGBA color. <strong>glColor</strong> has two major variants:
	 * <strong>glColor3</strong> and <strong>glColor4</strong>. <strong>glColor3</strong> variants
	 * specify new	red,
	 * green, and blue values explicitly and set the current	alpha
	 * value to 1.0 (full intensity) implicitly. <strong>glColor4</strong> variants
	 * specify all four color components explicitly.
	 * <p/>
	 * <strong>glColor3b</strong>, <strong>glColor4b</strong>, <strong>glColor3s</strong>,
	 * <strong>glColor4s</strong>, <strong>glColor3i</strong>, and
	 * <strong>glColor4i</strong> take three or four signed byte, short, or long
	 * integers as arguments. When v is appended to the name, the
	 * color commands can take a pointer to an array of such
	 * values.
	 * <p/>
	 * Current color values are stored in floating-point format,
	 * with unspecified mantissa and exponent sizes. Unsigned
	 * integer color components, when specified, are linearly
	 * mapped to floating-point values such that the largest
	 * representable value maps to 1.0 (full intensity), and 0 maps
	 * to 0.0 (zero intensity). Signed integer color components,
	 * when specified, are linearly mapped to floating-point values
	 * such that the most positive representable value maps to 1.0,
	 * and the most negative representable value maps to -1.0.
	 * (Note that this mapping does not convert 0 precisely to
	 * 0.0.) Floating-point	values are mapped directly.
	 * <p/>
	 * Neither floating-point nor signed integer values are clamped
	 * to the range [0,1] before the current color is updated.
	 * However, color components are clamped to this range before
	 * they are interpolated or written into a color buffer.
	 * <p/>
	 * <strong>NOTES - </strong> The initial value for the current color is (1, 1, 1, 1).
	 * The current color can be updated at any time.	 In
	 * particular, <strong>glColor</strong> can be called between a call to <strong>glBegin</strong>
	 * and the corresponding	call to	<strong>glEnd</strong>.
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_CURRENT_COLOR</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_RGBA_MODE</strong>
	 * </ul>
	 *
	 * @param red   Specifies the red value for the current color.
	 * @param green Specifies the blue value for the current color.
	 * @param blue  Specifies the green value for the current color.
	 * @param alpha Specifies the alpha value for the current color.
	 * @see #glIndex glIndex
	 */
	public void glColor4f(float red, float green, float blue, float alpha);

	/**
	 * See {@link #glIndexf glIndexf}.
	 *
	 * @param c Specifies the new value for the current color index.
	 */
	public void glIndexi(int c);

	/**
	 * <strong>glIndex</strong> updates the current (single-valued) color index. It
	 * takes one argument, the new value for the current color
	 * index.
	 * <p/>
	 * The current index is stored as a floating-point value.
	 * Integer values are converted directly to floating-point
	 * values, with no special mapping. The initial	value is 1.
	 * <p/>
	 * Index values outside the representable range of the color
	 * index buffer are not clamped. However, before an index is
	 * dithered (if enabled) and written to the frame buffer, it is
	 * converted to fixed-point format.  Any	bits in	the integer
	 * portion of the resulting fixed-point value that do not
	 * correspond to bits in the frame buffer are masked out.
	 * <p/>
	 * <strong>NOTES - </strong> glIndexub and glIndexubv are available only if the <strong>GL</strong>
	 * version is 1.1 or greater.
	 * <p/>
	 * The current index can be updated at any time. In
	 * particular, <strong>glIndex</strong> can be called between a call to <strong>glBegin</strong>
	 * and the corresponding call to <strong>glEnd</strong>.
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_CURRENT_INDEX</strong>
	 * </ul>
	 *
	 * @param c Specifies the new value for the current color index.
	 * @see #glColor4f glColor4f
	 * @see #glIndexPointer glIndexPointer
	 */
	public void glIndexf(float c);

	/**
	 * See {@link #glNormal3f glNormal3f}.
	 *
	 * @param v Specifies an array of three values, the x, y, and z coordinates of the new current normal.
	 */
	public void glNormal3fv(float[] v);

	/**
	 * The current normal is set to the given coordinates whenever
	 * <strong>glNormal</strong> is issued. Byte, short, or integer arguments are
	 * converted to floating-point format with a linear mapping
	 * that maps the most positive representable integer value to
	 * 1.0, and the most negative representable integer value to -1.0.
	 * <p/>
	 * Normals specified with <strong>glNormal</strong> need not have unit length.
	 * If normalization is enabled, then normals specified with
	 * <strong>glNormal</strong> are normalized after transformation. To enable and
	 * disable normalization, call <strong>glEnable</strong> and <strong>glDisable</strong> with the
	 * argument <strong>GL_NORMALIZE</strong>. Normalization is initially disabled.
	 * <p/>
	 * <strong>NOTES - </strong> The current normal can be updated at any time. In
	 * particular, <strong>glNormal</strong> can be called between a call to <strong>glBegin</strong>
	 * and the corresponding call to <strong>glEnd</strong>.
	 * <p/>
	 * The initial value of the current normal is the unit vector (0, 0, 1).
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_CURRENT_NORMAL</strong>
	 * <li><strong>glIsEnabled</strong> with argument <strong>GL_NORMALIZE</strong>
	 * </ul>
	 *
	 * @param x Specifies the x coordinate of the new current normal.
	 * @param y Specifies the y coordinate of the new current normal.
	 * @param z Specifies the z coordinate of the new current normal.
	 * @see #glBegin glBegin
	 * @see #glColor4f glColor4f
	 * @see #glIndexf glIndexf
	 * @see #glNormalPointer glNormalPointer
	 * @see #glTexCoord glTexCoord
	 * @see #glVertex4f glVertex4f
	 */
	public void glNormal3f(float x, float y, float z);

	/**
	 * Line stippling masks out certain fragments produced by
	 * rasterization; those fragments will not be drawn.  The
	 * masking is achieved by using three parameters:  the 16-bit
	 * line stipple pattern <strong>pattern</strong>,	the repeat count <strong>factor</strong>, and
	 * an integer stipple counter s.
	 * <p/>
	 * Counter s is reset to	0 whenever <strong>glBegin</strong> is called, and
	 * before each line segment of a <strong>glBegin</strong>(<strong>GL_LINES</strong>)/<strong>glEnd</strong>
	 * sequence is generated.  It is incremented after each
	 * fragment of a unit width aliased line	segment	is generated,
	 * or after each i fragments of an i width line segment are
	 * generated.  The i fragments associated with count s are
	 * masked out if <code>pattern bit (s / factor) mod 16</code>
	 * is 0, otherwise these fragments are sent to the frame
	 * buffer.  Bit zero of pattern is the least significant	bit.
	 * <p/>
	 * Antialiased lines are	treated	as a sequence of 1xwidth
	 * rectangles for purposes of stippling.	 Whether rectagle s is
	 * rasterized or	not depends on the fragment rule described for
	 * aliased lines, counting rectangles rather than groups	of
	 * fragments.
	 * <p/>
	 * To enable and	disable	line stippling,	call <strong>glEnable</strong> and
	 * <strong>glDisable</strong> with argument <strong>GL_LINE_STIPPLE</strong>.  When enabled, the
	 * line stipple pattern is applied as described above.  When
	 * disabled, it is as if	the pattern were all 1's.  Initially,
	 * line stippling is disabled.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glLineStipple</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding	execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_LINE_STIPPLE_PATTERN</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_LINE_STIPPLE_REPEAT</strong>
	 * <li><strong>glIsEnabled</strong> with argument <strong>GL_LINE_STIPPLE</strong>
	 * </ul>
	 *
	 * @param factor  Specifies a multiplier for each bit in the line
	 *                stipple pattern.  If	factor is 3, for example, each
	 *                bit in the pattern is used three times before the
	 *                next	bit in the pattern is used.  factor is clamped
	 *                to the range	[1, 256] and defaults to 1.
	 * @param pattern Specifies a 16-bit integer whose bit	pattern
	 *                determines which fragments of a line	will be	drawn
	 *                when	the line is rasterized.	 Bit zero is used
	 *                first; the default pattern is all 1's.
	 * @see #glLineWidth glLineWidth
	 * @see #glPolygonStipple glPolygonStipple
	 */
	public void glLineStipple(int factor, short pattern);

	/**
	 * Polygon stippling, like line stippling (see {@link #glLineStipple glLineStipple}),
	 * masks out certain fragments produced by rasterization,
	 * creating a pattern. Stippling is independent of polygon
	 * antialiasing.
	 * <p/>
	 * <em>mask</em> is a pointer to a 32x32 stipple pattern that is stored
	 * in memory just like the pixel demos.data supplied to a
	 * <strong>glDrawPixels</strong> call with height	and width both equal to	32, a
	 * pixel	format of <strong>GL_COLOR_INDEX</strong>, and demos.data type	of <strong>GL_BITMAP</strong>.
	 * That is, the stipple pattern is represented as a 32x32 array
	 * of 1-bit color indices packed	in unsigned bytes.
	 * <strong>glPixelStore</strong> parameters like <strong>GL_UNPACK_SWAP_BYTES</strong> and
	 * <strong>GL_UNPACK_LSB_FIRST</strong> affect the assembling of the bits	into a
	 * stipple pattern.  Pixel transfer operations (shift, offset,
	 * pixel	map) are not applied to	the stipple image, however.
	 * <p/>
	 * To enable and	disable	polygon	stippling, call	<strong>glEnable</strong> and
	 * <strong>glDisable</strong> with argument <strong>GL_POLYGON_STIPPLE</strong>. Polygon
	 * stippling is initially disabled. If it's enabled, a
	 * rasterized polygon fragment with window coordinates x	 and
	 * y  is	sent to	the next stage of the <strong>GL</strong> if and only if the
	 * (x  mod 32)<sup>th</sup>	bit in the (y  mod 32)<sup>th</sup> row of	the stipple
	 * pattern is 1 (one).  When polygon stippling is disabled, it
	 * is as	if the stipple pattern consists	of all 1's.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glPolygonStipple</strong>	is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding	execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGetPolygonStipple</strong>
	 * <li><strong>glIsEnabled</strong> with argument <strong>GL_POLYGON_STIPPLE</strong>
	 * </ul>
	 *
	 * @param mask Specifies a array to a 32x32 stipple pattern that
	 *             will be	unpacked from memory in	the same way that
	 *             <strong>glDrawPixels</strong> unpacks pixels.
	 * @see	#glLineStipple glLineStipple
	 * @see #glDrawPixel glDrawPixel
	 * @see #glPixelStore glPixelStore
	 * @see #glPixelTransfer glPixelTransfer
	 */
	public void glPolygonStipple(byte[] mask);

	/**
	 * <strong>glRect</strong> supports efficient specification of rectangles	as two
	 * corner points.  Each rectangle command takes four arguments,
	 * organized either as two consecutive pairs of (x,y)
	 * coordinates, or as two pointers to arrays, each containing
	 * an (x,y) pair.  The resulting	rectangle is defined in	the
	 * z=0 plane.
	 * <p/>
	 * <strong>glRect</strong>(x1, y1, x2, y2) is exactly equivalent to the
	 * following sequence:  glBegin(<strong>GL_POLYGON</strong>); glVertex2(x1, y1);
	 * glVertex2(x2,	y1); glVertex2(x2, y2);	glVertex2(x1, y2);
	 * glEnd(); Note	that if	the second vertex is above and to the
	 * right	of the first vertex, the rectangle is constructed with
	 * a counterclockwise winding.
	 * <p/>
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glRect</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 *
	 * @param x0 the x coordinate of the first vertex.
	 * @param y0 the y coordinate of the first vertex.
	 * @param x1 the x coordinate of the second vertex.
	 * @param y1 the y coordinate of the second vertex.
	 * @see #glBegin glBegin
	 * @see #glVertex4f glVertex
	 */
	public void glRectf(float x0, float y0, float x1, float y1);

	/////////////////////// Lighting ///////////////////////

	/**
	 * <strong>GL</strong> primitives	can have either	flat or	smooth shading.
	 * Smooth shading, the default, causes the computed colors of
	 * vertices to be interpolated as the primitive is rasterized,
	 * typically assigning different colors to each resulting pixel
	 * fragment.  Flat shading selects the computed color of	just
	 * one vertex and assigns it to all the pixel fragments
	 * generated by rasterizing a single primitive.	In either
	 * case, the computed color of a	vertex is the result of
	 * lighting if lighting is enabled, or it is the	current	color
	 * at the time the vertex was specified if lighting is
	 * disabled.
	 * <p/>
	 * Flat and smooth shading are indistinguishable	for points.
	 * Starting when <strong>glBegin</strong> is issued and counting vertices	and
	 * primitives from 1, the <strong>GL</strong> gives each flat-shaded line
	 * segment i the	computed color of vertex i+1, its second
	 * vertex.  Counting similarly from 1, the <strong>GL</strong> gives each flat-shaded
	 * polygon the computed color of the vertex listed in
	 * the following	table.	This is	the last vertex	to specify the
	 * polygon in all cases except single polygons, where the first
	 * vertex specifies the flat-shaded color.
	 * <p/>
	 * <table align=center border=1>
	 * <tr><th>primitive type of polygon i <th>vertex
	 * <tr><td>Single polygon (i=1)<td align=center>1
	 * <tr><td>Triangle strip<td align=center>i+2
	 * <tr><td>Triangle fan<td align=center>i+2
	 * <tr><td>Independent triangle<td align=center>3i
	 * <tr><td>Quad strip<td align=center>2i+2
	 * <tr><td>Independent quad<td align=center>4i
	 * </table>
	 * <p/>
	 * Flat and smooth shading are specified by <strong>glShadeModel</strong>	with
	 * mode set to <strong>GL_FLAT</strong> and <strong>GL_SMOOTH</strong>, respectively.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if mode is any value other than
	 * <strong>GL_FLAT</strong> or GL_SMOOTH</ode>.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glShadeModel</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding	execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong> with argument <strong>GL_SHADE_MODEL</strong>
	 * </ul>
	 *
	 * @param mode Specifies a symbolic value representing a shading
	 *             technique. Accepted values are <strong>GL_FLAT</strong> and <strong>GL_SMOOTH</strong>.
	 *             The initial value is <strong>GL_SMOOTH</strong>.
	 * @see #glBegin glBegin
	 * @see #glColor4f glColor
	 * @see #glLightfv glLight
	 * @see #glLightModelfv glLightModel
	 */
	public void glShadeModel(int mode);

	/**
	 * See {@link #glLightfv glLightfv}.
	 *
	 * @param light Specifies a light.  The number of lights depends on
	 *              the implementation, but at least eight lights	are
	 *              supported.  They are identified by symbolic names of
	 *              the form <strong>GL_LIGHTi</strong> where 0 &lt; i &lt; <strong>GL_MAX_LIGHTS</strong>.
	 * @param pname Specifies a single-valued light source parameter for
	 *              light.  <strong>GL_SPOT_EXPONENT</strong>, <strong>GL_SPOT_CUTOFF</strong>,
	 *              <strong>GL_CONSTANT_ATTENUATION</strong>, <strong>GL_LINEAR_ATTENUATION</strong>, and
	 *              <strong>GL_QUADRATIC_ATTENUATION</strong> are accepted.
	 * @param param Specifies the value that parameter <em>pname</em> of light
	 *              source <em>light</em> will be set to.
	 */
	public void glLightf(int light, int pname, float param);

	/**
	 * <strong>glLight</strong> sets the values of individual	light source
	 * parameters.  light names the light and is a symbolic name of
	 * the form <strong>GL_LIGHTi</strong>, where 0 &lt;	i &lt; <strong>GL_MAX_LIGHTS</strong>.  <em>pname</em>
	 * specifies one	of ten light source parameters,	again by
	 * symbolic name.  <em>params</em> is either a single value or a pointer
	 * to an	array that contains the	new values.
	 * <p/>
	 * To enable and	disable	lighting calculation, call <strong>glEnable</strong>
	 * and <strong>glDisable</strong>	with argument <strong>GL_LIGHTING</strong>. Lighting is
	 * initially disabled.  When it is enabled, light sources that
	 * are enabled contribute to the	lighting calculation.  Light
	 * source i is enabled and disabled using <strong>glEnable</strong> and
	 * <strong>glDisable</strong> with argument <strong>GL_LIGHTi</strong>.
	 * <p/>
	 * The ten light parameters are as follows:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_AMBIENT</strong> <em>params</em> contains four integer or
	 * floating-point values that specify the
	 * ambient RGBA intensity of	the light.
	 * Integer values are mapped	linearly such
	 * that the most positive representable
	 * value maps to 1.0, and the most negative
	 * representable value maps to -1.0.
	 * Floating-point values are	mapped
	 * directly.	 Neither integer nor
	 * floating-point values are	clamped.  The
	 * initial ambient light intensity is (0,
	 * 0, 0, 1).
	 * <li><strong>GL_DIFFUSE</strong> <em>params</em> contains four integer or
	 * floating-point values that specify the
	 * diffuse RGBA intensity of	the light.
	 * Integer values are mapped	linearly such
	 * that the most positive representable
	 * value maps to 1.0, and the most negative
	 * representable value maps to -1.0.
	 * Floating-point values are	mapped
	 * directly.	 Neither integer nor
	 * floating-point values are	clamped.  The
	 * initial value for	<strong>GL_LIGHT0</strong> is (1, 1, 1,
	 * 1); for other lights, the	initial	value
	 * is (0, 0,	0, 0).
	 * <li><strong>GL_SPECULAR</strong>	      <em>params</em>  contains four integer or
	 * floating-point values that specify the
	 * specular RGBA intensity of the light.
	 * Integer values are mapped	linearly such
	 * that the most positive representable
	 * value maps to 1.0, and the most negative
	 * representable value maps to -1.0.
	 * Floating-point values are	mapped
	 * directly.	 Neither integer nor
	 * floating-point values are	clamped.  The
	 * initial value for	<strong>GL_LIGHT0</strong> is (1, 1, 1,
	 * 1); for other lights, the	initial	value
	 * is (0, 0,	0, 0).
	 * <p/>
	 * <li><strong>GL_POSITION</strong>	      <em>params</em>  contains four integer or
	 * floating-point values that specify the
	 * position of the light in homogeneous
	 * object coordinates.  Both	integer	and
	 * floating-point values are	mapped
	 * directly.	 Neither integer nor
	 * floating-point values are	clamped.
	 * <p/>
	 * The position is transformed by the
	 * modelview	matrix when <strong>glLight</strong> is called
	 * (just as if it were a point), and	it is
	 * stored in	eye coordinates.  If the w
	 * component	of the position	is 0, the
	 * light is treated as a directional
	 * source.  Diffuse and specular lighting
	 * calculations take	the light's direction,
	 * but not its actual position, into
	 * account, and attenuation is disabled.
	 * Otherwise, diffuse and specular lighting
	 * calculations are based on	the actual
	 * location of the light in eye
	 * coordinates, and attenuation is enabled.
	 * The initial position is (0, 0, 1,	0);
	 * thus, the	initial	light source is
	 * directional, parallel to,	and in the
	 * direction	of the -z axis.
	 * <p/>
	 * <li><strong>GL_SPOT_DIRECTION</strong>   <em>params</em>  contains three integer or
	 * floating-point values that specify the
	 * direction	of the light in	homogeneous
	 * object coordinates.  Both	integer	and
	 * floating-point values are	mapped
	 * directly.	 Neither integer nor
	 * floating-point values are	clamped.
	 * <p/>
	 * The spot direction is transformed	by the
	 * inverse of the modelview matrix when
	 * <strong>glLight</strong> is called	(just as if it were a
	 * normal), and it is stored	in eye
	 * coordinates.  It is significant only
	 * when <strong>GL_SPOT_CUTOFF</strong> is not 180, which it
	 * is initially.  The initial direction is
	 * (0, 0, -1).
	 * <p/>
	 * <li><strong>GL_SPOT_EXPONENT</strong>    <em>params</em>  is	a single integer or floating-
	 * point value that specifies the intensity
	 * distribution of the light.  Integer and
	 * floating-point values are	mapped
	 * directly.	 Only values in	the range
	 * [0,128] are accepted.
	 * <p/>
	 * Effective	light intensity	is attenuated
	 * by the cosine of the angle between the
	 * direction	of the light and the direction
	 * from the light to	the vertex being
	 * lighted, raised to the power of the spot
	 * exponent.	 Thus, higher spot exponents
	 * result in	a more focused light source,
	 * regardless of the	spot cutoff angle (see
	 * <strong>GL_SPOT_CUTOFF</strong>, next paragraph).	The
	 * initial spot exponent is 0, resulting in
	 * uniform light distribution.
	 * <p/>
	 * <li><strong>GL_SPOT_CUTOFF</strong>      <em>params</em>  is	a single integer or floating-
	 * point value that specifies the maximum
	 * spread angle of a	light source.  Integer
	 * and floating-point values	are mapped
	 * directly.	 Only values in	the range
	 * [0,90] and the special value 180 are
	 * accepted.	 If the	angle between the
	 * direction	of the light and the direction
	 * from the light to	the vertex being
	 * lighted is greater than the spot cutoff
	 * angle, the light is completely masked.
	 * Otherwise, its intensity is controlled
	 * by the spot exponent and the attenuation
	 * factors.	The initial spot cutoff	is
	 * 180, resulting in	uniform	light
	 * distribution.
	 * <p/>
	 * <li><strong>GL_CONSTANT_ATTENUATION</strong>
	 * <li><strong>GL_LINEAR_ATTENUATION</strong>
	 * <li><strong>GL_QUADRATIC_ATTENUATION</strong>
	 * <em>params</em>  is	a single integer or floating-
	 * point value that specifies one of	the
	 * three light attenuation factors.
	 * Integer and floating-point values	are
	 * mapped directly.	Only nonnegative
	 * values are accepted.  If the light is
	 * positional, rather than directional, its
	 * intensity	is attenuated by the
	 * reciprocal of the	sum of the constant
	 * factor, the linear factor	times the
	 * distance between the light and the
	 * vertex being lighted, and	the quadratic
	 * factor times the square of the same
	 * distance.	 The initial attenuation
	 * factors are (1, 0, 0), resulting in no
	 * attenuation.
	 * </ul>
	 * <p/>
	 * <strong>NOTES - </strong> It is	always the case	that <strong>GL_LIGHTi</strong> = <strong>GL_LIGHT0</strong> + i.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if either <em>light</em> or <em>pname</em>	is not
	 * an accepted value.
	 * <li><strong>GL_INVALID_VALUE</strong> is generated	if a spot exponent value is
	 * specified outside the	range [0,128], or if spot cutoff is
	 * specified outside the	range [0,90] (except for the special
	 * value	180), or if a negative attenuation factor is
	 * specified.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glLight</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGetLight</strong>
	 * <li><strong>glIsEnabled</strong> with argument <strong>GL_LIGHTING</strong>
	 * </ul>
	 *
	 * @param light  Specifies a light.  The number of lights depends on
	 *               the implementation, but at least eight lights	are
	 *               supported.  They are identified by symbolic names of
	 *               the form <strong>GL_LIGHTi</strong> where 0 &lt; i &lt; <strong>GL_MAX_LIGHTS</strong>.
	 * @param pname  Specifies a light source	parameter for light.
	 *               <strong>GL_AMBIENT</strong>, <strong>GL_DIFFUSE</strong>, <strong>GL_SPECULAR</strong>, <strong>GL_POSITION</strong>,
	 *               <strong>GL_SPOT_CUTOFF</strong>, <strong>GL_SPOT_DIRECTION</strong>, <strong>GL_SPOT_EXPONENT</strong>,
	 *               <strong>GL_CONSTANT_ATTENUATION</strong>, <strong>GL_LINEAR_ATTENUATION</strong>, and
	 *               <strong>GL_QUADRATIC_ATTENUATION</strong> are accepted.
	 * @param params Specifies the value(s) that parameter <em>pname</em> of light
	 *               source <em>light</em> will be set to.
	 * @see #glColorMaterial glColorMaterial
	 * @see #glLightModelf glLightModel
	 * @see #glMaterialf glMaterial
	 */
	public void glLightfv(int light, int pname, float params[]);

	/**
	 * See {@link #glLightModelfv glLightModelfv}.
	 *
	 * @param pname Specifies a lighting model parameter.
	 *              <strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong>,
	 *              and <strong>GL_LIGHT_MODEL_TWO_SIDE</strong> are accepted.
	 * @param param Specifies the value that <em>param</em>
	 *              will be set to.
	 */
	public void glLightModeli(int pname, boolean param);

	/**
	 * See {@link #glLightModelfv glLightModelfv}.
	 *
	 * @param pname Specifies a lighting model parameter.
	 *              <strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong>,
	 *              and <strong>GL_LIGHT_MODEL_TWO_SIDE</strong> are accepted.
	 * @param param Specifies the value that <em>param</em>
	 *              will be set to.
	 */
	public void glLightModeli(int pname, int param);

	/**
	 * See {@link #glLightModelfv glLightModelfv}.
	 *
	 * @param pname  Specifies a lighting model parameter.
	 *               <strong>GL_LIGHT_MODEL_AMBIENT</strong>, <strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong>,
	 *               and <strong>GL_LIGHT_MODEL_TWO_SIDE</strong> are accepted.
	 * @param params Specifies a pointer to the value or values that <em>params</em>
	 *               will be set to.
	 */
	public void glLightModeliv(int pname, int params[]);

	/**
	 * See {@link #glLightModelfv glLightModelfv}.
	 *
	 * @param pname Specifies a lighting model parameter.
	 *              <strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong>,
	 *              and <strong>GL_LIGHT_MODEL_TWO_SIDE</strong> are accepted.
	 * @param param Specifies the value that <em>param</em>
	 *              will be set to.
	 */
	public void glLightModelf(int pname, boolean param);

	/**
	 * See {@link #glLightModelfv glLightModelfv}.
	 *
	 * @param pname Specifies a lighting model parameter.
	 *              <strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong>,
	 *              and <strong>GL_LIGHT_MODEL_TWO_SIDE</strong> are accepted.
	 * @param param Specifies the value that <em>param</em>
	 *              will be set to.
	 */
	public void glLightModelf(int pname, float param);

	/**
	 * <strong>glLightModel</strong> sets the	lighting model parameter.  <em>pname</em> names
	 * a parameter and <em>params</em> gives the new value.  There are three
	 * lighting model parameters:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_LIGHT_MODEL_AMBIENT</strong>
	 * <em>params</em> contains four integer or floating-point
	 * values that	specify	the ambient RGBA intensity of
	 * the	entire scene.  Integer values are mapped
	 * linearly such that the most	positive representable
	 * value maps to 1.0, and the most negative
	 * representable value	maps to	-1.0.  Floating-point
	 * values are mapped directly.	 Neither integer nor
	 * floating-point values are clamped.	The initial
	 * ambient scene intensity is (0.2, 0.2, 0.2, 1.0).
	 * <li><strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong>
	 * <em>params</em> is a	single integer or floating-point value
	 * that specifies how specular	reflection angles are
	 * computed.  If <em>params</em> is 0 (or 0.0),	specular
	 * reflection angles take the view direction to be
	 * parallel to	and in the direction of	the -z axis,
	 * regardless of the location of the vertex in	eye
	 * coordinates.  Otherwise, specular reflections are
	 * computed from the origin of	the eye	coordinate
	 * system.  The initial value is 0.
	 * <li><strong>GL_LIGHT_MODEL_TWO_SIDE</strong>
	 * <em>params</em> is a	single integer or floating-point value
	 * that specifies whether one-	or two-sided lighting
	 * calculations are done for polygons.	 It has	no
	 * effect on the lighting calculations	for points,
	 * lines, or bitmaps.	If <em>params</em> is 0 (or 0.0), one-
	 * sided lighting is specified, and only the front
	 * material parameters	are used in the	lighting
	 * equation.  Otherwise, two-sided lighting is
	 * specified.	In this	case, vertices of back-facing
	 * polygons are lighted using the <em>back</em>	material
	 * parameters,	and have their normals reversed	before
	 * the	lighting equation is evaluated.	 Vertices of
	 * front-facing polygons are always lighted using the
	 * <em>front</em> material parameters, with no change to their
	 * normals. The initial value is 0.
	 * </ul>
	 * <p/>
	 * In RGBA mode,	the lighted color of a vertex is the sum of
	 * the material emission	intensity, the product of the material
	 * ambient reflectance and the lighting model full-scene
	 * ambient intensity, and the contribution of each enabled
	 * light	source.	 Each light source contributes the sum of
	 * three	terms:	ambient, diffuse, and specular.	 The ambient
	 * light	source contribution is the product of the material
	 * ambient reflectance and the light's ambient intensity.  The
	 * diffuse light	source contribution is the product of the
	 * material diffuse reflectance,	the light's diffuse intensity,
	 * and the dot product of the vertex's normal with the
	 * normalized vector from the vertex to the light source.  The
	 * specular light source	contribution is	the product of the
	 * material specular reflectance, the light's specular
	 * intensity, and the dot product of the	normalized vertex-to-
	 * eye and vertex-to-light vectors, raised to the power of the
	 * shininess of the material.  All three	light source
	 * contributions	are attenuated equally based on	the distance
	 * from the vertex to the light source and on light source
	 * direction, spread exponent, and spread cutoff	angle.	All
	 * dot products are replaced with 0 if they evaluate to a
	 * negative value.
	 * <p/>
	 * The alpha component of the resulting lighted color is	set to
	 * the alpha value of the material diffuse reflectance.
	 * <p/>
	 * In color index mode, the value of the	lighted	index of a
	 * vertex ranges	from the ambient to the	specular values	passed
	 * to <strong>glMaterial</strong> using <strong>GL_COLOR_INDEXES</strong>. Diffuse and specular
	 * coefficients,	computed with a	(.30, .59, .11)	weighting of
	 * the lights' colors, the shininess of the material, and the
	 * same reflection and attenuation equations as in the RGBA
	 * case,	determine how much above ambient the resulting index
	 * is.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if <em>pname</em> is not an accepted
	 * value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glLightModel</strong> is
	 * executed between the execution of <strong>glBegin</strong> and the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_LIGHT_MODEL_AMBIENT</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_LIGHT_MODEL_TWO_SIDE</strong>
	 * <li><strong>glIsEnabled</strong> with argument <strong>GL_LIGHTING</strong>
	 * </ul>
	 *
	 * @param pname  Specifies a lighting model parameter.
	 *               <strong>GL_LIGHT_MODEL_AMBIENT</strong>, <strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong>,
	 *               and <strong>GL_LIGHT_MODEL_TWO_SIDE</strong> are accepted.
	 * @param params Specifies a pointer to the value or values that <em>params</em>
	 *               will be set to.
	 * @see #glLightf glLight
	 * @see #glMaterialf glMaterial
	 */
	public void glLightModelfv(int pname, float params[]);

	/**
	 * See {@link #glMaterialfv glMaterialfv}.
	 *
	 * @param face  Specifies which face or faces are being updated.	 Must
	 *              be one of <strong>GL_FRONT</strong>, <strong>GL_BACK</strong>, or <strong>GL_FRONT_AND_BACK</strong>.
	 * @param pname Specifies the	single-valued material parameter of
	 *              the face or faces that is being updated.  Must be
	 *              <strong>GL_SHININESS</strong>.
	 * @param param Specifies the	value that parameter <strong>GL_SHININESS</strong> will
	 *              be set to.
	 */
	public void glMaterialf(int face, int pname, float param);

	/**
	 * <strong>glMaterial</strong> assigns values to material	parameters.  There are
	 * two matched sets of material parameters.  One, the <em>front-facing</em> set,
	 * is used to shade points, lines, bitmaps, and all
	 * polygons (when two-sided lighting is disabled), or just
	 * front-facing polygons	(when two-sided	lighting is enabled).
	 * The other set, <em>back-facing</em>, is used to shade back-facing
	 * polygons only	when two-sided lighting	is enabled.  Refer to
	 * the <strong>glLightModel</strong> reference page for details concerning one-
	 * and two-sided	lighting calculations.
	 * <p/>
	 * <strong>glMaterial</strong> takes three arguments.  The first,	<em>face</em>,
	 * specifies whether the	<strong>GL_FRONT</strong> materials, the	<strong>GL_BACK</strong>
	 * materials, or	both <strong>GL_FRONT_AND_BACK</strong> materials will be
	 * modified.  The second, <em>pname</em>,	specifies which	of several
	 * parameters in	one or both sets will be modified.  The	third,
	 * <em>params</em>, specifies what value or values will be assigned to
	 * the specified	parameter.
	 * <p/>
	 * Material parameters are used in the lighting equation	that
	 * is optionally	applied	to each	vertex.	 The equation is
	 * discussed in the <strong>glLightModel</strong>	reference page.	 The
	 * parameters that can be specified using <strong>glMaterial</strong>, and their
	 * interpretations by the lighting equation, are	as follows:
	 * <ul>
	 * <li><strong>GL_AMBIENT</strong>	      <em>params</em> contains four integer or
	 * floating-point values that specify the
	 * ambient RGBA reflectance of the
	 * material.	 Integer values	are mapped
	 * linearly such that the most positive
	 * representable value maps to 1.0, and the
	 * most negative representable value	maps
	 * to -1.0.	Floating-point values are
	 * mapped directly.	Neither	integer	nor
	 * floating-point values are	clamped.  The
	 * initial ambient reflectance for both
	 * front- and back-facing materials is
	 * (0.2, 0.2, 0.2, 1.0).
	 * <p/>
	 * <li><strong>GL_DIFFUSE</strong>	      <em>params</em> contains four integer or
	 * floating-point values that specify the
	 * diffuse RGBA reflectance of the
	 * material.	 Integer values	are mapped
	 * linearly such that the most positive
	 * representable value maps to 1.0, and the
	 * most negative representable value	maps
	 * to -1.0.	Floating-point values are
	 * mapped directly.	Neither	integer	nor
	 * floating-point values are	clamped.  The
	 * initial diffuse reflectance for both
	 * front- and back-facing materials is
	 * (0.8, 0.8, 0.8, 1.0).
	 * <p/>
	 * <li><strong>GL_SPECULAR</strong>	      <em>params</em> contains four integer or
	 * floating-point values that specify the
	 * specular RGBA reflectance	of the
	 * material.	 Integer values	are mapped
	 * linearly such that the most positive
	 * representable value maps to 1.0, and the
	 * most negative representable value	maps
	 * to -1.0.	Floating-point values are
	 * mapped directly.	Neither	integer	nor
	 * floating-point values are	clamped.  The
	 * initial specular reflectance for both
	 * front- and back-facing materials is (0,
	 * 0, 0, 1).
	 * <p/>
	 * <li><strong>GL_EMISSION</strong>	      <em>params</em> contains four integer or
	 * floating-point values that specify the
	 * RGBA emitted light intensity of the
	 * material.	 Integer values	are mapped
	 * linearly such that the most positive
	 * representable value maps to 1.0, and the
	 * most negative representable value	maps
	 * to -1.0.	Floating-point values are
	 * mapped directly.	Neither	integer	nor
	 * floating-point values are	clamped.  The
	 * initial emission intensity for both
	 * front- and back-facing materials is (0,
	 * 0, 0, 1).
	 * <p/>
	 * <li><strong>GL_SHININESS</strong>	      <em>params</em> is	a single integer or floating-
	 * point value that specifies the RGBA
	 * specular exponent	of the material.
	 * Integer and floating-point values	are
	 * mapped directly.	Only values in the
	 * range [0,128] are	accepted.  The initial
	 * specular exponent	for both front-	and
	 * back-facing materials is 0.
	 * <p/>
	 * <li><strong>GL_AMBIENT_AND_DIFFUSE</strong>
	 * Equivalent to calling <strong>glMaterial</strong> twice
	 * with the same parameter values, once
	 * with <strong>GL_AMBIENT</strong>and once with
	 * <strong>GL_DIFFUSE</strong>.
	 * <p/>
	 * <li><strong>GL_COLOR_INDEXES</strong>    <em>params</em> contains three integer or
	 * floating-point values specifying the
	 * color indices for	ambient, diffuse, and
	 * specular lighting.  These	three values,
	 * and <strong>GL_SHININESS</strong>,	are the	only material
	 * values used by the color index mode
	 * lighting equation.  Refer	to the
	 * <strong>glLightModel</strong> reference page for a
	 * discussion of color index	lighting.
	 * </ul>
	 * <p/>
	 * <strong>NOTES - </strong> The material parameters can be updated at any	time.  In
	 * particular, <strong>glMaterial</strong> can be	called between a call to
	 * <strong>glBegin</strong> and the corresponding	call to	<strong>glEnd</strong>	If only	a
	 * single material parameter is to be changed per vertex,
	 * however, <strong>glColorMaterial</strong> is preferred	over <strong>glMaterial</strong>	(see
	 * {@link #glColorMaterial glColorMaterial}).
	 * <p/>
	 * <p/>
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if either <em>face</em> or <em>pname</em> is not
	 * an accepted value.
	 * <li><strong>GL_INVALID_VALUE</strong> is generated if a specular exponent outside
	 * the range [0,128] is specified.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul><li><strong>glGetMaterial</strong></ul>
	 *
	 * @param face   Specifies which face or faces are being updated.	 Must
	 *               be one of <strong>GL_FRONT</strong>, <strong>GL_BACK</strong>, or <strong>GL_FRONT_AND_BACK</strong>.
	 * @param pname  Specifies the material parameter of the face or faces
	 *               that is being updated.  Must be one of <strong>GL_AMBIENT</strong>,
	 *               <strong>GL_DIFFUSE</strong>, <strong>GL_SPECULAR</strong>,	<strong>GL_EMISSION</strong>, <strong>GL_SHININESS</strong>,
	 *               <strong>GL_AMBIENT_AND_DIFFUSE</strong>,	or <strong>GL_COLOR_INDEXES</strong>.
	 * @param params Specifies a pointer to the value or values that <em>pname</em>
	 *               will be set to.
	 * @see #glColorMaterial glColorMaterial
	 * @see #glLightfv glLight
	 * @see #glLightModelfv glLightModel
	 */
	public void glMaterialfv(int face, int pname, float params[]);

	/////////////////////// Pixel ///////////////////////

	/**
	 * See {@link #glPixelTransferf glPixelTransferf}.
	 *
	 * @param pname Specifies the symbolic	name of	the pixel transfer
	 *              parameter to be set.  Must be one of the following:
	 *              <strong>GL_MAP_COLOR</strong>, <strong>GL_MAP_STENCIL</strong>, <strong>GL_INDEX_SHIFT</strong>,
	 *              <strong>GL_INDEX_OFFSET</strong>, <strong>GL_RED_SCALE</strong>, <strong>GL_RED_BIAS</strong>,
	 *              <strong>GL_GREEN_SCALE</strong>, <strong>GL_GREEN_BIAS</strong>, <strong>GL_BLUE_SCALE</strong>,
	 *              <strong>GL_BLUE_BIAS</strong>, <strong>GL_ALPHA_SCALE</strong>, <strong>GL_ALPHA_BIAS</strong>,
	 *              <strong>GL_DEPTH_SCALE</strong>, or <strong>GL_DEPTH_BIAS</strong>.
	 * @param param Specifies the value that pname	is set to.
	 */
	public void glPixelTransferi(int pname, int param);

	/**
	 * <strong>glPixelTransfer</strong> sets pixel transfer modes that affect	the
	 * operation of subsequent <strong>glCopyPixels</strong>,	<strong>glCopyTexImage1D</strong>,
	 * <strong>glCopyTexImage2D</strong>, <strong>glCopyTexSubImage1D</strong>, <strong>glCopyTexSubImage2D</strong>,
	 * <strong>glDrawPixels</strong>,	<strong>glReadPixels</strong>, <strong>glTexImage1D</strong>, <strong>glTexImage2D</strong>,
	 * <strong>glTexSubImage1D</strong>, and <strong>glTexSubImage2D</strong> commands.  The
	 * algorithms that are specified	by pixel transfer modes
	 * operate on pixels after they are read	from the frame buffer
	 * (<strong>glCopyPixels</strong>, <strong>glCopyTexImage1D</strong>, <strong>glCopyTexImage2D</strong>,
	 * <strong>glCopyTexSubImage1D</strong>, <strong>glCopyTexSubImage2D</strong>, and	<strong>glReadPixels</strong>),
	 * or unpacked from client memory (<strong>glDrawPixels</strong>,	<strong>glTexImage1D</strong>,
	 * <strong>glTexImage2D</strong>,	<strong>glTexSubImage1D</strong>, and <strong>glTexSubImage2D</strong>). Pixel
	 * transfer operations happen in	the same order,	and in the
	 * same manner, regardless of the command that resulted in the
	 * pixel	operation.  Pixel storage modes	(see {@link #glPixelStore glPixelStore})
	 * control the unpacking	of pixels being	read from client
	 * memory, and the packing of pixels being written back into
	 * client memory.
	 * <p/>
	 * Pixel	transfer operations handle four	fundamental pixel
	 * types:  <em>color</em>, <em>color index</em>, <em>depth</em>, and <em>stencil</em>.  Color
	 * pixels consist of four floating-point	values with
	 * unspecified mantissa and exponent sizes, scaled such that 0
	 * represents zero intensity and	1 represents full intensity.
	 * Color	indices	comprise a single fixed-point value, with
	 * unspecified precision	to the right of	the binary point.
	 * Depth	pixels comprise	a single floating-point	value, with
	 * unspecified mantissa and exponent sizes, scaled such that
	 * 0.0 represents the minimum depth buffer value, and 1.0
	 * represents the maximum depth buffer value.  Finally, stencil
	 * pixels comprise a single fixed-point value, with unspecified
	 * precision to the right of the	binary point.
	 * <p/>
	 * The pixel transfer operations	performed on the four basic
	 * pixel	types are as follows:
	 * <p/>
	 * <ul>
	 * <li><strong>Color</strong> - Each of the four color components is multiplied
	 * by a scale factor, then added to a bias factor.
	 * That is, the red component is multiplied by
	 * <strong>GL_RED_SCALE</strong>, then added to <strong>GL_RED_BIAS</strong>; the
	 * green component is multiplied by <strong>GL_GREEN_SCALE</strong>,
	 * then added to <strong>GL_GREEN_BIAS</strong>; the blue component
	 * is multiplied by <strong>GL_BLUE_SCALE</strong>, then added to
	 * <strong>GL_BLUE_BIAS</strong>; and	the alpha component is
	 * multiplied by <strong>GL_ALPHA_SCALE</strong>, then added to
	 * <strong>GL_ALPHA_BIAS</strong>.  After all	four color components
	 * are scaled and biased, each is clamped to	the
	 * range [0,1].  All	color, scale, and bias values
	 * are specified with <strong>glPixelTransfer</strong>.
	 * <p/>
	 * If <strong>GL_MAP_COLOR</strong> is true, each color component is
	 * scaled by	the size of the	corresponding color-to-color map,
	 * then replaced by the contents of
	 * that map indexed by the scaled component.	 That
	 * is, the red component is scaled by
	 * <strong>GL_PIXEL_MAP_R_TO_R_SIZE</strong>,	then replaced by the
	 * contents of <strong>GL_PIXEL_MAP_R_TO_R</strong> indexed by
	 * itself.  The green component is scaled by
	 * <strong>GL_PIXEL_MAP_G_TO_G_SIZE</strong>,	then replaced by the
	 * contents of <strong>GL_PIXEL_MAP_G_TO_G</strong> indexed by
	 * itself.  The blue	component is scaled by
	 * <strong>GL_PIXEL_MAP_B_TO_B_SIZE</strong>,	then replaced by the
	 * contents of <strong>GL_PIXEL_MAP_B_TO_B</strong> indexed by
	 * itself.  And the alpha component is scaled by
	 * <strong>GL_PIXEL_MAP_A_TO_A_SIZE</strong>,	then replaced by the
	 * contents of <strong>GL_PIXEL_MAP_A_TO_A</strong> indexed by
	 * itself.  All components taken from the maps are
	 * then clamped to the range	[0,1].	<strong>GL_MAP_COLOR</strong>
	 * is specified with	<strong>glPixelTransfer</strong>.  The contents
	 * of the various maps are specified	with
	 * <strong>glPixelMap</strong>.
	 * <p/>
	 * <li><strong>Color	index</strong> - Each color index is shifted left by
	 * <strong>GL_INDEX_SHIFT</strong> bits; any bits beyond the number
	 * of fraction bits carried by the fixed-point
	 * index are	filled with zeros.  If <strong>GL_INDEX_SHIFT</strong>
	 * is negative, the shift is	to the right, again
	 * zero filled.  Then <strong>GL_INDEX_OFFSET</strong> is added to
	 * the index.  <strong>GL_INDEX_SHIFT</strong> and <strong>GL_INDEX_OFFSET</strong>
	 * are specified with glPixelTransfer.
	 * <p/>
	 * From this	point, operation diverges depending on
	 * the required format of the resulting pixels.  If
	 * the resulting pixels are to be written to	a
	 * color index buffer, or if	they are being read
	 * back to client memory in <strong>GL_COLOR_INDEX</strong> format,
	 * the pixels continue to be	treated	as indices.
	 * If <strong>GL_MAP_COLOR</strong> is true, each index is masked by
	 * 2n - 1, where n is <strong>GL_PIXEL_MAP_I_TO_I_SIZE</strong>,
	 * then replaced by the contents of
	 * <strong>GL_PIXEL_MAP_I_TO_I</strong> indexed by the masked	value.
	 * <strong>GL_MAP_COLOR</strong> is specified	with <strong>glPixelTransfer</strong>.
	 * The contents of the index	map is specified with
	 * <strong>glPixelMap</strong>.
	 * <p/>
	 * If the resulting pixels are to be	written	to an
	 * RGBA color buffer, or if they are	read back to
	 * client memory in a format	other than
	 * <strong>GL_COLOR_INDEX</strong>, the pixels are converted from
	 * indices to colors	by referencing the four	maps
	 * <strong>GL_PIXEL_MAP_I_TO_R</strong>, <strong>GL_PIXEL_MAP_I_TO_G</strong>,
	 * <strong>GL_PIXEL_MAP_I_TO_B</strong>, and <strong>GL_PIXEL_MAP_I_TO_A</strong>.
	 * Before being dereferenced, the index is masked
	 * by 2n - 1, where n is <strong>GL_PIXEL_MAP_I_TO_R_SIZE</strong>
	 * for the red map, <strong>GL_PIXEL_MAP_I_TO_G_SIZE</strong>	for
	 * the green	map, <strong>GL_PIXEL_MAP_I_TO_B_SIZE</strong> for the
	 * blue map,	and <strong>GL_PIXEL_MAP_I_TO_A_SIZE</strong> for the
	 * alpha map.  All components taken from the	maps
	 * are then clamped to the range [0,1].  The
	 * contents of the four maps	is specified with
	 * glPixelMap.
	 * <p/>
	 * <li><strong>Depth</strong> - Each depth value is multiplied by
	 * <strong>GL_DEPTH_SCALE</strong>, added to <strong>GL_DEPTH_BIAS</strong>, then
	 * clamped to the range [0,1].
	 * <p/>
	 * <li><strong>Stencil</strong> - Each index is shifted <strong>GL_INDEX_SHIFT</strong> bits	just
	 * as a color index is, then	added to
	 * <strong>GL_INDEX_OFFSET</strong>.	If <strong>GL_MAP_STENCIL</strong> is true,
	 * each index is masked by 2n - 1, where n is
	 * <strong>GL_PIXEL_MAP_S_TO_S_SIZE</strong>,	then replaced by the
	 * contents of <strong>GL_PIXEL_MAP_S_TO_S</strong>indexed by the
	 * masked value.
	 * </ul>
	 * <p/>
	 * The following	table gives the	type, initial value, and range
	 * of valid values for each of the pixel	transfer parameters
	 * that are set with <strong>glPixelTransfer</strong>.
	 * <p/>
	 * <table align=center border=1>
	 * <tr><th>pname<th>type<th>initial value<th>valid range
	 * <tr><td><strong>GL_MAP_COLOR</strong><td align=center>boolean<td align=center>false<td align=center>true/false
	 * <tr><td><strong>GL_MAP_STENCIL</strong><td align=center>boolean<td align=center>false<td align=center>true/false
	 * <tr><td><strong>GL_INDEX_SHIFT</strong><td align=center>integer<td align=center>0<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_INDEX_OFFSET</strong><td align=center>integer<td align=center>0<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_RED_SCALE</strong><td align=center>float<td align=center>1<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_GREEN_SCALE</strong><td align=center>float<td align=center>1<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_BLUE_SCALE</strong><td align=center>float<td align=center>1<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_ALPHA_SCALE</strong><td align=center>float<td align=center>1<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_DEPTH_SCALE</strong><td align=center>float<td align=center>1<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_RED_BIAS</strong><td align=center>float<td align=center>0<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_GREEN_BIAS</strong><td align=center>float<td align=center>0<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_BLUE_BIAS</strong><td align=center>float<td align=center>0<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_ALPHA_BIAS</strong><td align=center>float<td align=center>0<td align=center>(-oo,oo)
	 * <tr><td><strong>GL_DEPTH_BIAS</strong><td align=center>float<td align=center>0<td align=center>(-oo,oo)
	 * </table>
	 * <p/>
	 * <p/>
	 * <strong>glPixelTransferf</strong> can be used to set any pixel	transfer
	 * parameter.  If the parameter type is boolean,	0 implies
	 * false	and any	other value implies true.  If <em>pname</em> is an
	 * integer parameter, <em>param</em> is rounded to the nearest integer.
	 * <p/>
	 * Likewise, <strong>glPixelTransferi</strong> can be used to set	any of the
	 * pixel	transfer parameters.  Boolean parameters are set to
	 * false	if <em>param</em> is 0 and to true otherwise.  <em>param</em> is
	 * converted to floating	point before being assigned to real-
	 * valued parameters.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if <em>pname</em>	is not an accepted
	 * value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glPixelTransfer</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding	execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_MAP_COLOR</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_MAP_STENCIL</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_INDEX_SHIFT</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_INDEX_OFFSET</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_RED_SCALE</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_RED_BIAS</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_GREEN_SCALE</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_GREEN_BIAS</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_BLUE_SCALE</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_BLUE_BIAS</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_ALPHA_SCALE</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_ALPHA_BIAS</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_DEPTH_SCALE</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_DEPTH_BIAS</strong>
	 * </ul>
	 *
	 * @param pname Specifies the symbolic	name of	the pixel transfer
	 *              parameter to be set.  Must be one of the following:
	 *              <strong>GL_MAP_COLOR</strong>, <strong>GL_MAP_STENCIL</strong>, <strong>GL_INDEX_SHIFT</strong>,
	 *              <strong>GL_INDEX_OFFSET</strong>, <strong>GL_RED_SCALE</strong>, <strong>GL_RED_BIAS</strong>,
	 *              <strong>GL_GREEN_SCALE</strong>, <strong>GL_GREEN_BIAS</strong>, <strong>GL_BLUE_SCALE</strong>,
	 *              <strong>GL_BLUE_BIAS</strong>, <strong>GL_ALPHA_SCALE</strong>, <strong>GL_ALPHA_BIAS</strong>,
	 *              <strong>GL_DEPTH_SCALE</strong>, or <strong>GL_DEPTH_BIAS</strong>.
	 * @param param Specifies the value that pname	is set to.
	 * @see #glCallList glCallList
	 * @see #glCopyPixels glCopyPixels
	 * @see #glCopyTexImage1D glCopyTexImage1D
	 * @see #glCopyTexImage2D glCopyTexImage2D
	 * @see #glCopyTexSubImage1D glCopyTexSubImage1D
	 * @see #glCopyTexSubImage2D glCopyTexSubImage2D
	 * @see #glDrawPixels glDrawPixels
	 * @see #glNewList glNewList
	 * @see #glPixelMap glPixelMap
	 * @see #glPixelStore glPixelStore
	 * @see #glPixelZoom glPixelZoom
	 * @see #glReadPixels glReadPixels
	 * @see #glTexImage1D glTexImage1D
	 * @see #glTexImage2D glTexImage2D
	 * @see #glTexSubImage1D glTexSubImage1D
	 * @see #glTexSubImage2D glTexSubImage2D
	 */
	public void glPixelTransferf(int pname, float param);

	/**
	 * See {@link #glPixelStoref glPixelStoref}.
	 *
	 * @param pname Specifies the symbolic	name of	the parameter to be
	 *              set.  Six values affect the packing of	pixel demos.data
	 *              into memory:  <strong>GL_PACK_SWAP_BYTES</strong>, <strong>GL_PACK_LSB_FIRST</strong>,
	 *              <strong>GL_PACK_ROW_LENGTH</strong>, <strong>GL_PACK_SKIP_PIXELS</strong>,
	 *              <strong>GL_PACK_SKIP_ROWS</strong>, and	<strong>GL_PACK_ALIGNMENT</strong>.  Six	more
	 *              affect	the unpacking of pixel demos.data from memory:
	 *              <strong>GL_UNPACK_SWAP_BYTES</strong>, <strong>GL_UNPACK_LSB_FIRST</strong>,
	 *              <strong>GL_UNPACK_ROW_LENGTH</strong>, <strong>GL_UNPACK_SKIP_PIXELS</strong>,
	 *              <strong>GL_UNPACK_SKIP_ROWS</strong>, and <strong>GL_UNPACK_ALIGNMENT</strong>.
	 * @param param Specifies the value that pname	is set to.
	 */
	public void glPixelStorei(int pname, boolean param);

	/**
	 * See {@link #glPixelStoref glPixelStoref}.
	 *
	 * @param pname Specifies the symbolic	name of	the parameter to be
	 *              set.  Six values affect the packing of	pixel demos.data
	 *              into memory:  <strong>GL_PACK_SWAP_BYTES</strong>, <strong>GL_PACK_LSB_FIRST</strong>,
	 *              <strong>GL_PACK_ROW_LENGTH</strong>, <strong>GL_PACK_SKIP_PIXELS</strong>,
	 *              <strong>GL_PACK_SKIP_ROWS</strong>, and	<strong>GL_PACK_ALIGNMENT</strong>.  Six	more
	 *              affect	the unpacking of pixel demos.data from memory:
	 *              <strong>GL_UNPACK_SWAP_BYTES</strong>, <strong>GL_UNPACK_LSB_FIRST</strong>,
	 *              <strong>GL_UNPACK_ROW_LENGTH</strong>, <strong>GL_UNPACK_SKIP_PIXELS</strong>,
	 *              <strong>GL_UNPACK_SKIP_ROWS</strong>, and <strong>GL_UNPACK_ALIGNMENT</strong>.
	 * @param param Specifies the value that pname	is set to.
	 */
	public void glPixelStorei(int pname, int param);

	/**
	 * <strong>glPixelStore</strong> sets pixel storage modes	that affect the
	 * operation of subsequent <strong>glDrawPixels</strong> and <strong>glReadPixels</strong>	as
	 * well as the unpacking	of polygon stipple patterns (see
	 * {@link #glPolygonStipple glPolygonStipple}), bitmaps (see {@link #glBitmap glBitmap}), and texture
	 * patterns (see	{@link #glTexImage1D glTexImage1D}, {@link #glTexImage2D glTexImage2D}, {@link #glTexSubImage1D glTexSubImage1D},
	 * and {@link #glTexSubImage2D glTexSubImage2D}).
	 * <p/>
	 * <em>pname</em>	is a symbolic constant indicating the parameter	to be
	 * set, and param is the	new value.  Six	of the twelve storage
	 * parameters affect how	pixel demos.data is returned to client
	 * memory, and are therefore significant	only for <strong>glReadPixels</strong>
	 * commands.  They are as follows:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_PACK_SWAP_BYTES</strong>
	 * If true, byte ordering for multibyte color
	 * components,	depth components, color	indices, or
	 * stencil indices is reversed.  That is, if a	four-
	 * byte component consists of bytes b , b , b , b ,
	 * it is stored in memory as b	, b , b	, b  if	  3
	 * <strong>GL_PACK_SWAP_BYTES</strong> is true.3 <strong>GL_PACK_SWAP_BYTES</strong>
	 * has	no effect on the memory	order of components
	 * within a pixel, only on the	order of bytes within
	 * components or indices.  For	example, the three
	 * components of a <strong>GL_RGB</strong> format pixel	are always
	 * stored with	red first, green second, and blue
	 * third, regardless of the value of
	 * <strong>GL_PACK_SWAP_BYTES</strong>.
	 * <br>
	 * <li><strong> GL_PACK_LSB_FIRST</strong>
	 * If true, bits are ordered within a byte from least
	 * significant	to most	significant; otherwise,	the
	 * first bit in each byte is the most significant
	 * one.  This parameter is significant	for bitmap
	 * demos.data only.
	 * <br>
	 * <li><strong>GL_PACK_ROW_LENGTH</strong>
	 * If greater than 0, <strong>GL_PACK_ROW_LENGTH</strong> defines the
	 * number of pixels in	a row.	If the first pixel of
	 * a row is placed at location	p in memory, then the
	 * location of	the first pixel	of the next row	is
	 * obtained by	skipping
	 * <pre>
	 *        (
	 *        |nl       s&gt;a
	 *    k = |_|___|   s&lt;a
	 *        |s| a |
	 *        )
	 *                 </pre>
	 * components or indices, where n is the number of
	 * components or indices in a pixel, l	is the number
	 * of pixels in a row (<strong>GL_PACK_ROW_LENGTH</strong> if it is
	 * greater than 0, the	width argument to the pixel
	 * routine otherwise),	a is the value of
	 * <strong>GL_PACK_ALIGNMENT</strong>, and s is	the size, in bytes, of
	 * a single component (if a&lt;s,	then it	is as if a=s).
	 * In the case	of 1-bit values, the location of the
	 * next row is	obtained by skipping
	 * <pre>
	 *        k = 8a|__|
	 *              |8a|
	 *                </pre>
	 * components or indices.
	 * <p><br>
	 * The	word <em>component</em> in this description refers to
	 * the	nonindex values	red, green, blue, alpha, and
	 * depth.  Storage format <strong>GL_RGB</strong>, for example,	has
	 * three components per pixel:	 first red, then
	 * green, and finally blue.
	 * <br>
	 * <li><strong>GL_PACK_SKIP_PIXELS</strong> and <strong>GL_PACK_SKIP_ROWS</strong>
	 * These values are provided as a convenience to the
	 * programmer;	they provide no	functionality that
	 * cannot be duplicated simply	by incrementing	the
	 * pointer passed to <strong>glReadPixels</strong>.  Setting
	 * <strong>GL_PACK_SKIP_PIXELS</strong>	to i is	equivalent to
	 * incrementing the pointer by	in components or
	 * indices, where n is	the number of components or
	 * indices in each pixel.  Setting <strong>GL_PACK_SKIP_ROWS</strong>
	 * to j is equivalent to incrementing the pointer by
	 * jk components or indices, where k is the number of
	 * components or indices per row, as just computed in
	 * the	<strong>GL_PACK_ROW_LENGTH</strong> section.
	 * <br>
	 * <li><strong>GL_PACK_ALIGNMENT</strong>
	 * Specifies the alignment requirements for the start
	 * of each pixel row in memory.  The allowable	values
	 * are	1 (byte-alignment), 2 (rows aligned to even-
	 * numbered bytes), 4 (word-alignment), and 8 (rows
	 * start on double-word boundaries).
	 * </ul>
	 * <p/>
	 * The other six	of the twelve storage parameters affect	how
	 * pixel	demos.data is	read from client memory.  These	values are
	 * significant for <strong>glDrawPixels</strong>,	<strong>glTexImage1D</strong>, <strong>glTexImage2D</strong>,
	 * <strong>glTexSubImage1D</strong>, <strong>glTexSubImage2D</strong>, <strong>glBitmap</strong>, and
	 * <strong>glPolygonStipple</strong>.  They are as follows:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_UNPACK_SWAP_BYTES</strong>
	 * If true,	byte ordering for multibyte color components,
	 * depth components, color indices,	or stencil indices is
	 * reversed.  That is, if a	four-byte component consists
	 * of bytes	b , b ,	b , b ,	it is taken from memory	as b ,
	 * b , b , b 0if <strong>GL_UNPACK_SWAP_BYTES</strong> is true.	    3
	 * <strong>GL_UNPACK_SWAP_BYTES</strong> has	no effect on the memory	order
	 * of components within a pixel, only on the order of
	 * bytes within components or indices.  For	example, the
	 * three components	of a <strong>GL_RGB</strong> format pixel are always
	 * stored with red first, green second, and	blue third,
	 * regardless of the value of <strong>GL_UNPACK_SWAP_BYTES</strong>.
	 * <br>
	 * <li><strong>GL_UNPACK_LSB_FIRST</strong>
	 * If true,	bits are ordered within	a byte from least
	 * significant to most significant;	otherwise, the first
	 * bit in each byte	is the most significant	one.  This is
	 * relevant	only for bitmap	demos.data.
	 * <br>
	 * <li><strong>GL_UNPACK_ROW_LENGTH</strong>
	 * If greater than 0, <strong>GL_UNPACK_ROW_LENGTH</strong> defines the
	 * number of pixels	in a row.  If the first	pixel of a row
	 * is placed at location p in memory, then the location of
	 * the first pixel of the next row is obtained by skipping
	 * <pre>
	 *        (
	 *        |nl       s&gt;a
	 *    k = |_|___|   s&lt;a
	 *        |s| a |
	 *        )
	 *                 </pre>
	 * components or indices, where n is the number of
	 * components or indices in	a pixel, l is the number of
	 * pixels in a row (<strong>GL_UNPACK_ROW_LENGTH</strong> if	it is greater
	 * than 0, the width argument to the pixel routine
	 * otherwise), a is	the value of <strong>GL_UNPACK_ALIGNMENT</strong>, and
	 * s is the	size, in bytes,	of a single component (if a&lt;s,
	 * then it is as if	a=s).  In the case of 1-bit values,
	 * the location of the next	row is obtained	by skipping
	 * <pre>
	 *        k = 8a|__|
	 *              |8a|
	 *                </pre>
	 * components or indices.
	 * <p><br>
	 * The word	component in this description refers to	the
	 * nonindex	values red, green, blue, alpha,	and depth.
	 * Storage format <strong>GL_RGB</strong>, for example, has three
	 * components per pixel:  first red, then green, and
	 * finally blue.
	 * <br>
	 * <li><strong>GL_UNPACK_SKIP_PIXELS</strong>	and <strong>GL_UNPACK_SKIP_ROWS</strong>
	 * These values are	provided as a convenience to the
	 * programmer; they	provide	no functionality that cannot
	 * be duplicated by	incrementing the pointer passed	to
	 * <strong>glDrawPixels</strong>, <strong>glTexImage1D</strong>, <strong>glTexImage2D</strong>,
	 * <strong>glTexSubImage1D</strong>,	<strong>glTexSubImage2D</strong>, <strong>glBitmap</strong>, or
	 * <strong> glPolygonStipple</strong>.  Setting <strong>GL_UNPACK_SKIP_PIXELS</strong>	to i
	 * is equivalent to	incrementing the pointer by in
	 * components or indices, where n is the number of
	 * components or indices in	each pixel.  Setting
	 * <strong>GL_UNPACK_SKIP_ROWS</strong> to j	is equivalent to incrementing
	 * the pointer by jk components or indices,	where k	is the
	 * number of components or indices per row,	as just
	 * computed	in the <strong>GL_UNPACK_ROW_LENGTH</strong> section.
	 * <br>
	 * <li><strong>GL_UNPACK_ALIGNMENT</strong>
	 * Specifies the alignment requirements for	the start of
	 * each pixel row in memory.  The allowable	values are 1
	 * (byte-alignment), 2 (rows aligned to even-numbered
	 * bytes), 4 (word-alignment), and 8 (rows start on
	 * double-word boundaries).
	 * </ul>
	 * <p/>
	 * The following	table gives the	type, initial value, and range
	 * of valid values for each storage parameter that can be set
	 * with <strong>glPixelStore</strong>.
	 * <p/>
	 * <table align=center border=1>
	 * <tr><th>pname<th>type<th>initial value<th>valid range
	 * <tr><td><strong>GL_PACK_SWAP_BYTES</strong><td align=center>boolean<td align=center>false<td align=center>true or false
	 * <tr><td><strong>GL_PACK_LSB_FIRST</strong><td align=center>boolean<td align=center>false<td align=center>true or false
	 * <tr><td><strong>GL_PACK_ROW_LENGTH</strong><td align=center>integer<td align=center>0<td align=center>[0,oo)
	 * <tr><td><strong>GL_PACK_SKIP_ROWS</strong><td align=center>integer<td align=center>0<td align=center>[0,oo)
	 * <tr><td><strong>GL_PACK_SKIP_PIXELS</strong><td align=center>integer<td align=center>0<td align=center>[0,oo)
	 * <tr><td><strong>GL_PACK_ALIGNMENT</strong><td align=center>integer<td align=center>4<td align=center>1, 2,	4, or 8
	 * <tr><td><strong>GL_UNPACK_SWAP_BYTES</strong><td align=center>boolean<td align=center>false<td align=center>true or false
	 * <tr><td><strong>GL_UNPACK_LSB_FIRST</strong><td align=center>boolean<td align=center>false<td align=center>true or false
	 * <tr><td><strong>GL_UNPACK_ROW_LENGTH</strong><td align=center>integer<td align=center>0<td align=center>[0,oo)
	 * <tr><td><strong>GL_UNPACK_SKIP_ROWS</strong><td align=center>integer<td align=center>0<td align=center>[0,oo)
	 * <tr><td><strong>GL_UNPACK_SKIP_PIXELS</strong><td align=center>integer<td align=center>0<td align=center>[0,oo)
	 * <tr><td><strong>GL_UNPACK_ALIGNMENT</strong><td align=center>integer<td align=center>4<td align=center>1, 2,	4, or 8
	 * </table>
	 * <p/>
	 * <strong>glPixelStoref</strong>	can be used to set any pixel store parameter.
	 * If the parameter type	is boolean, then if <em>param</em> is 0,	the
	 * parameter is false; otherwise	it is set to true.  If <em>pname</em>
	 * is a integer type parameter, <em>param</em> is	rounded	to the nearest
	 * integer.
	 * <p/>
	 * Likewise, <strong>glPixelStorei</strong> can also be used to set any of the
	 * pixel	store parameters.  Boolean parameters are set to false
	 * if <em>param</em> is 0	and true otherwise.
	 * <p/>
	 * <strong>NOTES - </strong>
	 * The pixel storage modes in effect when <strong>glDrawPixels</strong>,
	 * <strong>glReadPixels</strong>,	<strong>glTexImage1D</strong>, <strong>glTexImage2D</strong>, <strong>glTexSubImage1D</strong>,
	 * <strong>glTexSubImage2D</strong>, <strong>glBitmap</strong>, or	<strong>glPolygonStipple</strong> is placed in
	 * a display list control the interpretation of memory demos.data.
	 * The pixel storage modes in effect when a display list	is
	 * executed are not significant.
	 * <p/>
	 * Pixel storage	modes are client state and must	be pushed and
	 * restored using
	 * <strong>glPushClientAttrib</strong> and <strong>glPopClientAttrib</strong>.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if <em>pname</em>	is not an accepted
	 * value.
	 * <li><strong>GL_INVALID_VALUE</strong> is generated	if a negative row length,
	 * pixel	skip, or row skip value	is specified, or if alignment
	 * is specified as other	than 1,	2, 4, or 8.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glPixelStore</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding	execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_PACK_SWAP_BYTES</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_PACK_LSB_FIRST</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_PACK_ROW_LENGTH</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_PACK_SKIP_ROWS</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_PACK_SKIP_PIXELS</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_PACK_ALIGNMENT</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_UNPACK_SWAP_BYTES</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_UNPACK_LSB_FIRST</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_UNPACK_ROW_LENGTH</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_UNPACK_SKIP_ROWS</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_UNPACK_SKIP_PIXELS</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_UNPACK_ALIGNMENT</strong>
	 * </ul>
	 *
	 * @param pname Specifies the symbolic	name of	the parameter to be
	 *              set.  Six values affect the packing of	pixel demos.data
	 *              into memory:  <strong>GL_PACK_SWAP_BYTES</strong>, <strong>GL_PACK_LSB_FIRST</strong>,
	 *              <strong>GL_PACK_ROW_LENGTH</strong>, <strong>GL_PACK_SKIP_PIXELS</strong>,
	 *              <strong>GL_PACK_SKIP_ROWS</strong>, and	<strong>GL_PACK_ALIGNMENT</strong>.  Six	more
	 *              affect	the unpacking of pixel demos.data from memory:
	 *              <strong>GL_UNPACK_SWAP_BYTES</strong>, <strong>GL_UNPACK_LSB_FIRST</strong>,
	 *              <strong>GL_UNPACK_ROW_LENGTH</strong>, <strong>GL_UNPACK_SKIP_PIXELS</strong>,
	 *              <strong>GL_UNPACK_SKIP_ROWS</strong>, and <strong>GL_UNPACK_ALIGNMENT</strong>.
	 * @param param Specifies the value that pname	is set to.
	 * @see #glBitmap
	 * @see #glDrawPixels
	 * @see #glPixelMap
	 * @see #glPixelTransfer
	 * @see #glPixelZoom
	 * @see #glPolygonStipple
	 * @see #glPushClientAttrib
	 * @see #glReadPixels,
	 * @see #glTexImage1D
	 * @see #glTexImage2D
	 * @see #glTexSubImage1D
	 * @see #glTexSubImage2D
	 */
	public void glPixelStoref(int pname, float param);

	public void glReadPixels(int x, int y, int width, int height, int format, int type, Object pixels);

	/////////////////////// Textures ///////////////////////

	public void glTexCoord1f(float t);

	public void glTexCoord1fv(float[] tv);

	public void glTexCoord2f(float x, float y);

	public void glTexCoord2fv(float[] tv);

	public void glTexCoord3fv(float[] tv);

	public void glTexCoord4fv(float[] tv);

	public void glTexImage1D(int target, int level, int components, int length, int border, int format, int type,
			Object pixels);

	public void glTexImage2D(int target, int level, int components, int width, int height, int border, int format,
			int type, Object pixels);

	public void glTexParameteri(int target, int pname, int i);

	public void glTexParameterf(int target, int pname, float f);

	public void glTexParameterfv(int target, int pname, float[] fv);

	public void glTexEnvi(int target, int pname, int i);

	public void glTexEnvf(int target, int pname, float f);

	public void glTexEnvfv(int target, int pname, float[] fv);

	public void glTexGeni(int coord, int pname, int p);

	public void glTexGeniv(int coord, int pname, int[] pv);

	public void glTexGenf(int coord, int pname, float p);

	public void glTexGenfv(int coord, int pname, float[] pv);

	/////////////////////// Viewport / Frustrum ///////////////////////

	/**
	 * <strong>glViewport</strong> specifies the affine transformation of x and y
	 * from normalized device coordinates to window coordinates.
	 * Let (x , y) be normalized device coordinates.  Then the
	 * windowncoordinates (x, y) are computed as follows:
	 * <pre>
	 *        w   w
	 *        x  = (x + 1)(_____) + x
	 *        w  nd (2)
	 *        y  = (y + 1)(_____) + y
	 *        w	 nd (2)
	 * </pre>
	 * Viewport width and height are	silently clamped to a range
	 * that depends on the implementation.  To query	this range,
	 * call <strong>glGet</strong> with argument <strong>GL_MAX_VIEWPORT_DIMS</strong>.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_VALUE</strong> is generated	if either <em>width</em>	or <em>height</em> is
	 * negative.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glViewport</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_VIEWPORT</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_MAX_VIEWPORT_DIMS</strong>
	 * </ul>
	 *
	 * @param x      Specifies the x part the lower left corner of	the viewport
	 *               rectangle, in pixels. The initial value is (0,0).
	 * @param y      Specifies the y part the lower left corner of	the viewport
	 *               rectangle, in pixels. The initial value is (0,0).
	 * @param width  Specifies the width of the viewport.  When a
	 *               GL context is first attached to a window, width and
	 *               height are set to the dimensions	of that	window.
	 * @param height Specifies the height of the viewport.  When a
	 *               GL context is first attached to a window, width and
	 *               height are set to the dimensions of that	window.
	 * @see #glDepthRange glDepthRange
	 */
	public void glViewport(int x, int y, int width, int height);

	/**
	 * <strong>glOrtho</strong> describes a transformation that produces a parallel
	 * projection.  The current matrix (see {@link #glMatrixMode glMatrixMode}) is
	 * multiplied by	this matrix and	the result replaces the
	 * current matrix, as if	<strong>glMultMatrix</strong> were called with the
	 * following matrix as its argument:
	 * <pre>
	 *    (					   )
	 *    |__________	    0		0	t  |
	 *    |right-left				 x |
	 *    |		__________		   |
	 *    |	0	top-bottom	0	ty |
	 *    |					   |
	 *    |	0	    0	    __________	t  |
	 *    |			    zFar-zNear	 z |
	 *    |	0	    0		0	1  |
	 *    (					   )
	 * <p/>
	 * where
	 * <p/>
	 *    t  = -__________
	 *     x    right-left
	 *    t  = -__________
	 *     y    top-bottom
	 *    t  = -__________
	 *     z    zFar-zNear
	 * </pre>
	 * Typically, the matrix	mode is	<strong>GL_PROJECTION</strong>, and (<em>left</em>,
	 * <em>bottom</em>,  -<em>zNear</em>) and (<em>right</em>, <em>top</em>,  -<em>zNear</em>) specify the
	 * points on the	near clipping plane that are mapped to the
	 * lower	left and upper right corners of	the window,
	 * respectively,	assuming that the eye is located at (0,	0, 0).
	 * -<em>zFar</em>	specifies the location of the far clipping plane.
	 * Both <em>zNear</em> and <em>zFar</em> can be either positive or	negative.
	 * <p/>
	 * Use <strong>glPushMatrix</strong> and <strong>glPopMatrix</strong> to save and restore the
	 * current matrix stack.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glOrtho</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * </ul>
	 *
	 * @param left   Specifies the coordinate for the left
	 *               vertical clipping	plane.
	 * @param right  Specifies the coordinate for the right
	 *               vertical clipping	plane.
	 * @param bottom Specifies the coordinate for the bottom
	 *               horizontal clipping plane.
	 * @param top    Specifies the coordinate for the top
	 *               horizontal clipping plane.
	 * @param zNear  Specifies the distance to the nearer
	 *               depth clipping plane.  This value is
	 *               negative if the plane is to be behind the
	 *               viewer.
	 * @param zFar   Specifies the distance to the farther
	 *               depth clipping plane.  This value is
	 *               negative if the plane is to be behind the
	 *               viewer.
	 * @see #glFrustum glFrustum
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrix
	 * @see #glPushMatrix glPushMatrix
	 * @see #glViewport glViewport
	 */
	public void glOrtho(float left, float right, float bottom, float top, float zNear, float zFar);

	/**
	 * <strong>glFrustum</strong> describes a	perspective matrix that	produces a
	 * perspective projection.  The current matrix (see
	 * {@link #glMatrixMode glMatrixMode})	is multiplied by this matrix and the result
	 * replaces the current matrix, as if <strong>glMultMatrix</strong> were called
	 * with the following matrix as its argument:
	 * <pre>
	 * 	   (					   )
	 * 	   |  __________			   |
	 * 	   |  right-left       0       A       0   |
	 * 	   |					   |
	 * 	   |	  0	  __________   B       0   |
	 * 	   |		  top-bottom		   |
	 * 	   |	  0	       0       C       D   |
	 * 	   |					   |
	 * 	   |	  0	       0       -1      0   |
	 * 	   (					   )
	 * <p/>
	 *              __________
	 *          A = right-left
	 *              __________
	 *          B = top-bottom
	 *              ___________
	 *          C = -zFar-zNear
	 *              ___________
	 *          D = -zFar-zNear
	 * </pre>
	 * Typically, the matrix	mode is	<strong>GL_PROJECTION</strong>, and (<em>left</em>,
	 * <em>bottom</em>, -<em>zNear</em>) and (<em>right</em>, <em>top</em>,  -<em>zNear</em>) specify the	points
	 * on the near clipping plane that are mapped to	the lower left
	 * and upper right corners of the window, assuming that the eye
	 * is located at	(0, 0, 0).  -<em>zFar</em> specifies the	location of
	 * the far clipping plane.  Both	<em>zNear</em> and <em>zFar</em> must be
	 * positive.
	 * <p/>
	 * Use <strong>glPushMatrix</strong> and <strong>glPopMatrix</strong> to save and restore the
	 * current matrix stack.
	 * <p/>
	 * <strong>NOTES - </strong>
	 * Depth	buffer precision is affected by	the values specified
	 * for <em>zNear</em> and	<em>zFar</em>.  The greater the ratio of	<em>zFar</em> to	<em>zNear</em>
	 * is, the less effective the depth buffer will be at
	 * distinguishing between surfaces that are near	each other.
	 * If
	 * <pre>
	 * 			   r = _____
	 * 			       zNear
	 * </pre>
	 * roughly log (r) bits of depth	buffer precision are lost.
	 * Because r approaches infinity	as <em>zNear</em> approaches 0, <em>zNear</em>
	 * must never be	set to 0.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_VALUE</strong> is generated if <em>zNear</em> or <em>zFar</em> is not
	 * positive.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glFrustum</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_MATRIX_MODE</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_MODELVIEW_MATRIX</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_PROJECTION_MATRIX</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_TEXTURE_MATRIX</strong>
	 * </ul>
	 *
	 * @param left   Specifies the coordinate for the left
	 *               vertical clipping	plane.
	 * @param right  Specifies the coordinate for the right
	 *               vertical clipping	plane.
	 * @param bottom Specifies the coordinate for the bottom
	 *               horizontal clipping plane.
	 * @param top    Specifies the coordinate for the top
	 *               horizontal clipping plane.
	 * @param zNear  Specifies the distance to the near depth
	 *               clipping plane. The distance must be
	 *               positive.
	 * @param zFar   Specifies the distance to the far depth
	 *               clipping plane. The distance must be
	 *               positive.
	 * @see #glOrtho glOrtho
	 * @see #glMatrixMode glMatrixMode
	 * @see #glMultMatrixf glMultMatrixf
	 * @see #glPushMatrix glPushMatrix
	 * @see #glViewport glViewport
	 */
	public void glFrustum(float left, float right, float bottom, float top, float zNear, float zFar);

	/////////////////////// glEnable / glDisable ///////////////////////

	/**
	 * <strong>glEnable</strong> and <strong>glDisable</strong> enable	and disable various
	 * capabilities. Use <strong>glIsEnabled</strong>or <strong>glGet</strong> to determine the
	 * current setting of any capability. The initial value for
	 * each capability with the exception of	<strong>GL_DITHER</strong> is <strong>GL_FALSE</strong>.
	 * The initial value for	<strong>GL_DITHER</strong> is <strong>GL_TRUE</strong>.
	 * <p/>
	 * Both <strong>glEnable</strong>	and <strong>glDisable</strong> take a single argument, <em>capability</em>,
	 * which	can assume one of the following	values:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_ALPHA_TEST</strong>		   If enabled, do alpha	testing. See
	 * {@link #glAlphaFunc glAlphaFunc}.
	 * <p/>
	 * <li><strong>GL_AUTO_NORMAL</strong>	   If enabled, generate	normal vectors
	 * when	either <strong>GL_MAP2_VERTEX_3</strong>	or
	 * <strong>GL_MAP2_VERTEX_4</strong> is used to
	 * generate vertices. See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_BLEND</strong>		   If enabled, blend the incoming <strong>RGBA</strong>
	 * color values	with the values	in the
	 * color buffers. See {@link #glBlendFunc glBlendFunc}.
	 * <p/>
	 * <li><strong>GL_CLIP_PLANEi</strong>	   If enabled, clip geometry against
	 * user-defined	clipping plane i.  See
	 * {@link #glClipPlane glClipPlane}.
	 * <p/>
	 * <li><strong>GL_COLOR_LOGIC_OP</strong>	   If enabled, apply the currently
	 * selected logical operation to the
	 * incoming <strong>RGBA</strong> color and color
	 * buffer values. See {@link #glLogicOp glLogicOp}.
	 * <p/>
	 * <li><strong>GL_COLOR_MATERIAL</strong>	   If enabled, have one	or more
	 * material parameters track the
	 * current color.  See
	 * {@link #glColorMaterial glColorMaterial}.
	 * <p/>
	 * <li><strong>GL_CULL_FACE</strong>		   If enabled, cull polygons based on
	 * their winding in window
	 * coordinates.	See {@link #glCullFace glCullFace}.
	 * <p/>
	 * <li><strong>GL_DEPTH_TEST</strong>		   If enabled, do depth	comparisons
	 * and update the depth	buffer.	Note
	 * that	even if	the depth buffer
	 * exists and the depth	mask is	non-
	 * zero, the depth buffer is not
	 * updated if the depth	test is
	 * disabled. See {@link #glDepthFunc glDepthFunc} and
	 * {@link #glDepthRange glDepthRange}.
	 * <p/>
	 * <li><strong>GL_DITHER</strong>		   If enabled, dither color components
	 * or indices before they are written
	 * to the color	buffer.
	 * <p/>
	 * <li><strong>GL_FOG</strong>		   If enabled, blend a fog color into
	 * the posttexturing color.  See
	 * {@link #glFog glFog}.
	 * <p/>
	 * <li><strong>GL_INDEX_LOGIC_OP</strong>	   If enabled, apply the currently
	 * selected logical operation to the
	 * incoming index and color buffer
	 * indices. See
	 * {@link #glLogicOp glLogicOp}.
	 * <p/>
	 * <li><strong>GL_LIGHTi</strong>		   If enabled, include light i in the
	 * evaluation of the lighting
	 * equation. See <strong>glLightModel</strong> and
	 * {@link #glLight glLight}.
	 * <p/>
	 * <li><strong>GL_LIGHTING</strong>		   If enabled, use the current
	 * lighting parameters to compute the
	 * vertex color	or index.  Otherwise,
	 * simply associate the	current	color
	 * or index with each vertex. See
	 * <strong>glMaterial</strong>, <strong>glLightModel</strong>, and
	 * {@link #glLight glLight}.
	 * <p/>
	 * <li><strong>GL_LINE_SMOOTH</strong>	   If enabled, draw lines with correct
	 * filtering.  Otherwise, draw aliased
	 * lines.  See {@link #glLineWidth glLineWidth}.
	 * <p/>
	 * <li><strong>GL_LINE_STIPPLE</strong>	   If enabled, use the current line
	 * stipple pattern when	drawing	lines.
	 * See {@link #glLineStipple glLineStipple}.
	 * <p/>
	 * <li><strong>GL_MAP1_COLOR_4</strong>	   If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate <strong>RGBA</strong> values.  See {@link #glMap1 glMap1}.
	 * <p/>
	 * <li><strong>GL_MAP1_INDEX</strong>		   If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate color indices.  See
	 * {@link #glMap1 glMap1}.
	 * <p/>
	 * <li><strong>GL_MAP1_NORMAL</strong>	   If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate normals.  See <strong>glMap1</strong>.
	 * <p/>
	 * <li><strong>GL_MAP1_TEXTURE_COORD_1</strong>  If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate s texture coordinates.
	 * See {@link #glMap1 glMap1}.
	 * <p/>
	 * <li><strong>GL_MAP1_TEXTURE_COORD_2</strong>  If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate s and t texture
	 * coordinates.	 See {@link #glMap1 glMap1}.
	 * <p/>
	 * <li><strong>GL_MAP1_TEXTURE_COORD_3</strong> If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate s, t, and r	texture
	 * coordinates.	 See {@link #glMap1 glMap1}.
	 * <p/>
	 * <li><strong>GL_MAP1_TEXTURE_COORD_4</strong> If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate s, t, r, and q texture
	 * coordinates.	 See {@link #glMap1 glMap1}.
	 * <p/>
	 * <li><strong>GL_MAP1_VERTEX_3</strong>	   If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate x, y, and z	vertex
	 * coordinates.	 See {@link #glMap1 glMap1}.
	 * <p/>
	 * <li><strong>GL_MAP1_VERTEX_4</strong>	   If enabled, calls to	<strong>glEvalCoord1</strong>,
	 * <strong>glEvalMesh1</strong>,	and <strong>glEvalPoint1</strong>
	 * generate homogeneous	x, y, z, and w
	 * vertex coordinates.	See {@link #glMap1 glMap1}.
	 * <p/>
	 * <li><strong>GL_MAP2_COLOR_4</strong>	   If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate <strong>RGBA</strong> values.  See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_MAP2_INDEX</strong>		   If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate color indices.  See
	 * {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_MAP2_NORMAL</strong>	   If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate normals.  See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_MAP2_TEXTURE_COORD_1</strong>  If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate s texture coordinates.
	 * See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_MAP2_TEXTURE_COORD_2</strong>  If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate s and t texture
	 * coordinates.	 See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_MAP2_TEXTURE_COORD_3</strong>  If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate s, t, and r	texture
	 * coordinates.	 See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_MAP2_TEXTURE_COORD_4</strong>  If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate s, t, r, and q texture
	 * coordinates.	 See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_MAP2_VERTEX_3</strong>	   If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate x, y, and z	vertex
	 * coordinates.	 See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_MAP2_VERTEX_4</strong>	   If enabled, calls to	<strong>glEvalCoord2</strong>,
	 * <strong>glEvalMesh2</strong>,	and <strong>glEvalPoint2</strong>
	 * generate homogeneous	x, y, z, and w
	 * vertex coordinates.	See {@link #glMap2 glMap2}.
	 * <p/>
	 * <li><strong>GL_NORMALIZE</strong>		   If enabled, normal vectors
	 * specified with <strong>glNormal</strong> are scaled
	 * to unit length after
	 * transformation. See {@link #glNormal glNormal}.
	 * <p/>
	 * <li><strong>GL_POINT_SMOOTH</strong>	   If enabled, draw points with	proper
	 * filtering.  Otherwise, draw aliased
	 * points.  See	{@link #glPointSize glPointSize}.
	 * <p/>
	 * <li><strong>GL_POLYGON_OFFSET_FILL</strong>   If enabled, and if the polygon is
	 * rendered in <strong>GL_FILL</strong> mode, an	offset
	 * is added to depth values of a
	 * polygon's fragments before the
	 * depth comparison is performed. See
	 * {@link #glPolygonOffset glPolygonOffset}.
	 * <p/>
	 * <li><strong>GL_POLYGON_OFFSET_LINE</strong>   If enabled, and if the polygon is
	 * rendered in GL_LINE</strong> mode, an	offset
	 * is added to depth values of a
	 * polygon's fragments before the
	 * depth comparison is performed. See
	 * {@link #glPolygonOffset glPolygonOffset}.
	 * <p/>
	 * <li><strong>GL_POLYGON_OFFSET_POINT</strong>  If enabled, an offset is added to
	 * depth values	of a polygon's
	 * fragments before the	depth
	 * comparison is performed, if the
	 * polygon is rendered in <strong>GL_POINT</strong>
	 * mode. See {@link #glPolygonOffset glPolygonOffset}.
	 * <p/>
	 * <li><strong>GL_POLYGON_SMOOTH</strong>	   If enabled, draw polygons with
	 * proper filtering.  Otherwise, draw
	 * aliased polygons. For correct
	 * anti-aliased	polygons, an alpha
	 * buffer is needed and	the polygons
	 * must	be sorted front	to back.
	 * <p/>
	 * <li><strong>GL_POLYGON_STIPPLE</strong>	   If enabled, use the current polygon
	 * stipple pattern when	rendering
	 * polygons. See {@link #glPolygonStipple glPolygonStipple}.
	 * <p/>
	 * <li><strong>GL_SCISSOR_TEST</strong>	   If enabled, discard fragments that
	 * are outside the scissor rectangle.
	 * See {@link #glScissor glScissor}.
	 * <p/>
	 * <li><strong>GL_STENCIL_TEST</strong>	   If enabled, do stencil testing and
	 * update the stencil buffer. See
	 * {@link #glStencilFunc glStencilFunc} and {@link #glStencilOp glStencilOp}.
	 * <p/>
	 * <li><strong>GL_TEXTURE_1D</strong>		   If enabled, one-dimensional
	 * texturing is	performed (unless
	 * two-dimensional texturing is	also
	 * enabled). See {@link #glTexImage1D glTexImage1D}.
	 * <p/>
	 * <li><strong>GL_TEXTURE_2D</strong>		   If enabled, two-dimensional
	 * texturing is	performed. See
	 * {@link #glTexImage2D glTexImage2D}.
	 * <p/>
	 * <li><strong>GL_TEXTURE_GEN_Q</strong>	   If enabled, the q texture
	 * coordinate is computed using	the
	 * texture generation function defined
	 * with	<strong>glTexGen</strong>.  Otherwise, the
	 * current q texture coordinate	is
	 * used.  See {@link #glTexGen glTexGen}.
	 * <p/>
	 * <li><strong>GL_TEXTURE_GEN_R</strong>	   If enabled, the r texture
	 * coordinate is computed using	the
	 * texture generation function defined
	 * with	<strong>glTexGen</strong>.  Otherwise, the
	 * current r texture coordinate	is
	 * used.  See {@link #glTexGen glTexGen}.
	 * <p/>
	 * <li><strong>GL_TEXTURE_GEN_S</strong>	   If enabled, the s texture
	 * coordinate is computed using	the
	 * texture generation function defined
	 * with	<strong>glTexGen</strong>.  Otherwise, the
	 * current s texture coordinate	is
	 * used. See {@link #glTexGen glTexGen}.
	 * <p/>
	 * <li><strong>GL_TEXTURE_GEN_T</strong>	   If enabled, the t texture
	 * coordinate is computed using	the
	 * texture generation function defined
	 * with	<strong>glTexGen</strong>.  Otherwise, the
	 * current t texture coordinate	is
	 * used.  See {@link #glTexGen glTexGen}.
	 * </ul>
	 * <p/>
	 * <strong>NOTES - </strong>
	 * <strong>GL_POLYGON_OFFSET_FILL</strong>, <strong>GL_POLYGON_OFFSET_LINE</strong>,
	 * <strong>GL_POLYGON_OFFSET_POINT</strong>, <strong>GL_COLOR_LOGIC_OP</strong>, and
	 * <strong>GL_INDEX_LOGIC_OP</strong> are	only available if the GL version is
	 * 1.1 or greater.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if <em>capability</em> is not one of the	values
	 * listed previously.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glEnable</strong> or <strong>glDisable</strong>
	 * is executed between the execution of <strong>glBegin</strong> and the
	 * corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 *
	 * @param capability Specifies a symbolic constant indicating a GL capability.
	 * @see #glAlphaFunc glAlphaFunc
	 * @see #glBlendFunc glBlendFunc
	 * @see #glClipPlane glClipPlane
	 * @see #glColorMaterial glColorMaterial
	 * @see #glCullFace glCullFace
	 * @see #glDepthFunc glDepthFunc
	 * @see #glDepthRange glDepthRange
	 * @see #glEnableClientState glEnableClientState
	 * @see #glFog glFog
	 * @see #glGet glGet
	 * @see #glIsEnabled glIsEnabled
	 * @see #glLight glLight
	 * @see #glLightModel glLightModel
	 * @see #glLineWidth glLineWidth
	 * @see #glLineStipple glLineStipple
	 * @see #glLogicOp glLogicOp
	 * @see #glMap1 glMap1
	 * @see #glMap2 glMap2
	 * @see #glMaterial glMaterial
	 * @see #glNormal glNormal
	 * @see #glPointSize glPointSize
	 * @see #glPolygonMode glPolygonMode
	 * @see #glPolygonOffset glPolygonOffset
	 * @see #glPolygonStipple glPolygonStipple
	 * @see #glScissor glScissor
	 * @see #glStencilFunc glStencilFunc
	 * @see #glStencilOp glStencilOp
	 * @see #glTexGen glTexGen
	 * @see #glTexImage1D glTexImage1D
	 * @see #glTexImage2D glTexImage2D
	 */
	public void glEnable(int capability);

	/**
	 * See {@link #glEnable glEnable}.
	 *
	 * @param capability Specifies a symbolic constant indicating a GL capability.
	 */
	public void glDisable(int capability);

	/**
	 * <strong>glEnableClientState</strong> and <strong>glDisableClientState</strong> enable or
	 * disable individual client-side capabilities. By default, all
	 * client-side capabilities are disabled.  Both
	 * <strong>glEnableClientState</strong> and <strong>glDisableClientState</strong> take a single
	 * argument, <em>capability</em>, which can assume one of the following values:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_COLOR_ARRAY</strong>		   If enabled, the color array is
	 * enabled for writing and used	during
	 * rendering when <strong>glDrawArrays</strong> or
	 * <strong>glDrawElement</strong> is called. See
	 * {@link #glColorPointer glColorPointer}.
	 * <p/>
	 * <li><strong>GL_EDGE_FLAG_ARRAY</strong>		   If enabled, the edge	flag array is
	 * enabled for writing and used	during
	 * rendering when <strong>glDrawArrays</strong> or
	 * <strong>glDrawElements</strong> is called. See
	 * {@link #glEdgeFlagPointer glEdgeFlagPointer}.
	 * <p/>
	 * <li><strong>GL_INDEX_ARRAY</strong>		   If enabled, the index array is
	 * enabled for writing and used	during
	 * rendering when <strong>glDrawArrays</strong> or
	 * <strong>glDrawElements</strong> is called. See
	 * {@link #glIndexPointer glIndexPointer}.
	 * <p/>
	 * <li><strong>GL_NORMAL_ARRAY</strong>		   If enabled, the normal array	is
	 * enabled for writing and used	during
	 * rendering when <strong>glDrawArrays</strong> or
	 * <strong>glDrawElements</strong> is called. See
	 * {@link #glNormalPointer glNormalPointer}.
	 * <p/>
	 * <li><strong>GL_TEXTURE_COORD_ARRAY</strong>	   If enabled, the texture coordinate
	 * array is enabled for	writing	and
	 * used	for rendering when
	 * <strong>glDrawArrays</strong>	or <strong>glDrawElements</strong> is
	 * called. See {@link #glTexCoordPointer glTexCoordPointer}.
	 * <p/>
	 * <li><strong>GL_VERTEX_ARRAY</strong>	   If enabled, the vertex array	is
	 * enabled for writing and used	during
	 * rendering when <strong>glDrawArrays</strong> or
	 * <strong>glDrawElements</strong> is called. See
	 * {@link #glVertexPointer glVertexPointer}.
	 * </ul>
	 * <p/>
	 * <strong>NOTES - </strong>
	 * <strong>glEnableClientState</strong> is available only	if the GL version is
	 * 1.1 or greater.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if cap is not an	accepted
	 * value.
	 * <li><strong>glEnableClientState</strong> is not allowed between the execution of
	 * <strong>glBegin</strong> and the corresponding	<strong>glEnd</strong>, but an error may	or may
	 * not be generated. If no error	is generated, the behavior is
	 * undefined.
	 * </ul>
	 *
	 * @param capability Specifies a symbolic constant indicating a GL capability.
	 * @see #glArrayElement glArrayElement
	 * @see #glColorPointer glColorPointer
	 * @see #glDrawArrays glDrawArrays
	 * @see #glDrawElements glDrawElements
	 * @see #glEdgeFlagPointer glEdgeFlagPointer
	 * @see #glEnable glEnable
	 * @see #glGetPointerv glGetPointerv
	 * @see #glIndexPointer glIndexPointer
	 * @see #glInterleavedArrays glInterleavedArrays
	 * @see #glNormalPointer glNormalPointer
	 * @see #glTexCoordPointer glTexCoordPointer
	 * @see #glVertexPointer glVertexPointer
	 */
	public void glEnableClientState(int capability);

	/**
	 * See {@link #glEnableClientState glEnableClientState}.
	 *
	 * @param capability Specifies a symbolic constant indicating a GL capability.
	 */
	public void glDisableClientState(int capability);

	/**
	 * <strong>glIsEnabled</strong> returns <strong>GL_TRUE</strong> if <em>capability</em> is	an enabled capability
	 * and returns <strong>GL_FALSE</strong> otherwise.  Initially all capabilities
	 * except <strong>GL_DITHER</strong> are disabled; <strong>GL_DITHER</strong> is initially
	 * enabled.
	 * <p/>
	 * The following	capabilities are accepted for <em>capability</em>:
	 * <p/>
	 * <table align=center>
	 * <tr><th align=left>Constant<th align=left>See
	 * <tr><td><strong>GL_ALPHA_TEST</strong><td>{@link #glAlphaFunc glAlphaFunc}
	 * <tr><td><strong>GL_AUTO_NORMAL</strong><td>{@link #glEvalCoord glEvalCoord}
	 * <tr><td><strong>GL_BLEND</strong><td>{@link #glBlendFunc glBlendFunc}, {@link #glLogicOp glLogicOp}
	 * <tr><td><strong>GL_CLIP_PLANEi</strong><td>{@link #glClipPlane glClipPlane}
	 * <tr><td><strong>GL_COLOR_ARRAY</strong><td>{@link #glColorPointer glColorPointer}
	 * <tr><td><strong>GL_COLOR_LOGIC_OP</strong><td>{@link #glLogicOp glLogicOp}
	 * <tr><td><strong>GL_COLOR_MATERIAL</strong><td>{@link #glColorMaterial glColorMaterial}
	 * <tr><td><strong>GL_CULL_FACE</strong><td>{@link #glCullFace glCullFace}
	 * <tr><td><strong> GL_DEPTH_TEST</strong><td>{@link #glDepthFunc glDepthFunc}, {@link #glDepthRange glDepthRange}
	 * <tr><td><strong>GL_DITHER</strong><td>{@link #glEnable glEnable}
	 * <tr><td><strong>GL_EDGE_FLAG_ARRAY</strong><td>{@link #glEdgeFlagPointer glEdgeFlagPointer}
	 * <tr><td><strong>GL_FOG</strong><td>{@link #glFog glFog}
	 * <tr><td><strong>GL_INDEX_ARRAY</strong><td>{@link #glIndexPointer glIndexPointer}
	 * <tr><td><strong>GL_INDEX_LOGIC_OP</strong><td>{@link #glLogicOp glLogicOp}
	 * <tr><td><strong>GL_LIGHTi</strong><td>{@link #glLightModel glLightModel}, {@link #glLight glLight}
	 * <tr><td><strong>GL_LIGHTING</strong><td>{@link #glMaterial glMaterial}, {@link #glLightModel glLightModel}, {@link #glLight glLight}
	 * <tr><td><strong>GL_LINE_SMOOTH</strong><td>{@link #glLineWidth glLineWidth}
	 * <tr><td><strong>GL_LINE_STIPPLE</strong><td>{@link #glLineStipple glLineStipple}
	 * <tr><td><strong>GL_MAP1_COLOR_4</strong><td>{@link #glMap1 glMap1}, {@link #glMap2 glMap2}
	 * <tr><td><strong>GL_MAP2_TEXTURE_COORD_2</strong><td>{@link #glMap2 glMap2}
	 * <tr><td><strong>GL_MAP2_TEXTURE_COORD_3</strong><td>{@link #glMap2 glMap2}
	 * <tr><td><strong>GL_MAP2_TEXTURE_COORD_4</strong><td>{@link #glMap2 glMap2}
	 * <tr><td><strong>GL_MAP2_VERTEX_3</strong><td>{@link #glMap2 glMap2}
	 * <tr><td><strong>GL_MAP2_VERTEX_4</strong><td>{@link #glMap2 glMap2}
	 * <tr><td><strong>GL_NORMAL_ARRAY</strong><td>{@link #glNormalPointer glNormalPointer}
	 * <tr><td><strong>GL_NORMALIZE</strong><td>{@link #glNormal glNormal}
	 * <tr><td><strong>GL_POINT_SMOOTH</strong><td>{@link #glPointSize glPointSize}
	 * <tr><td><strong>GL_POLYGON_SMOOTH</strong><td>{@link #glPolygonMode glPolygonMode}
	 * <tr><td><strong>GL_POLYGON_OFFSET_FILL</strong><td>{@link #glPolygonOffset glPolygonOffset}
	 * <tr><td><strong>GL_POLYGON_OFFSET_LINE</strong><td>{@link #glPolygonOffset glPolygonOffset}
	 * <tr><td><strong>GL_POLYGON_OFFSET_POINT</strong><td>{@link #glPolygonOffset glPolygonOffset}
	 * <tr><td><strong>GL_POLYGON_STIPPLE</strong><td>{@link #glPolygonStipple glPolygonStipple}
	 * <tr><td><strong>GL_SCISSOR_TEST</strong><td>{@link #glScissor glScissor}
	 * <tr><td><strong>GL_STENCIL_TEST</strong><td>{@link #glStencilFunc glStencilFunc}, {@link #glStencilOp glStencilOp}
	 * <tr><td><strong>GL_TEXTURE_1D</strong><td>{@link #glTexImage1D glTexImage1D}
	 * <tr><td><strong>GL_TEXTURE_2D</strong><td>{@link #glTexImage2D glTexImage2D}
	 * <tr><td><strong>GL_TEXTURE_COORD_ARRAY</strong><td>{@link #glTexCoordPointer glTexCoordPointer}
	 * <tr><td><strong>GL_TEXTURE_GEN_Q</strong><td>{@link #glTexGen glTexGen}
	 * <tr><td><strong>GL_TEXTURE_GEN_R</strong><td>{@link #glTexGen glTexGen}
	 * <tr><td><strong>GL_TEXTURE_GEN_S</strong><td>{@link #glTexGen glTexGen}
	 * <tr><td><strong>GL_TEXTURE_GEN_T</strong><td>{@link #glTexGen glTexGen}
	 * <tr><td><strong>GL_VERTEX_ARRAY</strong><td>{@link #glVertexPointer glVertexPointer}
	 * </table>
	 * <br>
	 * <strong>NOTES - </strong> If an	error is generated, <strong>glIsEnabled</strong>	returns	<strong>GL_FALSE</strong>.
	 * <p/>
	 * <strong>GL_COLOR_LOGIC_OP</strong>, <strong>GL_COLOR_ARRAY</strong>, <strong>GL_EDGE_FLAG_ARRAY</strong>,
	 * <strong>GL_INDEX_ARRAY</strong>, <strong>GL_INDEX_LOGIC_OP</strong>, <strong>GL_NORMAL_ARRAY</strong>,
	 * <strong>GL_POLYGON_OFFSET_FILL</strong>, <strong>GL_POLYGON_OFFSET_LINE</strong>,
	 * <strong>GL_POLYGON_OFFSET_POINT</strong>, <strong>GL_TEXTURE_COORD_ARRAY</strong>, and
	 * <strong>GL_VERTEX_ARRAY</strong> are only available if the GL version is 1.1
	 * or greater
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if <em>capability</em> is not an	accepted
	 * value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glIsEnabled</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 *
	 * @param capability Specifies a symbolic constant indicating a GL capability.
	 * @return whether or not the supplied <em>capability</em> is enabled.
	 * @see #glEnable glEnable
	 * @see #glEnableClientState glEnableClientState
	 */
	public boolean glIsEnabled(int capability);

	/////////////////////// Attributes ///////////////////////

	/**
	 * <strong>glPushAttrib</strong> takes one argument, a mask that indicates which
	 * groups of state variables to save on the attribute stack.
	 * Symbolic constants are used to set bits in the mask.	<em>mask</em>
	 * is typically constructed by ORing several of these constants
	 * together.  The special mask <strong>GL_ALL_ATTRIB_BITS</strong> can be	used
	 * to save all stackable	states.
	 * <p/>
	 * The symbolic mask constants and their	associated GL state
	 * are as follows (the second column lists which	attributes are
	 * saved):
	 * <p/>
	 * <ul>
	 * <li><strong>GL_ACCUM_BUFFER_BIT</strong>	   Accumulation	buffer clear value
	 * <p/>
	 * <li><strong>GL_COLOR_BUFFER_BIT</strong>
	 * <ul>
	 * <li><strong>GL_ALPHA_TEST</strong> enable	bit
	 * <li>Alpha test function and reference value
	 * <li><strong>GL_BLEND</strong> enable bit
	 * <li>Blending source and destination functions
	 * <li>Constant blend color
	 * <li>Blending equation
	 * <li><strong>GL_DITHER</strong> enable bit
	 * <li><strong>GL_DRAW_BUFFER</strong> setting
	 * <li><strong>GL_COLOR_LOGIC_OP</strong> enable bit
	 * <li><strong>GL_INDEX_LOGIC_OP</strong> enable bit
	 * <li>Logic op function
	 * <li>Color mode and index	mode clear values
	 * <li>Color mode and index	mode writemasks
	 * </ul>
	 * <li><strong>GL_CURRENT_BIT</strong>
	 * <ul>
	 * <li>Current RGBA	color
	 * <li>Current color index
	 * <li>Current normal vector
	 * <li>Current texture coordinates
	 * <li>Current raster position
	 * </ul>
	 * <ul>
	 * <li><strong>GL_CURRENT_RASTER_POSITION_VALID</strong> flag
	 * <li>RGBA	color associated with current raster position
	 * <li>Color index associated with current raster position
	 * <li>Texture coordinates associated with current raster position
	 * <li><strong>GL_EDGE_FLAG</strong>	flag
	 * </ul>
	 * <li><strong>GL_DEPTH_BUFFER_BIT</strong>
	 * <ul>
	 * <li><strong>GL_DEPTH_TEST</strong> enable	bit
	 * <li>Depth buffer	test function
	 * <li>Depth buffer	clear value
	 * <li><strong>GL_DEPTH_WRITEMASK</strong> enable bit
	 * </ul>
	 * <li><strong>GL_ENABLE_BIT</strong>
	 * <ul>
	 * <li><strong>GL_ALPHA_TEST</strong> flag
	 * <li><strong>GL_AUTO_NORMAL</strong> flag
	 * <li><strong>GL_BLEND</strong> flag
	 * <li>Enable bits for the user-definable clipping planes
	 * <li><strong>GL_COLOR_MATERIAL</strong>
	 * <li><strong>GL_CULL_FACE</strong>	flag
	 * <li><strong>GL_DEPTH_TEST</strong> flag
	 * <li><strong>GL_DITHER</strong> flag
	 * <li><strong>GL_FOG</strong> flag
	 * <li><strong>GL_LIGHT</strong>i where 0 <= i &lt; GL_MAX_LIGHTS
	 * <li><strong>GL_LIGHTING</strong> flag
	 * <li><strong>GL_LINE_SMOOTH</strong> flag
	 * <li><strong>GL_LINE_STIPPLE</strong> flag
	 * <li><strong>GL_COLOR_LOGIC_OP</strong> flag
	 * <li><strong>GL_INDEX_LOGIC_OP</strong> flag
	 * <li><strong>GL_MAP1</strong>_x where x is	a map type
	 * <li><strong>GL_MAP2</strong>_x where x is	a map type
	 * <li><strong>GL_NORMALIZE</strong>	flag
	 * <li><strong>GL_POINT_SMOOTH</strong> flag
	 * <li><strong>GL_POLYGON_OFFSET_LINE</strong> flag
	 * <li><strong>GL_POLYGON_OFFSET_FILL</strong> flag
	 * <li><strong>GL_POLYGON_OFFSET_POINT</strong> flag
	 * <li><strong>GL_POLYGON_SMOOTH</strong> flag
	 * <li><strong>GL_POLYGON_STIPPLE</strong> flag
	 * <li><strong>GL_SCISSOR_TEST</strong> flag
	 * <li><strong>GL_STENCIL_TEST</strong> flag
	 * <li><strong>GL_TEXTURE_1D</strong> flag
	 * <li><strong>GL_TEXTURE_2D</strong> flag
	 * <li>Flags <strong>GL_TEXTURE_GEN</strong>_x where x is S,	T, R, or Q
	 * </ul>
	 * <li><strong>GL_EVAL_BIT</strong>
	 * <ul>
	 * <li><strong>GL_MAP1</strong>_x enable bits, where	x is a map type
	 * <li><strong>GL_MAP2</strong>_x enable bits, where	x is a map type
	 * <li><strong>1D grid endpoints and divisions
	 * <li><strong>2D grid endpoints and divisions
	 * <li><strong>GL_AUTO_NORMAL</strong> enable bit
	 * </ul>
	 * <li><strong>GL_FOG_BIT</strong>
	 * <ul>
	 * <li><strong>GL_FOG</strong> enable bit
	 * <li>Fog color
	 * <li>Fog density
	 * </ul>
	 * <ul>
	 * <li>Linear fog start
	 * <li>Linear fog end
	 * <li>Fog index
	 * <li><strong>GL_FOG_MODE</strong> value
	 * </ul>
	 * <li><strong>GL_HINT_BIT</strong>
	 * <ul>
	 * <li><strong>GL_PERSPECTIVE_CORRECTION_HINT</strong> setting
	 * <li><strong>GL_POINT_SMOOTH_HINT</strong>	setting
	 * <li><strong>GL_LINE_SMOOTH_HINT</strong> setting
	 * <li><strong>GL_POLYGON_SMOOTH_HINT</strong> setting
	 * <li><strong>GL_FOG_HINT</strong> setting
	 * </ul>
	 * <li><strong>GL_LIGHTING_BIT</strong>
	 * <ul>
	 * <li><strong>GL_COLOR_MATERIAL</strong> enable bit
	 * <li><strong>GL_COLOR_MATERIAL_FACE</strong> value
	 * <li>Color material parameters that are tracking the current color
	 * <li>Ambient scene color
	 * <li><strong>GL_LIGHT_MODEL_LOCAL_VIEWER</strong> value
	 * <li><strong>GL_LIGHT_MODEL_TWO_SIDE</strong> setting
	 * <li><strong>GL_LIGHTING</strong> enable bit
	 * <li>Enable bit for each light
	 * <li>Ambient, diffuse, and specular intensity for	each light
	 * <li>Direction, position,	exponent, and cutoff angle for each light
	 * <li>Constant, linear, and quadratic attenuation factors for each	light
	 * <li>Ambient, diffuse, specular, and emissive color for each material
	 * <li>Ambient, diffuse, and specular color	indices	for each material
	 * <li>Specular exponent for each material
	 * <li><strong>GL_SHADE_MODEL</strong> setting
	 * </ul>
	 * <li><strong>GL_LINE_BIT</strong>
	 * <ul>
	 * <li><strong>GL_LINE_SMOOTH</strong> flag
	 * <li><strong>GL_LINE_STIPPLE</strong> enable bit
	 * <li>Line	stipple	pattern	and repeat counter
	 * <li>Line	width
	 * </ul>
	 * <li><strong>GL_LIST_BIT</strong>
	 * <ul>
	 * <li><strong>GL_LIST_BASE</strong>	setting
	 * </ul>
	 * <li><strong>GL_PIXEL_MODE_BIT</strong>
	 * <ul>
	 * <li><strong>GL_RED_BIAS</strong> and <strong>GL_RED_SCALE</strong>	settings
	 * <li><strong>GL_GREEN_BIAS</strong> and <strong>GL_GREEN_SCALE</strong> values
	 * <li><strong>GL_BLUE_BIAS</strong>	and <strong>GL_BLUE_SCALE</strong>
	 * <li><strong>GL_ALPHA_BIAS</strong> and <strong>GL_ALPHA_SCALE</strong>
	 * <li><strong>GL_DEPTH_BIAS</strong> and <strong>GL_DEPTH_SCALE</strong>
	 * <li><strong>GL_INDEX_OFFSET</strong> and <strong>GL_INDEX_SHIFT</strong> values
	 * <li><strong>GL_MAP_COLOR</strong>	and <strong>GL_MAP_STENCIL</strong> flags
	 * <li><strong>GL_ZOOM_X</strong> and <strong>GL_ZOOM_Y</strong> factors
	 * <li><strong>GL_READ_BUFFER</strong> setting
	 * </ul>
	 * <li><strong>GL_POINT_BIT</strong>
	 * <ul>
	 * <li><strong>GL_POINT_SMOOTH</strong> flag
	 * <li>Point size
	 * </ul>
	 * <li><strong>GL_POLYGON_BIT</strong>
	 * <ul>
	 * <li><strong>GL_CULL_FACE</strong> enable bit
	 * <li><strong>GL_CULL_FACE_MODE</strong>value
	 * <li><strong>GL_FRONT_FACE</strong> indicator
	 * </ul>
	 * <ul>
	 * <li><strong>GL_POLYGON_MODE</strong> setting
	 * <li><strong>GL_POLYGON_SMOOTH</strong> flag
	 * <li><strong>GL_POLYGON_STIPPLE</strong> enable bit
	 * <li><strong>GL_POLYGON_OFFSET_FILL</strong> flag
	 * <li><strong>GL_POLYGON_OFFSET_LINE</strong> flag
	 * <li><strong>GL_POLYGON_OFFSET_POINT</strong> flag
	 * <li><strong>GL_POLYGON_OFFSET_FACTOR</strong>
	 * <li><strong>GL_POLYGON_OFFSET_UNITS</strong>
	 * </ul>
	 * <li><strong>GL_POLYGON_STIPPLE_BIT</strong>
	 * <ul>
	 * <li>Polygon stipple image
	 * </ul>
	 * <li><strong>GL_SCISSOR_BIT</strong>
	 * <ul>
	 * <li><strong>GL_SCISSOR_TEST</strong> flag
	 * <li>Scissor box
	 * </ul>
	 * <li><strong>GL_STENCIL_BUFFER_BIT</strong>
	 * <ul>
	 * <li><strong>GL_STENCIL_TEST</strong> enable bit
	 * <li>Stencil function and	reference value
	 * <li>Stencil value mask
	 * <li>Stencil fail, pass, and depth buffer	pass actions
	 * <li>Stencil buffer clear	value
	 * <li>Stencil buffer writemask
	 * </ul>
	 * <li><strong>GL_TEXTURE_BIT</strong>
	 * <ul>
	 * <li>Enable bits for the four texture coordinates
	 * <li>Border color	for each texture image
	 * <li>Minification	function for each texture image
	 * <li>Magnification function for each texture image
	 * <li>Texture coordinates and wrap	mode for each texture image
	 * <li>Color and mode for each texture environment
	 * <li>Enable bits <strong>GL_TEXTURE_GEN_</strong>x, x is S, T, R, and Q
	 * <li><strong>GL_TEXTURE_GEN_MODE</strong> setting for S, T, R, and	Q
	 * <li><strong>glTexGen</strong> plane equations for	S, T, R, and Q
	 * <li>Current texture bindings (for example, <strong>GL_TEXTURE_2D_BINDING</strong>)
	 * </ul>
	 * <li><strong>GL_TRANSFORM_BIT</strong>
	 * <ul>
	 * <li>Coefficients	of the six clipping planes
	 * <li>Enable bits for the user-definable clipping planes
	 * <li><strong>GL_MATRIX_MODE</strong> value
	 * <li><strong>GL_NORMALIZE</strong>	flag
	 * </ul>
	 * <li><strong>GL_VIEWPORT_BIT</strong>
	 * <ul>
	 * <li>Depth range (near and far)
	 * <li>Viewport origin and extent
	 * </ul>
	 * </ul>
	 * <p/>
	 * <strong>glPopAttrib</strong> restores the values of the state variables saved
	 * with the last
	 * <strong>glPushAttrib</strong> command.	 Those not saved are left unchanged.
	 * <p/>
	 * It is	an error to push attributes onto a full	stack, or to
	 * pop attributes off an	empty stack.  In either	case, the
	 * error	flag is	set and	no other change	is made	to GL state.
	 * <p/>
	 * Initially, the attribute stack is empty.
	 * <p/>
	 * <strong>NOTES - </strong>
	 * Not all values for GL	state can be saved on the attribute
	 * stack.  For example, render mode state, and select and
	 * feedback state cannot	be saved.  Client state	must be	saved
	 * with <strong>glPushClientAttrib</strong>.
	 * <p/>
	 * The depth of the attribute stack depends on the
	 * implementation, but it must be at least 16.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_STACK_OVERFLOW</strong> is generated if <strong>glPushAttrib</strong> is called
	 * while	the attribute stack is full.
	 * <li><strong>GL_STACK_UNDERFLOW</strong> is	generated if <strong>glPopAttrib</strong> is called
	 * while	the attribute stack is empty.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glPushAttrib</strong> or
	 * <strong>glPopAttrib</strong> is executed between the execution	of <strong>glBegin</strong> and
	 * the corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_ATTRIB_STACK_DEPTH</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_MAX_ATTRIB_STACK_DEPTH</strong>
	 * </ul>
	 *
	 * @param mask Specifies a mask that indicates	which attributes to
	 *             save. Values for mask are listed above.
	 * @see #glGet glGet
	 * @see #glGetClipPlane glGetClipPlane
	 * @see #glGetError glGetError
	 * @see #glGetLight glGetLight
	 * @see #glGetMap glGetMap
	 * @see #glGetMaterial glGetMaterial
	 * @see #glGetPixelMap
	 * @see #glGetPolygonStipple glGetPolygonStipple
	 * @see #glGetString glGetString
	 * @see #glGetTexEnv glGetTexEnv
	 * @see #glGetTexGen glGetTexGen
	 * @see #glGetTexImage glGetTexImage
	 * @see #glGetTexLevelParameter glGetTexLevelParameter
	 * @see #glGetTexParameter glGetTexParameter
	 * @see #glIsEnabled glIsEnabled
	 * @see #glPushClientAttrib glPushClientAttrib
	 */
	public void glPushAttrib(int mask);

	/**
	 * See {@link #glPushAttrib glPushAttrib}.
	 */
	public void glPopAttrib();

	public void glPushClientAttrib(int mask);

	/**
	 * See {@link #glPushClientAttrib glPushClientAttrib}.
	 */
	public void glPopClientAttrib();

	/**
	 * <strong>glPointSize</strong> specifies	the rasterized diameter	of both
	 * aliased and antialiased points.  Using a point size other
	 * than 1 has different effects,	depending on whether point
	 * antialiasing is enabled.  To enable and disable point
	 * antialiasing,	call <strong>glEnable</strong> and <strong>glDisable</strong> with argument
	 * <strong>GL_POINT_SMOOTH</strong>. Point antialiasing is initially disabled.
	 * <p/>
	 * If point antialiasing	is disabled, the actual	size is
	 * determined by	rounding the supplied size to the nearest
	 * integer.  (If	the rounding results in	the value 0, it	is as
	 * if the point size were 1.)  If the rounded size is odd, then
	 * the center point (x, y) of the pixel fragment	that
	 * represents the point is computed as
	 * <pre>
	 *         ( (| x | + .5) / w, (| y | + .5) / w )
	 * </pre>
	 * where	w subscripts indicate window coordinates.  All pixels
	 * that lie within the square grid of the rounded size centered
	 * at (x, y) make up the	fragment.  If the size is even,	the
	 * center point is
	 * <pre>
	 *         ( | x + .5 | / w, | y + .5 | / w )
	 * </pre>
	 * and the rasterized fragment's centers are the half-integer
	 * window coordinates within the square of the rounded size
	 * centered at (x, y).  All pixel fragments produced in
	 * rasterizing a	nonantialiased point are assigned the same
	 * associated demos.data, that	of the vertex corresponding to the
	 * point.
	 * <p/>
	 * If antialiasing is enabled, then point rasterization
	 * produces a fragment for each pixel square that intersects
	 * the region lying within the circle having diameter equal to
	 * the current point size and centered at the point's (x / w, y / w).
	 * The coverage value for each fragment is the window
	 * coordinate area of the intersection of the circular region
	 * with the corresponding pixel square.	This value is saved
	 * and used in the final	rasterization step. The	demos.data
	 * associated with each fragment	is the demos.data associated with
	 * the point being rasterized.
	 * <p/>
	 * Not all sizes	are supported when point antialiasing is
	 * enabled. If an unsupported size is requested,	the nearest
	 * supported size is used.  Only	size 1 is guaranteed to	be
	 * supported; others depend on the implementation.  To query
	 * the range of supported sizes and the size difference between
	 * supported sizes within the range, call glGet with arguments
	 * <strong>L_POINT_SIZE_RANGE</strong> and <strong>GL_POINT_SIZE_GRANULARITY</strong>.
	 * <p/>
	 * <strong>NOTES - </strong>
	 * The point size specified by <strong>glPointSize</strong> is always returned
	 * when <strong>GL_POINT_SIZE</strong> is	queried.  Clamping and rounding	for
	 * aliased and antialiased points have no effect on the
	 * specified value.
	 * <p/>
	 * A non-antialiased point size may be clamped to an
	 * implementation-dependent maximum.  Although this maximum
	 * cannot be queried, it	must be	no less	than the maximum value
	 * for antialiased points, rounded to the nearest integer
	 * value.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_VALUE</strong> is generated if <em>size</em>	is less	than or	equal
	 * to 0.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glPointSize</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <strong>glGet</strong>	with argument <strong>GL_POINT_SIZE</strong>
	 * <strong>glGet</strong>	with argument <strong>GL_POINT_SIZE_RANGE</strong>
	 * <strong>glGet</strong>	with argument <strong>GL_POINT_SIZE_GRANULARITY</strong>
	 * <strong>glIsEnabled</strong> with argument <strong>GL_POINT_SMOOTH</trong>
	 * </ul>
	 *
	 * @param size Specifies the diameter of rasterized points.  The initial value is 1.
	 * @see	#glEnable glEnable
	 */
	public void glPointSize(float size);

	/**
	 * <strong>glLineWidth</strong> specifies	the rasterized width of	both aliased
	 * and antialiased lines.  Using	a line width other than	1 has
	 * different effects, depending on whether line antialiasing is
	 * enabled.  To enable and disable line antialiasing, call
	 * <strong>glEnable</strong> and <strong>glDisable</strong> with argument <strong>GL_LINE_SMOOTH</strong>. Line
	 * antialiasing is initially disabled.
	 * <p/>
	 * If line antialiasing is disabled, the	actual width is
	 * determined by	rounding the supplied width to the nearest
	 * integer.  (If	the rounding results in	the value 0, it	is as
	 * if the line width were 1.)  If
	 * | DELTA x | >= | DELTA y |,
	 * i pixels are filled in each column that is rasterized, where
	 * i is the rounded value of <em>width</em>.  Otherwise, i pixels	are
	 * filled in each row that is rasterized.
	 * <p/>
	 * If antialiasing is enabled, line rasterization produces a
	 * fragment for each pixel square that intersects the region
	 * lying	within the rectangle having width equal	to the current
	 * line width, length equal to the actual length	of the line,
	 * and centered on the mathematical line	segment.  The coverage
	 * value	for each fragment is the window	coordinate area	of the
	 * intersection of the rectangular region with the
	 * corresponding	pixel square.  This value is saved and used in
	 * the final rasterization step.
	 * <p/>
	 * Not all widths can be	supported when line antialiasing is
	 * enabled. If an unsupported width is requested, the nearest
	 * supported width is used.  Only width 1 is guaranteed to be
	 * supported; others depend on the implementation.  To query
	 * the range of supported widths	and the	size difference
	 * between supported widths within the range, call <strong>glGet</strong>	with
	 * arguments <strong>GL_LINE_WIDTH_RANGE</strong>	and <strong>GL_LINE_WIDTH_GRANULARITY</strong>.
	 * <p/>
	 * <strong>NOTES - </strong>
	 * The line width specified by glLineWidth is always returned
	 * when <strong>GL_LINE_WIDTH</strong> is	queried.  Clamping and rounding	for
	 * aliased and antialiased lines	have no	effect on the
	 * specified value.
	 * Nonantialiased line width may	be clamped to an
	 * implementation-dependent maximum.  Although this maximum
	 * cannot be queried, it	must be	no less	than the maximum value
	 * for antialiased lines, rounded to the	nearest	integer	value.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_VALUE</strong> is generated	if width is less than or equal to 0.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glLineWidth</strong> is executed
	 * between the execution of <strong>glBegin</strong> and the corresponding execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_LINE_WIDTH</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_LINE_WIDTH_RANGE</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_LINE_WIDTH_GRANULARITY</strong>
	 * <li><strong>glIsEnabled</strong> with argument <strong>GL_LINE_SMOOTH</strong>
	 * </ul>
	 *
	 * @param width Specifies the width of	rasterized lines.  The initial value is 1.
	 * @see	#glEnable glEnable
	 */
	public void glLineWidth(float width);

	/**
	 * <strong>glPolygonMode</strong>	controls the interpretation of polygons	for
	 * rasterization.  face describes which polygons	mode applies
	 * to:  front-facing polygons (<strong>GL_FRONT</strong>), back-facing polygons
	 * (<strong>GL_BACK</strong>), or	both (<strong>GL_FRONT_AND_BACK</strong>).  The polygon mode
	 * affects only the final rasterization of polygons.  In
	 * particular, a	polygon's vertices are lit and the polygon is
	 * clipped and possibly culled before these modes are applied.
	 * <p/>
	 * Three	modes are defined and can be specified in mode:
	 * <p/>
	 * <ul>
	 * <li><strong>GL_POINT</strong>	Polygon	vertices that are marked as the	start
	 * of a boundary edge are drawn as	points.	 Point
	 * attributes such	as <strong>GL_POINT_SIZE</strong> and
	 * <strong>GL_POINT_SMOOTH</strong>	control	the rasterization of
	 * the points.  Polygon rasterization attributes
	 * other than GL_POLYGON_MODE</strong> have	no effect.
	 * <p/>
	 * <li><strong>GL_LINE</strong>	Boundary edges of the polygon are drawn	as
	 * line segments.	They are treated as connected
	 * line segments for line stippling; the line
	 * stipple	counter	and pattern are	not reset
	 * between	segments (see glLineStipple).  Line
	 * attributes such	as <strong>GL_LINE_WIDTH</strong> and
	 * <strong>GL_LINE_SMOOTH</strong> control the rasterization of
	 * the lines.  Polygon rasterization attributes
	 * other than <strong>GL_POLYGON_MODE</strong> have	no effect.
	 * <p/>
	 * <li><strong>GL_FILL</strong>	The interior of	the polygon is filled.
	 * Polygon	attributes such	as <strong>GL_POLYGON_STIPPLE</strong>
	 * and <strong>GL_POLYGON_SMOOTH</strong> control the
	 * rasterization of the polygon.
	 * </ul>
	 * <p/>
	 * <strong>EXAMPLES - </strong>
	 * To draw a surface with filled	back-facing polygons and
	 * outlined front-facing	polygons, call <strong>glPolygonMode</strong>(<strong>GL_FRONT</strong>,
	 * <strong>GL_LINE</strong>);
	 * <p/>
	 * <strong>NOTES - </strong>
	 * Vertices are marked as boundary or nonboundary with an edge
	 * flag.	 Edge flags are	generated internally by	the GL when it
	 * decomposes polygons; they can be set explicitly using <strong>glEdgeFlag</strong>.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if either <em>face</em> or <em>mode</em> is not
	 * an accepted value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glPolygonMode</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding	execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_POLYGON_MODE</strong>
	 * </ul>
	 *
	 * @param face Specifies the polygons that mode applies to.  Must be
	 *             <strong>GL_FRONT</strong> for front-facing polygons, <strong>GL_BACK</strong> for	back-
	 *             facing polygons, or <strong>GL_FRONT_AND_BACK</strong> for front- and
	 *             back-facing polygons.
	 * @param mode Specifies how polygons will be rasterized.  Accepted
	 *             values are <strong>GL_POINT</strong>, <strong>GL_LINE</strong>, and <strong>GL_FILL</strong>.  The
	 *             initial	value is <strong>GL_FILL</strong> for both front- and back-
	 *             facing polygons.
	 * @see #glBegin glBegin
	 * @see #glEdgeFlag glEdgeFlag
	 * @see #glLineStipple glLineStipple
	 * @see #glLineWidth glLineWidth
	 * @see #glPointSize glPointSize
	 * @see #glPolygonStipple glPolygonStipple
	 */
	public void glPolygonMode(int face, int mode);

	/**
	 * <strong>glCullFace</strong> specifies whether front- or back-facing numberOfFacets
	 * are culled (as specified by <em>mode</em>) when facet culling is
	 * enabled. Facet culling is initially disabled.	 To enable and
	 * disable facet	culling, call the <strong>glEnable</strong> and <strong>glDisable</strong>
	 * commands with	the argument <strong>GL_CULL_FACE</strong>.  Facets include
	 * triangles, quadrilaterals, polygons, and rectangles.
	 * <p/>
	 * <strong>glFrontFace</strong> specifies	which of the clockwise and
	 * counterclockwise numberOfFacets are front-facing and back-facing.
	 * See {@link #glFrontFace glFrontFace}.
	 * <p/>
	 * <strong>NOTES - </strong>
	 * If  <em>mode</em> is <strong>GL_FRONT_AND_BACK</strong>, no numberOfFacets are drawn, but
	 * other primitives such	as points and lines are	drawn.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if <em>mode</em> is not an accepted
	 * value.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glCullFace</strong> is executed
	 * between the execution	of <strong>glBegin</strong> and the corresponding
	 * execution of <strong>glEnd</strong>.
	 * </ul>
	 * <p/>
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glIsEnabled</strong> with argument <strong>GL_CULL_FACE</strong>
	 * <li><strong>glGet</strong> with argument <strong>GL_CULL_FACE_MODE</strong>
	 * </ul>
	 *
	 * @param mode Specifies whether front- or back-facing	numberOfFacets are
	 *             candidates for culling. Symbolic constants <strong>GL_FRONT</strong>,
	 *             <strong>GL_BACK</strong>, and <strong>GL_FRONT_AND_BACK</strong> are accepted.  The
	 *             initial	value is <strong>GL_BACK</strong>.
	 * @see #glEnable glEnable
	 * @see #glFrontFace glFrontFace
	 */
	public void glCullFace(int mode);

	public void glFrontFace(int direction);

	public void glClearDepth(float z);

	public void glDepthFunc(int zf);

	public void glDepthMask(boolean enabled);

	public void glDepthRange(float zNear, float zFar);

	/////////////////////// Clipping ///////////////////////

	public void glClipPlane(int plane, float[] equation);

	/////////////////////// Evaluators ///////////////////////

	public void glMap2f(int type, float u1, float u2, int uStride, int uOrder, float v1, float v2, int vStride,
			int vOrder, float points[][][]);

	public void glMapGrid2f(int nu, float u0, float u1, int nv, float v0, float v1);

	public void glEvalCoord2f(float u, float v);

	public void glEvalMesh2(int mode, int lowU, int highU, int lowV, int highV);

	/////////////////////// Picking ///////////////////////

	/**
	 * @param bufferLength
	 * @param buffer
	 */
	public void glSelectBuffer(int bufferLength, int buffer[]);

	public void glInitNames();

	public void glLoadName(int name);

	public void glPopName();

	public void glPushName(int name);

	/////////////////////// Feedback ///////////////////////

	public void glFeedbackBuffer(int bufferLength, int type, float buffer[]);

	/////////////////////// Accumulation  ///////////////////////

	public void glClearAccum(float r, float g, float b, float a);

	public void glAccum(int op, float value);

	/////////////////////// Display Lists ///////////////////////

	public int glGenLists(int range);

	public void glNewList(int list, int mode);

	public void glEndList();

	public void glCallList(int list);

	public void glDeleteLists(int list, int range);

	/////////////////////// Stenciling ///////////////////////

	public void glClearStencil(int clear);

	public void glStencilFunc(int func, int ref, int mask);

	public void glStencilMask(int mask);

	public void glStencilOp(int fail, int zfail, int zpass);

	/////////////////////// Blending ///////////////////////

	public void glBlendFunc(int sfactor, int dfactor);

	/////////////////////// Fog ///////////////////////

	public void glFogi(int p, int i);

	public void glFogf(int p, float f);

	public void glFogfv(int p, float[] f);

	/////////////////////// Raster / Bitmaps ///////////////////////

	public void glRasterPos2f(float x, float y);

	public void glRasterPos2fv(float[] p);

	public void glBitmap(int width, int height, float xOrig, float yOrig, float xMove, float yMove, byte[] bits);

	/////////////////////// Gets ////////////////////////

	public void glGetBooleanv(int pname, boolean[] result);

	public void glGetIntegerv(int pname, int[] result);

	public void glGetFloatv(int pname, float[] result);
	
	/////////////////////// Vertex Arrays ///////////////////////

	public void glVertexPointer(int size, int type, int stride, float[] pointer);

	public void glNormalPointer(int type, int stride, float[] pointer);

	public void glColorPointer(int size, int type, int stride, float[] pointer);

	//public void glTexCoordPointer(int size, int type, int stride, float[] pointer);
	//public void glEdgeFlagPointer(int stride, float[] pointer);

	//public void glGetPointerv(int pname, float[] params);

	public void glArrayElement(int i);

	public void glDrawArrays(int mode, int first, int count);

	//public void glDrawElements(int mode, int count, int type, int[] indices);
	//public void glInterleavedArrays(int format, int stride, float[] pointer);

	/////////////////////// Textures ///////////////////////
	
	

	public void glGenTextures(int n, int[] textures);

	//public void glDeleteTextures(int n, int[] textures);

	//public boolean glIsTexture(int texture);

	/**
	 * <strong>glBindTexture</strong> lets you create	or use a named texture.
	 * Calling <strong>glBindTexture</strong>	with
	 * target set to	<strong>GL_TEXTURE_1D</strong> or <strong>GL_TEXTURE_2D</strong> and texture set
	 * to the name of the newtexture	binds the texture name to the
	 * target. When a texture is bound to a target, the previous
	 * binding for that target is automatically broken.
	 * <p/>
	 * Texture names	are unsigned integers. The value zero is
	 * reserved to represent	the default texture for	each texture
	 * target.  Texture names and the corresponding texture
	 * contents are local to	the shared display-list	space (see
	 * glXCreateContext) of the current GL rendering context; two
	 * rendering contexts share texture names only if they also
	 * share	display	lists.
	 * <p/>
	 * You may use <strong>glGenTextures</strong> to generate	a set of new texture
	 * names.
	 * <p/>
	 * When a texture is first bound, it assumes the	dimensionality
	 * of its target:  A texture first bound	to <strong>GL_TEXTURE_1D</strong>
	 * becomes 1-dimensional	and a texture first bound to
	 * <strong>GL_TEXTURE_2D</strong>	becomes	2-dimensional. The state of a 1-
	 * dimensional texture immediately after	it is first bound is
	 * equivalent to	the state of the default <strong>GL_TEXTURE_1D</strong> at <strong>GL</strong>
	 * initialization, and similarly	for 2-dimensional textures.
	 * <p/>
	 * While	a texture is bound, <strong>GL</strong> operations on the target	to
	 * which	it is bound affect the bound texture, and queries of
	 * the target to	which it is bound return state from the	bound
	 * texture. If texture mapping of the dimensionality of the
	 * target to which a texture is bound is	active,	the bound
	 * texture is used.  In effect, the texture targets become
	 * aliases for the textures currently bound to them, and	the
	 * texture name zero refers to the default textures that	were
	 * bound	to them	at initialization.
	 * <p/>
	 * A texture binding created with <strong>glBindTexture</strong> remains active
	 * until	a different texture is bound to	the same target, or
	 * until	the bound texture is deleted with <strong>glDeleteTextures</strong>.
	 * <p/>
	 * Once created,	a named	texture	may be re-bound	to the target
	 * of the matching dimensionality as often as needed.  It is
	 * usually much faster to use <strong>glBindTexture</strong> to bind an existing
	 * named	texture	to one of the texture targets than it is to
	 * reload the texture image using <strong>glTexImage1D</strong> or <strong>glTexImage2D</strong>.
	 * For additional control over performance, use
	 * <strong>glPrioritizeTextures</strong>.
	 * <p/>
	 * <strong>glBindTexture</strong>	is included in display lists.
	 * <p/>
	 * <strong>NOTES - </strong>
	 * <strong>glBindTexture</strong>	is available only if the <strong>GL</strong> version is 1.1 or
	 * greater.
	 * <p/>
	 * <strong>ERRORS</strong>
	 * <ul>
	 * <li><strong>GL_INVALID_ENUM</strong> is generated if target is not	one of the
	 * allowable values.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if texture has a
	 * dimensionality which doesn't match that of target.
	 * <li><strong>GL_INVALID_OPERATION</strong> is generated if <strong>glBindTexture</strong> is
	 * executed between the execution of <strong>glBegin</strong> and	the
	 * corresponding	execution of <strong>glEnd</strong>.
	 * </ul>
	 *
	 * <strong>ASSOCIATED</strong>
	 * <ul>
	 * <li><strong>glGet</strong>	with argument <strong>GL_TEXTURE_1D_BINDING</strong>
	 * <li><strong>glGet</strong>	with argument <strong>GL_TEXTURE_2D_BINDING</strong>
	 * </ul>
	 *
	 * @param target Specifies the target	to which the texture is	bound.
	 * Must	be either <strong>GL_TEXTURE_1D</strong>	or <strong>GL_TEXTURE_2D</strong>.
	 * @param texture Specifies the name of a texture.
	 *
	 * @see #glAreTexturesResident
	 * @see #glDeleteTextures
	 * @see #glGenTextures
	 * @see #glGet
	 * @see #glGetTexParameter
	 * @see #glIsTexture
	 * @see #glPrioritizeTextures
	 * @see #glTexImage1D
	 * @see #glTexImage2D
	 * @see #glTexParameter
	 */
	public void glBindTexture(int target, int texture);
}
