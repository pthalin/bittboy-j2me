#!/usr/bin/env python

#===========================================================================
# 
#   Project:        libfg - Frame Grabber interface for Linux
# 
#   Module:         Python distutils build script
# 
#   Description:    Uses Python's distutils to build the extension library.
# 
#   Author:         Gavin Baker <gavinb@antonym.org>
# 
#   Homepage:       http://www.antonym.org/libfg
# 
#---------------------------------------------------------------------------
# 
#   libfg - Frame Grabber interface for Linux
#   Copyright (c) 2002 Gavin Baker
# 
#   This library is free software; you can redistribute it and/or
#   modify it under the terms of the GNU Lesser General Public
#   License as published by the Free Software Foundation; either
#   version 2.1 of the License, or (at your option) any later version.
# 
#   This library is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
#   Lesser General Public License for more details.
# 
#   You should have received a copy of the GNU Lesser General Public
#   License along with this library; if not, write to the Free Software
#   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
#   or obtain a copy from the GNU website at http://www.gnu.org/
# 
#===========================================================================

from distutils.core import setup, Extension

fg_module = Extension('fg',
                      libraries = ['fg'],
                      library_dirs = ['.'],
                      sources = ['fgmodule.c'])

setup (name = 'FrameGrabber',
       version = '0.1a',
       description = 'Python bindings for the libfg framegrabber interface.',
       author = 'Gavin Baker',
       author_email = 'gavinb@antonym.org',
       url = 'http://www.antonym.org/libfg',
       ext_modules = [fg_module])
