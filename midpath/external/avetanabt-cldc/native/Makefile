JNI_INCLUDE = -I/usr/include/classpath

CFLAGS = $(JNI_INCLUDE)
LDFLAGS = -lbluetooth 

SOURCES = de_avetana_bluetooth_stack_BlueZ.cpp

OBJECTS = $(patsubst %.cpp, %.o, $(SOURCES))

all: libavetanabtcldc.so

%.o : %.cpp
	$(CC) $(CFLAGS) -fPIC -g -c -Wall -o $(patsubst %.cpp, %.o, $<) $<

%.so: $(OBJECTS)
	$(CC) $(LDFLAGS) -shared -Wl,-soname,$@ -lstdc++ -o $@ $^

clean:
	rm -f *.so *.o 


