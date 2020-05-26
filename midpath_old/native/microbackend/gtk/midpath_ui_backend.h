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
 
#include "midpath_ui_peer.h"

void key_entered(int eventtype, int keycode, int unicode_character);
gboolean key_event(GtkWidget *widget, GdkEventKey *event);
gboolean button_event(GtkWidget *widget, GdkEventButton *event);
gboolean on_darea_expose (GtkWidget *widget, GdkEventExpose *event, gpointer user_data);
gboolean motion_notify_event(GtkWidget *widget, GdkEventMotion *event );
gboolean delete_event(GtkWidget *widget, GdkEvent *event, gpointer data);
  
