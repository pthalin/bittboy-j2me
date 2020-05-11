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

package org.thenesis.pjogles.render;

/**
 * @author tdinneen
 */
public final class GLRenderRenderer //implements GLRenderer
{
	//    public GLSoftwareContext gc;
	//
	//    private GLBitmapRenderer bitmapRenderer;
	//    private GLPointRenderer pointRenderer;
	//    private GLLineRenderer lineRenderer;
	//    private GLPolygonRenderer polygonRenderer;
	//
	//    // points
	//    private GLFlatPointRenderer genericPointRenderer;
	//
	//    // lines
	//    private GLFlatLineRenderer genericLineRenderer;
	//    private GLWideLineRenderer wideLineRenderer;
	//    private GLStippleLineRenderer stippleLineRenderer;
	//    private GLWideStippleLineRenderer wideStippleLineRenderer;
	//
	//    // polygons
	//    private GLGenericPolygonRenderer genericPolygonRenderer;
	//
	//    public GLRenderRenderer(GLSoftwareContext gc)
	//    {
	//        this.gc = gc;
	//
	//        pointRenderer = genericPointRenderer = new GLFlatPointRenderer(this);
	//        lineRenderer = genericLineRenderer = new GLFlatLineRenderer(this);
	//        polygonRenderer = genericPolygonRenderer = new GLGenericPolygonRenderer(this);
	//
	//        wideLineRenderer = new GLWideLineRenderer(this);
	//        stippleLineRenderer = new GLStippleLineRenderer(this);
	//        wideStippleLineRenderer = new GLWideStippleLineRenderer(this);
	//
	//        bitmapRenderer = new GLRenderBitmapRenderer(this);
	//    }
	//
	//    public void store(GLFragment fragment)
	//    {
	//        /*
	//            __GLcontext *gc;
	//            GLint x, y;
	//
	//            gc = cfb->buf.gc;
	//
	//            x = frag->x;
	//            y = frag->y;
	//
	//            // Pixel ownership, scissor
	//            if (x < gc->transform.clipX0 || y < gc->transform.clipY0 ||
	//                x >= gc->transform.clipX1 || y >= gc->transform.clipY1) {
	//            return;
	//            }
	//
	//            if (!gc->frontBuffer.alphaTestFuncTable[(GLint) (frag->color[__GL_PRIMARY_COLOR].a
	//                                * gc->constants.alphaTableConv)]) {
	//            // alpha test failed
	//            return;
	//            }
	//            if (!(*gc->stencilBuffer.testFunc)(&gc->stencilBuffer, x, y)) {
	//            // stencil test failed
	//            (*gc->stencilBuffer.failOp)(&gc->stencilBuffer, x, y);
	//            return;
	//            }
	//            if (!(*gc->depthBuffer.store)(&gc->depthBuffer, x, y, frag->z)) {
	//            // depth buffer test failed
	//            (*gc->stencilBuffer.passDepthFailOp)(&gc->stencilBuffer, x, y);
	//            return;
	//            }
	//            (*gc->stencilBuffer.depthPassOp)(&gc->stencilBuffer, x, y);
	//
	//
	//            (*gc->procs.cfbStore)( cfb, frag );
	//        */
	//
	//        GLColorBuffer colorBuffer = gc.drawBuffer;
	//        GLStencilBuffer stencilBuffer = gc.frameBuffer.stencilBuffer;
	//        GLDepthBuffer depthBuffer = gc.frameBuffer.depthBuffer;
	//
	//        boolean stencilEnabled = (gc.state.enables.general & __GL_STENCIL_TEST_ENABLE) != 0;
	//        boolean depthEnabled = (gc.state.enables.general & __GL_DEPTH_TEST_ENABLE) != 0;
	//
	//        if (stencilEnabled && !stencilBuffer.testFunction(fragment))
	//        {
	//            stencilBuffer.failOperation(fragment);
	//            return;
	//        }
	//
	//        if (depthEnabled && (depthBuffer.store(fragment) == false))
	//        {
	//            if (stencilEnabled)
	//                stencilBuffer.depthFailOperation(fragment);
	//
	//            return;
	//        }
	//
	//        if (stencilEnabled)
	//            stencilBuffer.depthPassOperation(fragment);
	//
	//        colorBuffer.store(fragment);
	//    }
	//
	//    public void pickPointProcs()
	//    {
	//    }
	//
	//    public void pickLineProcs()
	//    {
	////        if (gc.state.line.aliasedWidth != 1)
	////        {
	////            if ((gc.state.enables.general & __GL_LINE_STIPPLE_ENABLE) != 0)
	////                lineRenderer = wideStippleLineRenderer;
	////            else
	////                lineRenderer = wideLineRenderer;
	////        }
	////        else
	////        {
	////            if ((gc.state.enables.general & __GL_LINE_STIPPLE_ENABLE) != 0)
	////                lineRenderer = stippleLineRenderer;
	////            else
	////                lineRenderer = genericLineRenderer;
	////        }
	//    }
	//
	//    public void pickPolygonProcs()
	//    {
	//    }
	//
	//    public void renderPoint(GLVertex v)
	//    {
	//        pointRenderer.renderPoint(v);
	//    }
	//
	//    public void renderLine(GLVertex v1, GLVertex v2)
	//    {
	//        lineRenderer.renderLine(v1, v2);
	//    }
	//
	//    public void renderLine(GLLineSegment line)
	//    {
	//        lineRenderer.renderLine(line);
	//    }
	//
	//    public void renderPolygon(GLPolygon polygon) throws GLRenderException
	//    {
	//        polygonRenderer.renderPolygon(polygon);
	//    }
	//
	//    public void renderBitmap(GLBitmap bitmap)
	//    {
	//        bitmapRenderer.renderBitmap(bitmap);
	//    }
}
