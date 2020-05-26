//==========================================================================
//
//  Project:        libfg - Frame Grabber interface for Linux
//
//  Module:         Python 2.x language bindings
//
//  Description:    Provides bindings for the libfg framegrabber library
//                  to enable capture control from Python.
//
//  Author:         Gavin Baker <gavinb@antonym.org>
//
//  Homepage:       http://www.antonym.org/libfg
//
//--------------------------------------------------------------------------
//
//  libfg - Frame Grabber interface for Linux
//  Copyright (c) 2002 Gavin Baker
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
//  or obtain a copy from the GNU website at http://www.gnu.org/
//
//==========================================================================

#include <Python.h>

#include "capture.h"

#include <string.h>

//--------------------------------------------------------------------------

// Exception
static PyObject* GrabberError;

// Forward typedef for class
staticforward PyTypeObject fg_GrabberType;

//--------------------------------------------------------------------------
//
//  Type:           fg_GrabberObject
//
//  Description:    Represents a Grabber instance at runtime, incorporating
//                  the struct from libfg to do the real work.
//
//--------------------------------------------------------------------------
typedef struct
{
    PyObject_HEAD               // Python's overHEAD
    FRAMEGRABBER*   fg;         // Real framegrabber instance from libfg

} fg_GrabberObject;

//--------------------------------------------------------------------------

static PyObject* fg_Grabber_save_frame( PyObject* self, PyObject* args )
{
    fg_GrabberObject* this = (fg_GrabberObject*)self;
    FRAME*  fr;
    char*   filename;

    if ( !PyArg_ParseTuple( args, "s", &filename ) )
        return NULL;

    printf( "Saving frame to %s...\n", filename );

    fr = fg_grab( this->fg );
    frame_save( fr, filename );
    frame_release( fr );

	Py_INCREF(Py_None);
	return Py_None;
}

//--------------------------------------------------------------------------

static PyObject* fg_Grabber_set_source( PyObject* self, PyObject* args )
{
    fg_GrabberObject* this = (fg_GrabberObject*)self;
    int src;

    if ( !PyArg_ParseTuple( args, "i", &src ) )
        return NULL;

    printf( "fg_set_source(%d)\n", src );

    fg_set_source( this->fg, src );

	Py_INCREF(Py_None);
	return Py_None;
}

//--------------------------------------------------------------------------

static PyObject* fg_Grabber_set_channel( PyObject* self, PyObject* args )
{
    fg_GrabberObject* this = (fg_GrabberObject*)self;
    float freq;

    if ( !PyArg_ParseTuple( args, "f", &freq ) )
        return NULL;

    fg_set_channel( this->fg, freq );

	Py_INCREF(Py_None);
	return Py_None;
}

//--------------------------------------------------------------------------

static PyObject* fg_Grabber_dump_info( PyObject* self, PyObject* args )
{
    fg_GrabberObject* this = (fg_GrabberObject*)self;

    fg_dump_info( this->fg );

	Py_INCREF(Py_None);
	return Py_None;
}

//--------------------------------------------------------------------------

static PyMethodDef fg_Grabber_methods[] =
{
	{
        "save_frame",
        (PyCFunction)fg_Grabber_save_frame,
        METH_VARARGS
    },
    {
        "set_source",
        (PyCFunction)fg_Grabber_set_source,
        METH_VARARGS
    },
    {
        "set_channel",
        (PyCFunction)fg_Grabber_set_channel,
        METH_VARARGS
    },
    {
        "dump_info",
        (PyCFunction)fg_Grabber_dump_info,
        METH_VARARGS
    },
	{
        NULL,
		NULL
    }
};

//--------------------------------------------------------------------------

static PyObject* fg_Grabber_new( PyObject* self, PyObject* args )
{
    fg_GrabberObject* this;

    if ( !PyArg_ParseTuple( args, ":new_Grabber" ) )
        return NULL;

    this = PyObject_New( fg_GrabberObject, &fg_GrabberType );

    this->fg = fg_open( NULL );

    return (PyObject*)this;
}

//--------------------------------------------------------------------------

static void fg_Grabber_dealloc( PyObject* self )
{
    fg_GrabberObject* fg = (fg_GrabberObject*)self;

    fg_close( fg->fg );

    PyObject_Del( self );
}

//--------------------------------------------------------------------------

static PyObject* fg_Grabber_getattr( fg_GrabberObject* fg,
                                          char* name )
{
    /*
	if (xp->x_attr != NULL )
    {
		PyObject *v = PyDict_GetItemString(xp->x_attr, name);
		if (v != NULL)
        {
			Py_INCREF(v);
			return v;
		}
	}
    */

	return Py_FindMethod( fg_Grabber_methods, (PyObject*)fg, name );
}

//--------------------------------------------------------------------------

static PyTypeObject fg_GrabberType =
{
    PyObject_HEAD_INIT(NULL)
    0,
    "Grabber",
    sizeof( fg_GrabberObject ),
    0,
    fg_Grabber_dealloc,                             // tp_dealloc
    0,                                              // tp_print
    fg_Grabber_getattr,                             // tp_getattr
    0,                                              // tp_setattr
    0,                                              // tp_compare
    0,                                              // tp_repr
    0,                                              // tp_as_number
    0,                                              // tp_as_sequence
    0,                                              // tp_as_mapping
    0,                                              // tp_hash
};


//--------------------------------------------------------------------------
//
//  Structure:      fg_framegrabber_Methods
//
//  Description:    Registers the methods for the FRAMEGRABBER class.
//
//--------------------------------------------------------------------------
static PyMethodDef fg_framegrabber_Methods[] =
{
    // Constructor

    {   "Grabber",
        fg_Grabber_new,
        METH_VARARGS,
        "Create a new Frame Grabber control object."
    },

    // Sentinel

    {
        NULL,
        NULL,
        0,
        NULL
    }
};


//--------------------------------------------------------------------------
//
//  Function:       initfg
//
//  Description:    Initialises the library, registers the methods with
//                  the Python interpreter.
//
//--------------------------------------------------------------------------
void initfg( void )
{
    PyObject* mod;
    PyObject* dict;

    mod = Py_InitModule( "fg", fg_framegrabber_Methods );

    // Register our own exception for frame grabber errors
    dict = PyModule_GetDict( mod );
    GrabberError = PyErr_NewException( "fg.error", NULL, NULL );
    PyDict_SetItemString( dict, "error", GrabberError );
}

//==========================================================================
