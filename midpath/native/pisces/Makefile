JNI_INCLUDE = -I/usr/include/classpath

CFLAGS = $(JNI_INCLUDE)
LDFLAGS =

SOURCES = JAbstractSurface.c \
		  PiscesBlit.c \
		  PiscesPipelines.c \
		  PiscesTransform.c \
		  JNativeSurface.c \
		  JPiscesRenderer.c \
		  PiscesLibrary.c \
		  PiscesRenderer.c \
		  PiscesUtil.c \
		  JNIUtil.c \
		  JTransform.c \
		  PiscesMath.c \
		  PiscesSysutils.c

OBJECTS=$(patsubst %.c, %.o, $(SOURCES))

all: libmidpath-pisces.so

%.o : %.c
	$(CC) $(CFLAGS) -fPIC -O3 -g -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o

