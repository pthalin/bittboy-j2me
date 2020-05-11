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
#include <string.h>
#include <gtk/gtk.h>
#include <hildon/hildon-program.h>
#include <hildon/hildon-window.h>

#include "../midpath_ui_peer.h"
#include "../midpath_ui_backend.h"


static GtkIMContext *maemo_input_im_context;

static gboolean commit_text(GtkIMContext *context,
                            const gchar  *str,
                            gpointer *data) {  
  int i;
  for(i=0; i<strlen(str); i++) {
    key_entered(GDK_KEY_PRESS, -1, str[i]);
    key_entered(GDK_KEY_RELEASE, -1, str[i]);
  }
  return TRUE;
}


GtkWindow *create_window(char* title) {
  HildonProgram *program = HILDON_PROGRAM(hildon_program_get_instance());
  g_set_application_name(title);
  HildonWindow *window = HILDON_WINDOW(hildon_window_new());
  hildon_program_add_window(program, window);
  gtk_window_set_title(GTK_WINDOW(window), title);

  return GTK_WINDOW(window);
}

void raise_keyboard() {
  gtk_im_context_reset (maemo_input_im_context);
  hildon_gtk_im_context_show(maemo_input_im_context);
  gtk_im_context_focus_in (maemo_input_im_context);
}

void lower_keyboard() {
  hildon_gtk_im_context_hide(maemo_input_im_context);
}

void initialize_window_events(GtkWindow *window) {
  /** input context */
  maemo_input_im_context = gtk_im_multicontext_new();
  gtk_im_context_set_client_window(maemo_input_im_context, GTK_WIDGET(window)->window);
  g_signal_connect (maemo_input_im_context, "commit", G_CALLBACK (commit_text), NULL);  

  raise_keyboard();
}

void initialize_drawing_area_events(GtkWidget *darea) {
}
