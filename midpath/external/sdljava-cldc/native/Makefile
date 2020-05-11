CC = /opt/mipsel-linux-uclibc/usr/bin/mipsel-linux-gcc

CFLAGS=-Os -fPIC -g -mips32 -mtune=mips32 -mno-mips16 -mbranch-likely -pipe -G0 -fno-common -fpeel-loops

JNI_INCLUDE=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux

SDL_INCLUDE=-I/usr/include/SDL -I/usr/local/include -I/usr/local/include/SDL

INCLUDE=-I. $(JNI_INCLUDE) $(SDL_INCLUDE)

SDLJAVA_OBJ=SDLAudio_wrap.o\
            SDLEvent_wrap.o\
            SDLMain_wrap.o\
            SDLVideo_wrap.o\
            SDLJoystick_wrap.o\
            SDLCdrom_wrap.o

SDLJAVA_LIB=-lSDL

SDLJAVA_MIXER_OBJ=SDLMixer_wrap.o
SDLJAVA_MIXER_LIB=-lSDL -lSDL_mixer

SDLJAVA_TTF_OBJ=SDLTTF_wrap.o
SDLJAVA_TTF_LIB=-lSDL -lSDL_ttf

SDLJAVA_IMAGE_OBJ=SDLImage_wrap.o
SDLJAVA_IMAGE_LIB=-lSDL -lSDL_image

SDLJAVA_GFX_OBJ=SDLGFX_wrap.o SDLVideo_wrap.o
SDLJAVA_GFX_LIB=-lSDL -lSDL_gfx

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
