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

import org.thenesis.pjogles.GLConstants;

/**
 * @author tdinneen
 */
public final class GLGeometry implements GLConstants {
	//public static final int MAX_NUMBER_VERTICES = 16;

	//    private GLVertex vertex[] = new GLVertex[MAX_NUMBER_VERTICES];
	//    private GLSoftwareContext gc;
	//    public GLRenderer renderer;
	//    private GLPolygon polygon;
	//    private GLLineSegment line;
	//    private int index;
	//
	//    /*
	//    ** Provoking vertices.  For flat shaded primitives the triangle
	//    ** renderer needs to know which vertices provoked the primitive to
	//    ** properly assign the color during scan conversion.  This is kept
	//    ** around as its a big pain to remember which vertices was provoking
	//    ** during clipping (and to keep its parameters right).
	//    */
	//    public GLVertex provoking;
	//
	//    public GLGeometry(GLSoftwareContext gc, GLRenderer renderer)
	//    {
	//        for (int i = 0; i < MAX_NUMBER_VERTICES; ++i)
	//            vertex[i] = new GLVertex();
	//
	//        this.gc = gc;
	//        this.renderer = renderer;
	//
	//        this.polygon = new GLPolygon();
	//        this.line = new GLLineSegment();
	//    }
	//
	//    public void begin()
	//    {
	//        index = 0;
	//    }
	//
	//    public void end() throws GLRenderException
	//    {
	//        switch (gc.beginMode)
	//        {
	//            case GL_LINE_LOOP:
	//
	//                if (index == 2)
	//                    renderLine(1, 0);
	//
	//                break;
	//
	//            case GL_POLYGON:
	//
	//                if (index < 3)
	//                {
	//                    switch (index)
	//                    {
	//                        case 0:
	//                            break;
	//                        case 1:
	//                            renderPoint(vertex[0]);
	//                            break;
	//                        case 2:
	//                            renderLine(1, 0);
	//                            break;
	//                    }
	//
	//                    break;
	//                }
	//
	//                renderPolygon(index);
	//
	//                break;
	//
	//            default:
	//
	//                break;
	//        }
	//    }
	//
	//    public void vertex(GLVertex v) throws GLRenderException
	//    {
	//        v.validateVertex(gc, __GL_HAS_CLIP);
	//
	//        // check against frustum and user clip planes
	//        // internally calcs eye if testing against user planes
	//
	//        v.validateVertex(gc, __GL_HAS_CLIPCODES);
	//
	//        gc.geometry.provoking = v;
	//
	//        // save any required current state variables
	//        // such as normal, color etc...
	//
	//        v.saveCurrentState(gc);
	//
	//        switch (gc.beginMode)
	//        {
	//            case GL_POINTS:
	//
	//                if (v.clipCodes == 0)
	//                    renderPoint(v);
	//
	//                break;
	//
	//            case GL_LINES:
	//
	//                if (index == 1)
	//                {
	//                    vertex[1].set(v);
	//                    renderLine(0, 1);
	//                    index = 0;
	//                }
	//                else
	//                {
	//                    vertex[0].set(v);
	//                    index = 1;
	//                }
	//
	//                break;
	//
	//            case GL_LINE_STRIP:
	//
	//                if (index == 1)
	//                {
	//                    vertex[1].set(v);
	//                    renderLine(0, 1);
	//                    vertex[0].set(vertex[1]);
	//                }
	//                else
	//                {
	//                    vertex[0].set(v);
	//                    index = 1;
	//                }
	//
	//                break;
	//
	//            case GL_LINE_LOOP:
	//
	//                if (index == 2)
	//                {
	//                    vertex[2].set(v);
	//                    renderLine(1, 2);
	//                    vertex[1].set(vertex[2]);
	//                }
	//                else
	//                {
	//                    vertex[0].set(v);
	//                    vertex[1].set(v);
	//
	//                    index = 2;
	//                }
	//
	//                break;
	//
	//            case GL_TRIANGLES:
	//
	//                if (index == 2)
	//                {
	//                    vertex[2].set(v);
	//                    renderPolygon(3);
	//
	//                    index = 0;
	//                }
	//                else
	//                    vertex[index++].set(v);
	//
	//                break;
	//
	//            case GL_TRIANGLE_STRIP:
	//
	//                if (index == 0 || index == 1)
	//                    vertex[index++].set(v);
	//                else
	//                {
	//                    vertex[2].set(v);
	//                    renderPolygon(3);
	//
	//                    if (index == 2)
	//                    {
	//                        vertex[0].set(vertex[2]);
	//                        index = 3;
	//                    }
	//                    else
	//                    {
	//                        vertex[1].set(vertex[2]);
	//                        index = 2;
	//                    }
	//                }
	//
	//                break;
	//
	//            case GL_TRIANGLE_FAN:
	//
	//                if (index == 2)
	//                {
	//                    vertex[2].set(v);
	//                    renderPolygon(3);
	//                    vertex[1].set(vertex[2]);
	//                }
	//                else
	//                    vertex[index++].set(v);
	//
	//                break;
	//
	//            case GL_QUADS:
	//
	//                if (index == 3)
	//                {
	//                    vertex[3].set(v);
	//                    renderPolygon(4);
	//
	//                    index = 0;
	//                }
	//                else
	//                    vertex[index++].set(v);
	//
	//                break;
	//
	//            case GL_QUAD_STRIP:
	//
	//                if (index == 3)
	//                {
	//                    vertex[2].set(v);
	//                    renderPolygon(4);
	//
	//                    vertex[0].set(vertex[3]);
	//                    vertex[1].set(vertex[2]);
	//
	//                    index = 2;
	//                }
	//                else if (index == 2)
	//                {
	//                    vertex[3].set(v);
	//                    index = 3;
	//                }
	//                else
	//                    vertex[index++].set(v);
	//
	//                break;
	//
	//            case GL_POLYGON:
	//
	//                if (index >= MAX_NUMBER_VERTICES)
	//                    throw new GLRendererException("GLGeometry.vertices(GLVertex) - The maximum GL_POLYGON size is '" + MAX_NUMBER_VERTICES + "'");
	//                else
	//                    vertex[index++].set(v);
	//
	//                break;
	//
	//            default:
	//
	//                throw new GLInvalidEnumException("GLGeometry.vertices(GLVertex)");
	//        }
	//    }
	//
	//    public void renderPoint(GLVertex v)
	//    {
	//        v.validateVertex(gc, __GL_HAS_WINDOW);
	//        renderer.renderPoint(v);
	//    }
	//
	//    public void renderLine(int index1, int index2)
	//    {
	//        GLVertex v1 = vertex[index1];
	//        GLVertex v2 = vertex[index2];
	//
	//        gc.geometry.provoking = v2;
	//
	//        line.v1 = v1;
	//        line.v2 = v2;
	//
	//        if ((v1.clipCodes | v2.clipCodes) != 0)
	//        {
	//            /*
	//            ** The line must be clipped more carefully.  Cannot trivially
	//            ** accept the lines.
	//            */
	//            if ((v1.clipCodes & v2.clipCodes) != 0)
	//            {
	//                /*
	//                ** Trivially reject the line.  If anding the codes is non-zero then
	//                ** every vertices in the line is outside of the same set of
	//                ** clipping planes (at least one).
	//                */
	//                return;
	//            }
	//
	//            if (line.clipLine(gc))
	//                return;
	//        }
	//
	//        v1.validateVertex(gc, __GL_HAS_WINDOW);
	//        v2.validateVertex(gc, __GL_HAS_WINDOW);
	//
	//        renderer.renderLine(v1, v2);
	//    }
	//
	//    public void renderPolygon(int count) throws GLRenderException
	//    {
	//        /*
	//        ** Generate array of addresses of the verticies.  And all the
	//        ** clip codes together while we are at it.
	//        */
	//        int andCodes = ~0;
	//        int orCodes = 0;
	//
	//        polygon.removeAllVertices();
	//
	//        for (int i = 0; i < count; ++i)
	//        {
	//            GLVertex v = vertex[i];
	//
	//            andCodes &= v.clipCodes;
	//            orCodes |= v.clipCodes;
	//
	//            polygon.addVertex(v);
	//        }
	//
	//        if ((andCodes & __GL_CLIP_MASK) != 0)
	//        {
	//            /*
	//            ** Trivially reject the polygon.  If andCodes is non-zero then
	//            ** every vertices in the polygon is outside of the same set of
	//            ** clipping planes (at least one).
	//            */
	//            return;
	//        }
	//
	//        if (polygon.clipPolygon(gc, orCodes & __GL_CLIP_MASK))
	//            return;
	//
	//        polygon.validateVertices(gc, __GL_HAS_WINDOW);
	//
	//        renderer.renderPolygon(polygon);
	//    }
}
