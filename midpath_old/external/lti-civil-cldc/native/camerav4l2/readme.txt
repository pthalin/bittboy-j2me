Autor:  Marcin Rudowski
E-mail: mar_rud@poczta.onet.pl
Opis:   Player module and client libraries for v4l2 compatible capture devices

Directory content:
 - camerav4l2/ - module sources
 - cam-player/ - sources of mini client application using new player module (needed fltk libs)
 - clientlib/ - client libraries sources
 - html/ - doxygen documentation
 - bin/ - comipiled versions of:
    - driver/ - module: camerav4l2.so and example configuration: example.cfg
    - lib/ - client libraries (static and dynamic)
    - include/ - include files
    - gui - example application (needs fltk dynamic libs)

Compile commands:
 - make - Compile all (except docs) and copy to bin/
 - make clean - clean binaries except bin/ directory
 - make doc - generate doxygen documentation in html directory
 - make tar - create archive with sources