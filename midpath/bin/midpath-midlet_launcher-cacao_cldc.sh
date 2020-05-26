#!/bin/sh

# Adapt the next line to your environment
JAVA_CMD=cacao

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
BCP=$MIDPATH_HOME/dist/cldc1.1.jar:$MIDPATH_HOME/dist/midpath.jar:$MIDPATH_HOME/configuration:$MIDPATH_HOME/dist/kxml2-2.3.0.jar:$MIDPATH_HOME/dist/microbackend.jar:$MIDPATH_HOME/dist/jlayerme-cldc.jar:$MIDPATH_HOME/dist/jorbis-cldc.jar:$MIDPATH_HOME/dist/escher-cldc.jar:$MIDPATH_HOME/dist/sdljava-cldc.jar:$MIDPATH_HOME/dist/avetanabt-cldc.jar:$MIDPATH_HOME/dist/jsr172-jaxp.jar:$MIDPATH_HOME/dist/jsr172-jaxrpc.jar:$MIDPATH_HOME/dist/jsr179-location.jar:$MIDPATH_HOME/dist/jsr184-m3g.jar:$MIDPATH_HOME/dist/jsr205-messaging.jar:$MIDPATH_HOME/dist/jsr226-svg-core.jar:$MIDPATH_HOME/dist/jsr226-svg-midp2.jar:$MIDPATH_HOME/dist/jsr226-svg-awt.jar:$MIDPATH_HOME/dist/jsr239-opengles-core.jar:$MIDPATH_HOME/dist/jsr239-opengles-jgl.jar:$MIDPATH_HOME/dist/jsr239-opengles-nio.jar:$MIDPATH_HOME/dist/jgl-cldc.jar

# Parse the arguments
if [ $1 = "-jar" ]; then
 BCP=$BCP:$2
 ARGS="$1 $2"
else
 BCP=$BCP:$1
 ARGS="$2 $3"
fi

# Path of the native libraries
JLP=$MIDPATH_HOME/dist
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JLP 

CLASS=org.thenesis.midpath.main.MIDletLauncher

# Notes: 
# "-Xbootclasspath/c" argument prepends vm.zip (created at cacao compile time) to the bootclasspath. Use "-Xbootclasspath" instead if you need to override it.
# System properties required by MIDP2 and JSR specs are loaded with the help of the VM
# com.sun.midp.io.backend=CLDC overrides value in the configuration file
$JAVA_CMD -Xbootclasspath/c:${BCP} \
-Djavax.microedition.io.Connector.protocolpath=com.sun.midp.io \
-Dmicroedition.profiles=MIDP-2.0 \
-Dmicroedition.configuration=CLDC-1.1 \
-Dmicroedition.locale=en-US \
-Dmicroedition.platform=j2me \
-Dmicroedition.encoding=ISO8859_1 \
-Dmicroedition.hostname=localhost \
-Dmicroedition.commports= \
-Dmicroedition.jtwi.version=1.0 \
-Dmicroedition.media.version=1.1 \
-Dsupports.mixing=true \
-Dsupports.audio.capture=false \
-Dsupports.video.capture=false \
-Dsupports.recording=false \
-Daudio.encodings="encoding=audio/wav encoding=audio/x-wav encoding=pcm encoding=audio/mp3 encoding=audio/ogg" \
-Dvideo.snapshot.encodings="encoding=png encoding=image/png" \
-Dvideo.encodings= \
-Dstreamable.contents="encoding=audio/wav encoding=audio/x-wav encoding=pcm encoding=audio/mp3 encoding=audio/ogg" \
-Dmicroedition.io.file.FileConnection.version=1.0 \
-DBluetooth.api.version=1.1 \
-Dobex.api.version=1.1 \
-Dbluetooth.l2cap.receiveMTU.max=1024 \
-Dbluetooth.master.switch=true \
-Dbluetooth.connected.devices.max=6 \
-Dbluetooth.sd.trans.max=8 \
-Dbluetooth.sd.attr.retrievable.max=16 \
-Dbluetooth.connected.inquiry=true \
-Dbluetooth.connected.inquiry.scan=true \
-Dbluetooth.connected.page=true \
-Dbluetooth.connected.page.scan=true \
-Dmicroedition.m3g.version=1.1 \
-Dmicroedition.location.version=1.1 \
-Dmicroedition.m2g.version=1.1 \
-Dmicroedition.m2g.svg.version=1.1 \
-Dmicroedition.m2g.svg.baseProfile=tiny \
-Dxml.jaxp.subset.version=1.0 \
-Dxml.rpc.subset.version=1.0 \
-Dwireless.messaging.version=2.0 \
-Dwireless.messaging.sms.smsc=+17815511212 \
-Dwireless.messaging.mms.mmsc=+17815511212 \
-Dfile.separator=/ \
-Dcom.sun.midp.io.backend=CLDC \
${CLASS} ${ARGS}

