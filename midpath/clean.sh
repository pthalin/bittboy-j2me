find . -name '*.class' -exec rm -f {} \;
make -C external/sdljava-cldc/native/ clean
make -C native/file/ clean
make -C native/nio/ clean
