#!/bin/sh

export TIMIDITY_CFG=/etc/timidity/freepats.cfg

if [ "$MIDPATH_HOME" = "" ]; then
  MIDPATH_HOME=$(pwd)/..
fi

CVM_HOME=$MIDPATH_HOME/../phoneme/cdc/build/linux-x86-generic

# Modify the next line to change the JVM
JAVA_CMD=$CVM_HOME/bin/cvm

# Set the classpath
CP=$MIDPATH_HOME/dist/midpath.jar:$MIDPATH_HOME/configuration:$MIDPATH_HOME/dist/kxml2-2.3.0.jar:$MIDPATH_HOME/dist/microbackend.jar:$MIDPATH_HOME/dist/jlayerme-cldc.jar:$MIDPATH_HOME/dist/jorbis-cldc.jar:$MIDPATH_HOME/dist/escher-cldc.jar:$MIDPATH_HOME/dist/sdljava-cldc.jar:$MIDPATH_HOME/dist/avetanabt-cldc.jar:$MIDPATH_HOME/dist/jsr172-jaxp.jar:$MIDPATH_HOME/dist/jsr172-jaxrpc.jar:$MIDPATH_HOME/dist/jsr179-location.jar:$MIDPATH_HOME/dist/jsr184-m3g.jar:$MIDPATH_HOME/dist/jsr205-messaging.jar:$MIDPATH_HOME/dist/jsr226-svg-core.jar:$MIDPATH_HOME/dist/jsr226-svg-midp2.jar:$MIDPATH_HOME/dist/jsr226-svg-awt.jar:$MIDPATH_HOME/dist/jsr239-opengles-core.jar:$MIDPATH_HOME/dist/jsr239-opengles-jgl.jar:$MIDPATH_HOME/dist/jsr239-opengles-nio.jar:$MIDPATH_HOME/dist/jgl-cldc.jar:$MIDPATH_HOME/dist/nokia.jar

# Path of the native libraries
JLP=$MIDPATH_HOME/dist

# The MIDlet launcher for J2SE
CLASS=org.thenesis.midpath.main.SuiteManager

$JAVA_CMD -Dsun.boot.library.path=${JLP} -Xbootclasspath/p:${CP} \
-Druntime.platform=PC \
-Xmx10M \
${CLASS}

