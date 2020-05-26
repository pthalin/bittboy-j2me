JNI_INCLUDE = -I/usr/include/classpath

ifdef QT3
CFLAGS = `pkg-config --cflags qt-mt` $(JNI_INCLUDE)
LDFLAGS = `pkg-config --libs qt-mt`
DFLAGS = -DQT3_BACKEND
# QT4
else
CFLAGS =  -I/usr/include/qt4 -I/usr/include/qt4/QtGui -I/usr/include/classpath
LDFLAGS =  -lQtGui

# QTOPIA4 settings 
ifdef QTOPIA4
ifdef GREENPHONE_SDK_PATH
CFLAGS = -I$(GREENPHONE_SDK_PATH)/include/qtopia -I$(GREENPHONE_SDK_PATH)/include/qtopiabase \
-I$(GREENPHONE_SDK_PATH)/include -I$(GREENPHONE_SDK_PATH)/qtopiacore/target/include/QtCore \
-I$(GREENPHONE_SDK_PATH)/qtopiacore/target/include -I$(GREENPHONE_SDK_PATH)/target/include \
-I$(GREENPHONE_SDK_PATH)/qtopiacore/target/include/QtGui \
$(JNI_INCLUDE)

LDFLAGS = -L$(GREENPHONE_SDK_PATH)/qtopiacore/target/lib -L$(GREENPHONE_SDK_PATH)/image/lib -lqtopia -lQtGui
endif
DFLAGS = -DQTOPIA4_BACKEND
endif

endif

SOURCES = org_thenesis_microbackend_ui_qt_QTBackend.cpp

OBJECTS = $(patsubst %.cpp, %.o, $(SOURCES))

all: libmicrobackendqt.so

%.o : %.cpp
	$(CXX) $(CFLAGS) $(DFLAGS) -fPIC -g -c -Wall -o $(patsubst %.cpp, %.o, $<) $<

%.so: $(OBJECTS)
	$(CXX) $(LDFLAGS) -shared -Wl,-soname,$@ -lc -o $@ $^

clean:
	rm -f *.so *.o 


