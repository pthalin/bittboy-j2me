JNI_INCLUDE = -I/usr/include/classpath

CFLAGS = `pkg-config --cflags libpulse-simple` $(JNI_INCLUDE)
LDFLAGS = `pkg-config --libs libpulse-simple` 

SOURCES = org_thenesis_midpath_sound_backend_pulseaudio_PulseSink.c

OBJECTS=$(patsubst %.c, %.o, $(SOURCES))

all: libmidpathpulse.so

%.o : %.c
	$(CC) $(CFLAGS) -fPIC -g -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o

