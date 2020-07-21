JNI_INCLUDE=''
CC=''

CFLAGS = $(JNI_INCLUDE) 
CFLAGS += -Os


LDFLAGS = -s

SOURCES = java_nio_ByteBufferImpl.c

OBJECTS = $(patsubst %.c, %.o, $(SOURCES))

all: libmidpathnio.so

%.o : %.c
	$(CC) $(CFLAGS) -fPIC -g -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -fPIC -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o 


