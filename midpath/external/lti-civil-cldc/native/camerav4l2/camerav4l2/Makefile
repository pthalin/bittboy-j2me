# Desc: Camera V4L2 driver


CFLAGS=$(C_FLAGS) -fpic $(CFLAGS_PLAYER)
LFLAGS=$(L_FLAGS) -fpic

%.o: %.cpp
	$(CXX) $(CFLAGS) -c $<

%.o: %.c
	$(CC) $(CFLAGS) -c $<

camerav4l2.so: camerav4l2.o v4l2capture.o
	$(CXX) $(LFLAGS) -shared -nostartfiles -o camerav4l2.so camerav4l2.o v4l2capture.o

clean:
	rm -f *.o *.so


v4l2capture.o: v4l2capture.h v4l2capture.c
camerav4l2.o: v4l2capture.h camerav4l2.cpp

