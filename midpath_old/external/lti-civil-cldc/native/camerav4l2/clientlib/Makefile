# Desc: Camera V4L2 driver

CFLAGS_D=$(C_FLAGS) -fpic $(CFLAGS_PLAYER)
LFLAGS_D=$(L_FLAGS) -fpic
CFLAGS_S=$(C_FLAGS) $(CFLAGS_PLAYER)
#SO_VER=.1.0.1
SO_VER=
#SO_NAM=.1
SO_NAM=

%.o: %.cpp
	$(CXX) -static $(CFLAGS_S) -c $<

%.o: %.c
	$(CC) -static $(CFLAGS_S) -c $<


all: libplayerv4l2.so$(SO_VER) libplayerv4l2c.so$(SO_VER) libplayerv4l2s.a libplayerv4l2cs.a

libplayerv4l2.so$(SO_VER): playerv4l2.h playerv4l2.cpp plv4l2tools.c plv4l2tools.h
	$(CXX) -shared $(CFLAGS_D) $(LFLAGS_D) -Wl,-soname,libplayerv4l2.so$(SO_NAM) -o libplayerv4l2.so$(SO_VER) playerv4l2.cpp plv4l2tools.c

libplayerv4l2s.a: playerv4l2.o plv4l2tools.o
	ar rcs libplayerv4l2s.a   playerv4l2.o plv4l2tools.o

libplayerv4l2c.so$(SO_VER): playerv4l2c.h playerv4l2c.c plv4l2tools.c plv4l2tools.h
	$(CXX) -shared $(CFLAGS_D) $(LFLAGS_D) -Wl,-soname,libplayerv4l2c.so$(SO_NAM) -o libplayerv4l2c.so$(SO_VER) playerv4l2c.c plv4l2tools.c

libplayerv4l2cs.a: playerv4l2c.o plv4l2tools.o
	ar rcs libplayerv4l2cs.a      playerv4l2c.o plv4l2tools.o

clean:
	rm -f *.o *.so$(SO_VER) *.a


playerv4l2c.o: playerv4l2c.h playerv4l2c.c plv4l2tools.h
playerv4l2.o: playerv4l2.h playerv4l2.cpp plv4l2tools.h
plv4l2tools.o: plv4l2tools.h plv4l2tools.c


