/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * Copyright (C) Sebastian Mancke
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
 
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <gtk/gtk.h>

#include "../midpath_ui_peer.h"
#include "../midpath_ui_backend.h"


GtkWindow* create_window(char* title) {
  GtkWindow *window = GTK_WINDOW(gtk_window_new(GTK_WINDOW_TOPLEVEL));
  gtk_window_set_title(window, title);
  return window;
}

void raise_keyboard() {
  /** do nothing here in this implementation */
}

void lower_keyboard() {
  /** do nothing here in this implementation */
}

void initialize_window_events(GtkWindow *window) {
  /** do nothing here in this implementation */
}

void initialize_drawing_area_events(GtkWidget *darea) {
  /** do nothing here in this implementation */
}

