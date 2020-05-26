#!/bin/sh

# Modify the next line to change the JVM
JAVA_CMD=/mnt/emus/bittboy-j2me/jvm/bin/cvm

if [ ! $MIDPATH_HOME ]; then
  MIDPATH_HOME=$(pwd)/..
fi

if [ $# -lt 2 ]; then
 echo "Usage :"
 echo "  $(basename $0) <classpath> <midlet-class> [midlet-name]" 
 echo "  $(basename $0) -jar <jar-file>"
 exit 1
fi

# Set the classpath
CP=$MIDPATH_HOME/dist/midpath.jar:$MIDPATH_HOME/configuration:$MIDPATH_HOME/dist/kxml2-2.3.0.jar:$MIDPATH_HOME/dist/microbackend.jar:$MIDPATH_HOME/dist/jlayerme-cldc.jar:$MIDPATH_HOME/dist/jorbis-cldc.jar:$MIDPATH_HOME/dist/escher-cldc.jar:$MIDPATH_HOME/dist/sdljava-cldc.jar:$MIDPATH_HOME/dist/avetanabt-cldc.jar:$MIDPATH_HOME/dist/jsr172-jaxp.jar:$MIDPATH_HOME/dist/jsr172-jaxrpc.jar:$MIDPATH_HOME/dist/jsr179-location.jar:$MIDPATH_HOME/dist/jsr184-m3g.jar:$MIDPATH_HOME/dist/jsr205-messaging.jar:$MIDPATH_HOME/dist/jsr226-svg-core.jar:$MIDPATH_HOME/dist/jsr226-svg-midp2.jar:$MIDPATH_HOME/dist/jsr239-opengles-core.jar:$MIDPATH_HOME/dist/jsr239-opengles-jgl.jar:$MIDPATH_HOME/dist/jsr239-opengles-nio.jar:$MIDPATH_HOME/dist/jgl-cldc.jar:$MIDPATH_HOME/dist/nokia.jar:$MIDPATH_HOME/dist/cldc1.1.jar


# Parse the arguments
if [ $1 = "-jar" ]; then
 CP=$CP:$2
 ARGS="$1 $2"
else
 CP=$CP:$1
 ARGS="$2 $3"
fi

# Path of the native libraries
JLP=$MIDPATH_HOME/dist

CLASS=org.thenesis.midpath.main.MIDletLauncherSE


$JAVA_CMD -Dsun.boot.library.path=${JLP} -Xbootclasspath/p:${CP} -Xmx10M ${CLASS} ${ARGS} > j2me_std_log.txt 2>j2me_err_log.txt

