JNI_INCLUDE = -I/usr/include/classpath

CFLAGS = $(JNI_INCLUDE)
LDFLAGS =

SOURCES = gxapi_anchor.c \
	  gxj_font_bitmap.c \
	  gxj_graphics_asm.c \
	  gxj_graphics.c \
	  gxj_image.c \
	  gxj_putpixel.c \
	  gxj_text.c \
	  gxj_util.c \
	  gxj_screen_buffer.c \
	  midp_logging.c \
	  org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJFont.c \
	  org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics.c \
	  org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData.c \
	  org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageDataFactory.c

OBJECTS=$(patsubst %.c, %.o, $(SOURCES))

all: libmicrobackend-gxj.so

%.o : %.c
	$(CC) $(CFLAGS) -fPIC -O3 -g -std=c99 -c -Wall -o $(patsubst %.c, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o

