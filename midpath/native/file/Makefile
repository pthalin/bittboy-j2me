JNI_INCLUDE=''

CROSS_COMPILE=/opt/buildroot-bittboy/output/host/bin/arm-buildroot-linux-uclibcgnueabi-
CC = $(CROSS_COMPILE)gcc

CFLAGS = $(JNI_INCLUDE) 
CFLAGS += -Os

LDFLAGS = -s

SOURCES = jcl.c javaio.c org_thenesis_midpath_io_backend_cldc_File.c \
org_thenesis_midpath_io_backend_cldc_RandomAccessFile.c
		  

OBJECTS=$(patsubst %.c, %.o, $(SOURCES))

all: libmidpathfile.so

%.o : %.c
	$(CC) $(CFLAGS) -fPIC -g -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -fPIC -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o

