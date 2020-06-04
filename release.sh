#!/bin/bash


rm -rf release
mkdir release
mkdir release/bittboy-j2me

mkdir release/bittboy-j2me/jvm
cp -r phoneme_advanced_mr2/cdc/build/linux-arm-generic/bin release/bittboy-j2me/jvm/

cp -r phoneme_advanced_mr2/cdc/build/linux-arm-generic/lib/ release/bittboy-j2me/jvm/


mkdir release/bittboy-j2me/dist
cp -r midpath/dist/  release/bittboy-j2me/

#rename files
SOFILES=release/bittboy-j2me/dist/*.so
for f in $SOFILES
do
  cp $f ${f%%.*}_g.so
done

cp -r midpath/bin/ release/bittboy-j2me/
cp -r midpath/configuration/ release/bittboy-j2me/
