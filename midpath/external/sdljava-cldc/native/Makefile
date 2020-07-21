JNI_INCLUDE=''
SDL_INCLUDE=''


CROSS_COMPILE=/opt/buildroot-bittboy/output/host/bin/arm-buildroot-linux-uclibcgnueabi-
CC = $(CROSS_COMPILE)gcc
SYSROOT     := $(shell $(CC) --print-sysroot)

CFLAGS=-Os -fPIC 

INCLUDE=$(JNI_INCLUDE) $(SDL_INCLUDE)

SDLJAVA_OBJ=SDLAudio_wrap.o\
            SDLEvent_wrap.o\
            SDLMain_wrap.o\
            SDLVideo_wrap.o\
            SDLJoystick_wrap.o\
            SDLCdrom_wrap.o

SDLJAVA_LIB=$(shell $(SYSROOT)/usr/bin/sdl-config --static-libs)

SDLJAVA_MIXER_OBJ=SDLMixer_wrap.o
SDLJAVA_MIXER_LIB=-lSDL_mixer $(shell $(SYSROOT)/usr/bin/sdl-config --static-libs)

SDLJAVA_TTF_OBJ=SDLTTF_wrap.o
SDLJAVA_TTF_LIB=-lSDL_ttf $(shell $(SYSROOT)/usr/bin/sdl-config --static-libs)

SDLJAVA_IMAGE_OBJ=SDLImage_wrap.o
SDLJAVA_IMAGE_LIB=-lSDL_image $(shell $(SYSROOT)/usr/bin/sdl-config --static-libs)

SDLJAVA_GFX_OBJ=SDLGFX_wrap.o SDLVideo_wrap.o
SDLJAVA_GFX_LIB=-lSDL_gfx $(shell $(SYSROOT)/usr/bin/sdl-config --static-libs)

all:	libsdljava.so libsdljava_mixer.so libsdljava_ttf.so libsdljava_image.so libsdljava_gfx.so

%.o: %.c
	$(CC) $(CFLAGS) $(INCLUDE) -c $< -o $@

libsdljava.so: $(SDLJAVA_OBJ)
	$(CC) -shared -o libsdljava.so $(SDLJAVA_OBJ) $(SDLJAVA_LIB)

libsdljava_mixer.so: $(SDLJAVA_MIXER_OBJ)
	$(CC) -shared -s -o libsdljava_mixer.so $(SDLJAVA_MIXER_OBJ) $(SDLJAVA_MIXER_LIB)

libsdljava_ttf.so: $(SDLJAVA_TTF_OBJ)
	$(CC) -shared -s -o libsdljava_ttf.so $(SDLJAVA_TTF_OBJ) $(SDLJAVA_TTF_LIB)

libsdljava_image.so: $(SDLJAVA_IMAGE_OBJ)
	$(CC) -shared -s -o libsdljava_image.so $(SDLJAVA_IMAGE_OBJ) $(SDLJAVA_IMAGE_LIB)

libsdljava_gfx.so: $(SDLJAVA_GFX_OBJ)
	$(CC) -shared -s -o libsdljava_gfx.so $(SDLJAVA_GFX_OBJ) $(SDLJAVA_GFX_LIB)

clean:
	rm *.o *.so
