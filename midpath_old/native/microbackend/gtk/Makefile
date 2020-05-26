JNI_INCLUDE = -I/usr/include/classpath

ifdef HILDON
# Gtk in Hildon AF
MY_CFLAGS = `pkg-config --cflags gtk+-2.0 hildon-1` $(JNI_INCLUDE) $(CFLAGS)
MY_LDFLAGS = `pkg-config --libs gtk+-2.0 hildon-1` $(LDFLAGS)

SOURCES=org_thenesis_microbackend_ui_gtk_GTKBackend.c maemo/maemo_input.c

else

# Plain Gtk
MY_CFLAGS = `pkg-config --cflags gtk+-2.0` $(JNI_INCLUDE) $(CFLAGS)
MY_LDFLAGS = `pkg-config --libs gtk+-2.0` $(LDFLAGS)

SOURCES=org_thenesis_microbackend_ui_gtk_GTKBackend.c standard/standard_input.c

endif

OBJECTS=$(patsubst %.c, %.o, $(SOURCES))

all: libmicrobackendgtk.so

%.o : %.c
	$(CC) $(MY_CFLAGS) -fPIC -g -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(MY_LDFLAGS) -shared -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o maemo/*.o
