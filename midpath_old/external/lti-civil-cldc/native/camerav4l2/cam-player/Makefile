CFLAGS=`fltk-config --cflags` -I../clientlib/ -Wall -Wextra
#LFLAGS=`fltk-config --ldflags` -O2 -Wall -ljpeg -lplayerclient -lplayerv4l2 -L../clientlib/
LFLAGS=`fltk-config --ldflags` -O2 -Wall -ljpeg -L../clientlib/  -lplayerv4l2s   -lplayerclient


SRC=\
capturecam.cxx \
cam-gui.cxx

all: cam-gui

cam-gui: $(SRC) capturecam.h ../clientlib/libplayerv4l2s.a
	$(CXX) $(CFLAGS) -o cam-gui $(SRC) $(LFLAGS)

clean:
	rm -f cam-gui
