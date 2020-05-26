JNI_INCLUDE = -I/usr/include/classpath

CFLAGS = $(JNI_INCLUDE)
LDFLAGS =  

SOURCES = org_thenesis_microbackend_ui_fb_FBBackend.c

OBJECTS = $(patsubst %.c, %.o, $(SOURCES))

all: libmicrobackendfb.so

%.o : %.c
	$(CC) $(CFLAGS) -fPIC -g -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o 


