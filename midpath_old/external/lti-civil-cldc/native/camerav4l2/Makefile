export CFLAGS_PLAYER=-I/usr/local/include
export C_FLAGS= -Wall  -O2
export L_FLAGS= -Wall  -O2


all:
	mkdir -p bin/driver; mkdir -p bin/lib; mkdir -p bin/include; \
	rm camerav4l2.so gui 2> /dev/null; \
	cp example.cfg bin/driver && \
	cd  camerav4l2/ && \
	make && \
	cp camerav4l2.so ../bin/driver/ && \
	cd ../clientlib/ && \
	make && \
	cp *.a *.so* ../bin/lib/ && \
	cp playerv4l2.h playerv4l2c.h ../bin/include/ && \
	cd ../cam-player/ && \
	make && \
	cp cam-gui ../bin/gui

clean:
	cd  camerav4l2/; \
	make clean; \
	cd ../clientlib; \
	make clean; \
	cd ../cam-player/; \
	make clean; \


doc:
	doxygen Doxyfile >/dev/null && \
	 echo "Success"

tar: clean
	tar -zcf camerav4l2.tgz camerav4l2 clientlib cam-player readme.txt readme.pl.txt example.cfg Makefile Doxyfile;
