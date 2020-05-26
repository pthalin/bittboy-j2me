JNI_INCLUDE=-I/usr/lib/jvm/jdk1.6.0_45_x86/include -I/usr/lib/jvm/jdk1.6.0_45_x86/include/linux

CROSS_COMPILE=/opt/buildroot-bittboy/output/host/bin/arm-buildroot-linux-uclibcgnueabi-
CC = $(CROSS_COMPILE)gcc


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


