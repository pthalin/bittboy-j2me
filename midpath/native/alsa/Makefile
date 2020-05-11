JNI_INCLUDE = -I/usr/include/classpath

CFLAGS = `pkg-config --cflags alsa` $(JNI_INCLUDE)
LDFLAGS = `pkg-config --libs alsa` 

SOURCES = org_thenesis_midpath_sound_backend_alsa_AlsaSink.c

OBJECTS=$(patsubst %.c, %.o, $(SOURCES))

all: libmidpathalsa.so

%.o : %.c
	$(CC) $(CFLAGS) -fPIC -g -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o

