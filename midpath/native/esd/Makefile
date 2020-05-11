JNI_INCLUDE = -I/usr/include/classpath

CFLAGS = `pkg-config --cflags esound` $(JNI_INCLUDE)
LDFLAGS = `pkg-config --libs esound` 

SOURCES = org_thenesis_midpath_sound_backend_esd_EsdSink.c

OBJECTS=$(patsubst %.c, %.o, $(SOURCES))

all: libmidpathesd.so

%.o : %.c
	$(CC) $(CFLAGS) -fPIC -g -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o

