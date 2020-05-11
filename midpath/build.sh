#!/bin/bash
# Usage: type ./build.sh --help

# Default commands and library locations
JAVAC_CMD=ecj
JAR_CMD=jar
JAR_FLAGS=cvf
FASTJAR_ENABLED=no
J2SE_JAR=/usr/share/classpath/glibj.zip

DIST_HOME=`pwd`
JAR_DIST_HOME=$DIST_HOME/dist

# The file to use if you do not built it
# yourself or the target name if it is
# build
CLDC_JAR=${JAR_DIST_HOME}/cldc1.1.jar

CLDC_FLAGS="-source 1.3 -target 1.1"

# Defaults
CLDC_ENABLED=yes
MIDPATH_ENABLED=yes

# Default components/libraries
SDLJAVA_CLDC_ENABLED=yes
ESCHER_CLDC_ENABLED=yes
JLAYERME_CLDC_ENABLED=yes
JORBIS_CLDC_ENABLED=yes
AVETANABT_CLDC_ENABLED=yes
JGL_CLDC_ENABLED=yes

# Optional components
WEB_SERVICES_API_ENABLED=yes
LOCATION_API_ENABLED=yes
MESSAGING_API_ENABLED=yes
SVG_API_ENABLED=yes
SVG_API_AWT_ENABLED=no
OPENGL_API_ENABLED=yes
M3G_API_ENABLED=yes

DEMOS_ENABLED=yes

KXML2_DIST_ENABLED=yes

# Overridable file names and default locations
SDLJAVA_CLDC_JAR=${JAR_DIST_HOME}/sdljava-cldc.jar
ESCHER_CLDC_JAR=${JAR_DIST_HOME}/escher-cldc.jar
JLAYERME_CLDC_JAR=${JAR_DIST_HOME}/jlayerme-cldc.jar
JORBIS_CLDC_JAR=${JAR_DIST_HOME}/jorbis-cldc.jar
AVETANABT_CLDC_JAR=${JAR_DIST_HOME}/avetanabt-cldc.jar

JAXP_JAR=$JAR_DIST_HOME/jsr172-jaxp.jar
JAXRPC_JAR=$JAR_DIST_HOME/jsr172-jaxrpc.jar
LOCATION_JAR=$JAR_DIST_HOME/jsr179-location.jar
MESSAGING_JAR=$JAR_DIST_HOME/jsr205-messaging.jar

SVG_CORE_JAR=$JAR_DIST_HOME/jsr226-svg-core.jar
SVG_MIDP2_JAR=$JAR_DIST_HOME/jsr226-svg-midp2.jar
SVG_AWT_JAR=$JAR_DIST_HOME/jsr226-svg-awt.jar

JGL_CLDC_JAR=$JAR_DIST_HOME/jgl-cldc.jar
OPENGLES_CORE_JAR=$JAR_DIST_HOME/jsr239-opengles-core.jar
OPENGLES_JGL_JAR=$JAR_DIST_HOME/jsr239-opengles-jgl.jar
OPENGLES_NIO_JAR=$JAR_DIST_HOME/jsr239-opengles-nio.jar
M3G_JAR=$JAR_DIST_HOME/jsr184-m3g.jar

MICROBACKEND_JAR=${JAR_DIST_HOME}/microbackend.jar

# External library dependencies
# (By default use the included ones.)
KXML2_JAR=`pwd`/lib/kxml2-2.3.0.jar
SWT_JAR=`pwd`/lib/swt.jar

MIDPATH_JAR=$JAR_DIST_HOME/midpath.jar

# Default include headers location (CC syntax)
JNI_INCLUDE=-I/usr/include/classpath
SDL_INCLUDE=-I/usr/include/SDL

# JNI library defaults (those variables must exist and have a non-empty value)
GTK_ENABLED=no
ALSA_ENABLED=no
ESD_ENABLED=no
PULSEAUDIO_ENABLED=no
QT3_ENABLED=no
QT4_ENABLED=no
SDL_ENABLED=no
FB_ENABLED=no
BT_ENABLED=no
NIO_NATIVE_ENABLED=no
FILE_NATIVE_ENABLED=no

#==========================================
# You should not change anything below
#==========================================

OPTIONS="\
help,\
sdl,alsa,esd,pulseaudio,gtk,qt3,qt4,qtopia4,fb,bt,\
\
enable-hildon,\
enable-fastjar,\
enable-cldc-vm,\
\
disable-cldc,\
disable-midpath,\
disable-sdljava-cldc,\
disable-escher-cldc,\
disable-jlayerme-cldc,\
disable-jorbis-cldc,\
disable-avetanabt-cldc,\
disable-jgl-cldc,\
\
disable-web_services-api,\
disable-location-api,\
disable-messaging-api,\
disable-svg-api,\
enable-svg-api-awt,\
disable-opengl-api,\
disable-m3g-api,\
disable-demos,\
\
with-j2se-jar:,\
with-cldc-jar:,\
with-midpath-jar:,\
with-sdljava-cldc-jar:,\
with-escher-cldc-jar:,\
with-jlayerme-cldc-jar:,\
with-jorbis-cldc-jar:,\
with-avetanabt-cldc-jar:,\
with-microbackend-jar:,\
with-jaxp-jar:,\
with-jaxrpc-jar:,\
with-location-jar:,\
with-messaging-jar:,\
with-svg-core-jar:,\
with-svg-midp2-jar:,\
with-svg-awt-jar:,\
with-jgl-cldc-jar:,\
with-opengles-jgl-jar:,\
with-opengles-nio-jar:,\
with-opengles-core-jar:,\
with-m3g-jar:,\
with-kxml2-jar:,\
with-swt-jar:,\
with-greenphone-sdk:,\
with-jdk-home:,\
\
with-jar:,\
with-javac:,\
\
with-jni-include:,\
with-sdl-include:\
"

TEMP=`getopt -l $OPTIONS -o h -- "$@"`

eval set -- "$TEMP"

while true; do
  case $1 in
    --help ) echo "Usage : $(basename $0) [options...]"
      echo " Native libraries:"
      echo "  --sdl       : Compile SDL native code (default: no)"
      echo "  --alsa      : Compile ALSA native code (default: no)"
      echo "  --esd       : Compile ESD native code (default: no)"
      echo "  --pulseaudio: Compile PulseAudio native code (default: no)"
      echo "  --gtk       : Compile GTK native code (default: no)"
      echo "  --qt3       : Compile Qt3 native code (default: no)"
      echo "  --qt4       : Compile Qt4 native code (default: no)"
      echo "  --qtopia4   : Compile Qtopia4 native code (default: no)"
      echo "  --fb        : Compile Linux framebuffer native code (default: no)"
      echo "  --bt        : Compile AvetanaBT native code (default: no)"
      echo
      echo "Misc. build options:"
      echo "  --enable-hildon           : Compile gtk support library with hildon support (default: no)"
      echo "  --enable-fastjar          : Enable use of the fastjar utility (default: no)"
      echo "  --enable-cldc-vm	  : Enable native libraries required to make MIDPath work with a CLDC VM"
      echo "  --help                    : Show this help"
      echo
      echo "Core features:"
      echo "  --disable-cldc            : Do not compile CLDC1.1 classes (default: yes)"
      echo "  --disable-midpath         : Do not compile MIDPath classes (includes microbackend) (default: yes)"
      echo "  --disable-sdljava-cldc    : Do not compile SDLJava-CLDC backend (default: yes)"
      echo "  --disable-escher-cldc     : Do not compile Escher CLDC backend (default: yes)"
      echo "  --disable-jlayerme-cldc   : Do not compile JLayerME CLDC (MP3) library (default: yes)"
      echo "  --disable-jorbis-cldc     : Do not compile JOrbis CLDC (Ogg Vorbis) library (default: yes)"
      echo "  --disable-avetanabt-cldc  : Do not compile AvetanaBT CLDC (Bluetooth) library (default: yes)"
      echo "  --disable-jgl-cldc        : Do not compile jGL CLDC library (default: yes)"
      echo
      echo "Optional features:"
      echo "  --disable-web_services-api: Do not compile the J2ME Web Services API (JSR172) (default: yes)"
      echo "  --disable-location-api    : Do not compile the Location API (JSR179) (default: yes)"
      echo "  --disable-messaging-api   : Do not compile the Wireless Messaging API (JSR205) (default: yes)"
      echo "  --disable-svg-api         : Do not compile the Scalable 2D Vector Graphics API (JSR226) (default: yes)"
      echo "  --enable-svg-api-awt      : Do not compile the SVG API implementation for AWT (default: no)"
      echo "  --disable-opengl-api      : Do not compile the OpenGL ES API (JSR239) (default: yes)"
      echo "  --disable-m3g-api         : Do not compile the Mobile 3D Graphics API (JSR184) (default: yes)"
      echo "  --disable-demos           : Do not compile the MIDPath demos (default: yes)"
      echo
      echo "Providable libraries:"
      echo "  --with-cldc-jar           : Location of the CLDC class library"
      echo "  --with-midpath-jar        : Location of the MIDPath classes"
      echo "  --with-sdljava-cldc-jar   : Location of the SDLJava-CLDC library"
      echo "  --with-escher-cldc-jar    : Location of the Escher-CLDC library"
      echo "  --with-jlayerme-cldc-jar  : Location of the JLayerME-CLDC library"
      echo "  --with-jorbis-cldc-jar    : Location of the JOrbis-CLDC library"
      echo "  --with-avetanabt-cldc-jar : Location of the AvetanaBT-CLDC library"
      echo "  --with-jaxp-jar           : Location of the J2ME Web Services API - JAXP library"
      echo "  --with-jaxrpc-jar         : Location of the J2ME Web Services API - JAXRPC library"
      echo "  --with-location-jar       : Location of the Location API library"
      echo "  --with-messaging-jar      : Location of the Wireless Messaging API library"
      echo "  --with-svg-core-jar       : Location of the SVG core API library"
      echo "  --with-svg-midp2-jar      : Location of the SVG MIDP2 implementation library"
      echo "  --with-svg-awt-jar        : Location of the SVG AWT implementation library"
      echo "  --with-jgl-cldc-jar       : Location of the jGL-CLDC library"
      echo "  --with-opengles-core-jar  : Location of the OpenGL ES core library"
      echo "  --with-opengles-jgl-jar   : Location of the jGL-based OpenGL ES library"
      echo "  --with-opengles-nio-jar   : Location of the OpenGL ES NIO library"
      echo "  --with-m3g-jar            : Location of the JSR184 M3G library"
      echo
      echo "External libraries:"
      echo "  --with-j2se-jar           : Location of the J2SE class library"
      echo "  --with-kxml2-jar          : Location of the kxml2 library (when given disables distribution of the jar)"
      echo "  --with-swt-jar            : Location of the SWT library (default: lib/swt.jar)"
      echo "  --with-greenphone-sdk     : Location of the Greenphone SDK (used to compile the qtopia backend)"
      echo "  --with-jdk-home           : Location of a Sun JDK (used to derive the location of the JNI headers)"
      echo
      echo "External programs:"
      echo "  --with-jar                : Location and name of the jar tool (default: $JAR_CMD)"
      echo "  --with-javac              : Location and name of the javac tool (default: $JAVAC_CMD)"
      echo
      echo "Header file locations:"
      echo "Note: Quoting is neccessary for multiple path elements."
      echo "  --with-jni-include        : Location of the JNI headers (CC syntax) (default: $JNI_INCLUDE)"
      echo "  --with-sdl-include        : Location of the SDL headers (CC syntax) (default: $SDL_INCLUDE)"
      exit 0
      ;;
    --alsa ) ALSA_ENABLED=yes
      echo "ALSA enabled"
      shift ;;
    --sdl ) SDL_ENABLED=yes
      echo "SDL enabled"
      shift ;;
    --esd ) ESD_ENABLED=yes
      echo "ESD enabled"
      shift ;;
    --pulseaudio ) PULSEAUDIO_ENABLED=yes
      echo "PulseAudio enabled"
      shift ;;
    --gtk ) GTK_ENABLED=yes
      echo "GTK enabled"
      shift ;;
    --qt3 ) QT3_ENABLED=yes
      echo "QT3 enabled"
      shift ;;
    --qt4 ) QT4_ENABLED=yes
      echo "QT4 enabled"
      shift ;;
    --qtopia4 ) QTOPIA4_ENABLED=yes
      echo "Qtopia4 enabled"
      shift ;;
    --sdl ) SDL_ENABLED=yes
      echo "SDL enabled"
      shift ;;
    --fb ) FB_ENABLED=yes
      echo "FB enabled"
      shift ;;
    --bt ) BT_ENABLED=yes
      echo "Bluetooth library native compilation enabled"
      shift ;;
    --enable-hildon ) HILDON_ENABLED=yes
      echo "hildon support enabled"
      shift ;;
    --enable-cldc-vm ) NIO_NATIVE_ENABLED=yes
      FILE_NATIVE_ENABLED=yes
      echo "Enable native libraries required for a CLDC VM:"
      echo " -native nio library enabled"
      echo " -native file library enabled"
      shift ;;
    --disable-cldc ) CLDC_ENABLED=no
      echo "compiling CLDC1.1 disabled"
      shift ;;
    --disable-midpath ) MIDPATH_ENABLED=no
      echo "compiling MIDPath (J2ME class library) disabled"
      shift ;;
    --disable-sdljava-cldc ) SDLJAVA_CLDC_ENABLED=no
      echo "compiling SDLJava-CLDC backend disabled"
      shift ;;
    --disable-escher-cldc ) ESCHER_CLDC_ENABLED=no
      echo "compiling Escher CLDC backend disabled"
      shift ;;
    --disable-jlayerme-cldc ) JLAYERME_CLDC_ENABLED=no
      echo "compiling JLayerME CLDC library disabled"
      shift ;;
    --disable-jorbis-cldc ) JORBIS_CLDC_ENABLED=no
      echo "compiling JOrbis CLDC library disabled"
      shift ;;
    --disable-avetanabt-cldc ) AVETANABT_CLDC_ENABLED=no
      echo "compiling AvetanaBT CLDC library disabled"
      shift ;;
    --disable-jgl-cldc ) JGL_CLDC_ENABLED=no
      echo "compiling jGL CLDC library disabled"
      shift ;;
    --disable-web_services-api ) WEB_SERVICES_API_ENABLED=no
      echo "compiling J2ME Web Services API (JSR172) disabled"
      shift ;;
    --disable-location-api ) LOCATION_API_ENABLED=no
      echo "compiling Location API (JSR179) disabled"
      shift ;;
    --disable-messaging-api ) MESSAGING_API_ENABLED=no
      echo "compiling Wireless Messaging API (JSR205) disabled"
      shift ;;
    --disable-svg-api ) SVG_API_ENABLED=no
      echo "compiling Scalable 2D Vector Graphics API (JSR226) disabled"
      shift ;;
    --enable-svg-api-awt ) SVG_API_AWT_ENABLED=yes
      echo "compiling SVG API implementation for AWT (JSR226) enabled"
      shift ;;
    --disable-opengl-api ) OPENGL_API_ENABLED=no
      echo "compiling OpenGL API (JSR239) disabled"
      shift ;;
    --disable-m3g-api ) M3G_API_ENABLED=no
      echo "compiling Mobile 3D Graphics API (JSR184) disabled"
      shift ;;
    --disable-demos ) DEMOS_ENABLED=no
      echo "compiling MIDPath demos disabled"
      shift ;;
    --with-j2se-jar )
      J2SE_JAR=$2
      echo "using J2SE class library at: $J2SE_JAR"
      shift 2 ;;
    --with-cldc-jar )
      CLDC_JAR=$2
      echo "using CLDC class library at: $CLDC_JAR"
      shift 2 ;;
    --with-midpath-jar )
      MIDPATH_JAR=$2
      echo "using MIDPath classes at: $MIDPATH_JAR"
      shift 2 ;;
    --with-sdljava-cldc-jar )
      SDLJAVA_CLDC_JAR=$2
      echo "using SDLJava-CLDC library at: $SDLJAVA_CLDC_JAR"
      shift 2 ;;
    --with-escher-cldc-jar )
      ESCHER_CLDC_JAR=$2
      echo "using Escher-CLDC library at: $ESCHER_CLDC_JAR"
      shift 2 ;;
    --with-jlayerme-cldc-jar )
      JLAYERME_CLDC_JAR=$2
      echo "using JLayerME-CLDC library at: $JLAYERME_CLDC_JAR"
      shift 2 ;;
    --with-jorbis-cldc-jar )
      JORBIS_CLDC_JAR=$2
      echo "using JOrbis-CLDC library at: $JORBIS_CLDC_JAR"
      shift 2 ;;
    --with-avetanabt-cldc-jar )
      AVETANABT_CLDC_JAR=$2
      echo "using AvetanaBT-CLDC library at: $AVETANABT_CLDC_JAR"
      shift 2 ;;
    --with-jaxp-jar )
      JAXP_JAR=$2
      echo "using J2ME Web Services - JAXP library at: $JAXP_JAR"
      shift 2 ;;
    --with-jaxrpc-jar )
      JAXRPC_JAR=$2
      echo "using J2ME Web Services - JAXRPC library at: $JAXRPC_JAR"
      shift 2 ;;
    --with-location-jar )
      LOCATION_JAR=$2
      echo "using Location API library at: $LOCATION_JAR"
      shift 2 ;;
    --with-messaging-jar )
      MESSAGING_JAR=$2
      echo "using Wireless Messaging library at: $MESSAGING_JAR"
      shift 2 ;;
    --with-svg-core-jar )
      SVG_CORE_JAR=$2
      echo "using SVG API core library at: $SVG_CORE_JAR"
      shift 2 ;;
    --with-svg-midp2-jar )
      SVG_MIDP2_JAR=$2
      echo "using SVG MIDP2 implementation library at: $SVG_MIDP2_JAR"
      shift 2 ;;
    --with-svg-awt-jar )
      SVG_AWT_JAR=$2
      echo "using SVG AWT implementation library at: $SVG_AWT_JAR"
      shift 2 ;;
    --with-jgl-cldc-jar )
      JGL_CLDC_JAR=$2
      echo "using jGL-CLDC library at: $JGL_CLDC_JAR"
      shift 2 ;;
    --with-opengles-jgl-jar )
      OPENGLES_JGL_JAR=$2
      echo "using jGL-based OpenGL ES library at: $OPENGLES_JGL_JAR"
      shift 2 ;;
    --with-opengles-core-jar )
      OPENGLES_CORE_JAR=$2
      echo "using OpenGL ES core library at: $OPENGLES_CORE_JAR"
      shift 2 ;;
    --with-opengles-nio-jar )
      OPENGLES_NIO_JAR=$2
      echo "using OpenGL ES NIO library at: $OPENGLES_NIO_JAR"
      shift 2 ;;
    --with-m3g-jar )
      M3G_JAR=$2
      echo "using M3G library at: $M3G_JAR"
      shift 2 ;;
    --with-kxml2-jar )
      # If kxml2 is provided from somewhere else we do not need to
      # copy it for distribution.
      KXML2_DIST_ENABLED=no
      KXML2_JAR=$2
      echo "using kxml2 library at: $KXML2_JAR"
      shift 2 ;;
    --with-swt-jar )
      SWT_JAR=$2
      echo "using SWT library at: $SWT_JAR"
      shift 2 ;;
    --with-greenphone-sdk )
      GREENPHONE_SDK_PATH=$2
      echo "using Greenphone SDK: $GREENPHONE_SDK_PATH"
      shift 2 ;;
    --with-jdk-home )
      JNI_INCLUDE="-I$2/include -I$2/include/linux"
      echo "using JDK. Setting JNI include path to: $JNI_INCLUDE"
      shift 2 ;;
    --enable-fastjar )
      FASTJAR_ENABLED=yes
      # fastjar has a bug that causes multiple META-INF directories to be created. By adding the "-M"
      # option the mmake-made makefiles do not generate this directory any more when using fastjar.
      JAR_FLAGS="-M -cvf"
      echo "using fastjar utility"
      shift ;;
    --with-jar )
      JAR_CMD=$2
      echo "using jar command: $JAR_CMD"
      shift 2 ;;
    --with-javac )
      JAVAC_CMD=$2
      echo "using javac command: $JAVAC_CMD"
      shift 2 ;;
    --with-jni-include )
      JNI_INCLUDE=$2
      echo "using JNI include paths: $JNI_INCLUDE"
      shift 2 ;;
    --with-sdl-include )
      SDL_INCLUDE=$2
      echo "using SDL include paths: $SDL_INCLUDE"
      shift 2 ;;
    -- ) shift; break;;
    * ) echo "Unknown argument: $1"; break ;;
  esac
done

# Create the dist directory
if [ ! -d $JAR_DIST_HOME ]; then
  mkdir $JAR_DIST_HOME
fi

#---------------------
# Build CLDC1.1
#---------------------
if [ $CLDC_ENABLED = "yes" ]
then
  CLDC_CLASSES=`readlink -f external/cldc1.1/classes`

  # Make and install CLDC classes
  make -C external/cldc1.1/src \
    JAVAC=$JAVAC_CMD \
    JAVAC_FLAGS="-bootclasspath . -sourcepath . $CLDC_FLAGS" || exit 1

  make install -C external/cldc1.1/src \
    CLASS_DIR=$CLDC_CLASSES || exit 1

  # Build CLDC extra classes for MIDP2
  make install -C components/cldc-glue \
    JAVAC=$JAVAC_CMD \
    JAVAC_FLAGS="-bootclasspath $CLDC_CLASSES -sourcepath . $CLDC_FLAGS" || exit 1

  # Install CLDC extra classes for MIDP2
  make install -C components/cldc-glue \
    CLASS_DIR=$CLDC_CLASSES || exit 1

  # Make a jar
  $JAR_CMD cvf $CLDC_JAR -C $CLDC_CLASSES . || exit 1

else
  echo "skipped CLDC1.1 build and using pre-built one"
fi

# Builds mmake-managed Java sources and creates a Jar.
#
# $1 - yes/no = whether the build should be done or not
# $2 - source directory
# $3 - target jar file name and location (must be absolute!)
# $4 - auxiliary bootclasspath entries (optional)
#      containing a leading colon (:) character
build_java ()
{
  if [ $1 = yes ]
  then
    local srcdir=$2
    local jarname=$3
    local auxbcp=$4

    make -C $srcdir \
      JAVAC=$JAVAC_CMD \
      JAVAC_FLAGS="-bootclasspath ${CLDC_JAR}$auxbcp -sourcepath . $CLDC_FLAGS" || exit 1

    make jar -C $srcdir \
      JAVAC=$JAVAC_CMD \
      JAVAC_FLAGS="-bootclasspath ${CLDC_JAR}$auxbcp -sourcepath . $CLDC_FLAGS" \
      JAR=$JAR_CMD \
      JAR_FLAGS="$JAR_FLAGS" \
      JAR_FILE="$jarname" || exit 1
  else
    echo "skipping: $2"
  fi
}

# Builds mmake-managed Java sources, creates a Jar and adds resources.
#
# $1 - yes/no = whether the build should be done or not
# $2 - source directory
# $3 - resource directory
# $4 - target jar file name and location (must be absolute!)
# $5 - auxilliary bootclasspath entries (optional)
#      containing a leading colon (:) character
build_java_res()
{
  build_java $1 $2 $4 $5 || exit 1

  if [ $1 = yes ]; then
    local resdir=$3
    local jarname=$4
    
    if [ $FASTJAR_ENABLED = yes ]; then
      # fastjar needs to get the file list via stdin
      ( cd $resdir && find -type f | grep -v "/.svn" | $JAR_CMD uvf $jarname -E -M -@ )
    else
      # Sun's jar has trouble with the first entry when using @ and -C
      echo "ignore_the_error" > resources.list
      # all other jar commands handle the resources via a file
      find $resdir -type f | grep -v "/.svn" >> resources.list
      $JAR_CMD uvf $jarname -C $resdir @resources.list
    fi
  fi
}

#--------------------------
# Build external libraries
#--------------------------
# Build SDLJava library
build_java $SDLJAVA_CLDC_ENABLED external/sdljava-cldc/java $SDLJAVA_CLDC_JAR

# Build Escher library
build_java $ESCHER_CLDC_ENABLED external/escher-cldc/core $ESCHER_CLDC_JAR

# Build JLayerME MP3 library
build_java $JLAYERME_CLDC_ENABLED external/jlayerme-cldc/src $JLAYERME_CLDC_JAR

# Build JOrbis Ogg Vorbis library
build_java $JORBIS_CLDC_ENABLED external/jorbis-cldc/src $JORBIS_CLDC_JAR

# Build AvetanaBT Bluetooth library
build_java $AVETANABT_CLDC_ENABLED external/avetanabt-cldc/src $AVETANABT_CLDC_JAR

# Build jGL library
build_java $JGL_CLDC_ENABLED external/jgl-cldc/src $JGL_CLDC_JAR

# Build MicroBackend library (requires sdljava-cldc, escher-cldc and swt library)
build_java \
  $MIDPATH_ENABLED components/microbackend $MICROBACKEND_JAR \
  :$J2SE_JAR:$SDLJAVA_CLDC_JAR:$ESCHER_CLDC_JAR:$SWT_JAR

#------------------------
# Build MIDPath 
#------------------------

# Build the MIDPath core
build_java_res $MIDPATH_ENABLED components/core/src components/core/resources $MIDPATH_JAR \
  :$J2SE_JAR:$SDLJAVA_CLDC_JAR:$JLAYERME_CLDC_JAR:$JORBIS_CLDC_JAR:$AVETANABT_CLDC_JAR:$MICROBACKEND_JAR:$KXML2_JAR

# Compile separately classes which can't be compiled against cldc.jar
if [ $MIDPATH_ENABLED = yes ]; then
   make -C components/j2se-glue \
     JAVAC=$JAVAC_CMD \
     JAVAC_FLAGS="-bootclasspath $J2SE_JAR:$MIDPATH_JAR -sourcepath . $CLDC_FLAGS" \

   make install -C components/j2se-glue \
     JAVAC=$JAVAC_CMD \
     JAVAC_FLAGS="-bootclasspath $J2SE_JAR:$MIDPATH_JAR -sourcepath . $CLDC_FLAGS" \
     CLASS_DIR="classes" || exit 1
  
  # Add classes to midpath and microbackend jars (which could be used in a Java SE environment)
  ${JAR_CMD} uvf $MIDPATH_JAR -C components/j2se-glue/classes .
  ${JAR_CMD} uvf $MICROBACKEND_JAR -C components/j2se-glue/classes com
fi

if [ $KXML2_DIST_ENABLED = yes ]; then
  # Add other required libraries to the dist directory
  cp $KXML2_JAR dist
fi

# Optional components

# Build J2ME Web Services API (JSR172) - JAXP
build_java $WEB_SERVICES_API_ENABLED \
  components/jsr172-web_services/jaxp \
  $JAXP_JAR \
  :$MIDPATH_JAR

# Build J2ME Web Services API (JSR172) - JAXRPC
build_java $WEB_SERVICES_API_ENABLED \
  components/jsr172-web_services/jaxrpc \
  $JAXRPC_JAR \
  :$MIDPATH_JAR:$JAXP_JAR

# Build Location API (JSR179)
build_java_res $LOCATION_API_ENABLED \
  components/jsr179-location/core \
  components/jsr179-location/resources \
  $LOCATION_JAR \
  :$MIDPATH_JAR:$KXML2_JAR:$AVETANABT_CLDC_JAR

# Build Wireless Messaging API (JSR205)
build_java $MESSAGING_API_ENABLED \
  components/jsr205-messaging/core \
  $MESSAGING_JAR \
  :$MIDPATH_JAR

# Build M2G/SVG (JSR226) core library
build_java_res $SVG_API_ENABLED \
  components/jsr226-svg/core \
  components/jsr226-svg/resources \
  $SVG_CORE_JAR \
  :$MIDPATH_JAR:$JAXP_JAR

# Build M2G/SVG (JSR226) MIDP2 implementation
build_java $SVG_API_ENABLED \
  components/jsr226-svg/midp2 \
  $SVG_MIDP2_JAR \
  :$MIDPATH_JAR:$SVG_CORE_JAR 

# Not fully tested yet.
# Build M2G/SVG (JSR226) AWT implementation
if [ $SVG_API_ENABLED = yes ]; then
  build_java $SVG_API_AWT_ENABLED \
    components/jsr226-svg/awt \
    $SVG_AWT_JAR \
    :$J2SE_JAR:$SVG_CORE_JAR
fi

# Build OpenGL ES (JSR239) core
build_java $OPENGL_API_ENABLED components/jsr239-opengl/core $OPENGLES_CORE_JAR \
  :$MIDPATH_JAR:$J2SE_JAR

# Build OpenGL ES (JSR239) pure Java implementation based on jGL
build_java $OPENGL_API_ENABLED components/jsr239-opengl/implementations/jgl $OPENGLES_JGL_JAR \
  :$MIDPATH_JAR:$J2SE_JAR:$JGL_CLDC_JAR:$OPENGLES_CORE_JAR

# Build OpenGL ES (JSR239) NIO classes (only used with CLDC JVMs)
build_java $OPENGL_API_ENABLED components/jsr239-opengl/nio-cldc $OPENGLES_NIO_JAR \
  :$OPENGLES_CORE_JAR

# Build M3G (JSR184)
build_java $M3G_API_ENABLED components/jsr184-m3g/core $M3G_JAR \
  :$MIDPATH_JAR:$OPENGLES_CORE_JAR:$OPENGLES_NIO_JAR

#-------------------
# Build demos
#-------------------

build_java_res $DEMOS_ENABLED demos/src demos/resources \
  $JAR_DIST_HOME/midpath-demos.jar \
  :$MIDPATH_JAR:$LOCATION_JAR:$MESSAGING_JAR:$SVG_CORE_JAR:$OPENGLES_CORE_JAR:$OPENGLES_NIO_JAR:$M3G_JAR
$JAR_CMD uvmf demos/resources/META-INF/MANIFEST.MF $JAR_DIST_HOME/midpath-demos.jar

#------------------- 
# Build native code
#-------------------

# Builds a JNI library in a subdirectory.
# 
# $1 - yes/no - whether the the item should be build or not.
# $2 - directory of the Makefile
# $3 - quoted makefile arguments (e.g. "FOO=bar BAZ=bla")
build_native()
{
  local dir=$2

  if [ $1 = yes ]; then

    local opts=$3

    make -C $dir \
      JNI_INCLUDE="$JNI_INCLUDE" \
      SDL_INCLUDE="$SDL_INCLUDE" \
      $opts \
    || exit 1

    find $dir -name "*.so" -exec cp \{\} $DIST_HOME/dist \;
  else
    echo "skipping native lib: $dir"
  fi
}

# Build the GTK native part
if [ "$HILDON_ENABLED" = "yes" ]; then
  build_native $GTK_ENABLED native/microbackend/gtk "HILDON=yes"
else
  build_native $GTK_ENABLED native/microbackend/gtk
fi

# Build native file part
build_native $FILE_NATIVE_ENABLED native/file

# Build native nio part
build_native $NIO_NATIVE_ENABLED native/nio

# Build the ALSA native part
build_native $ALSA_ENABLED native/alsa

# Build the ESounD native part
build_native $ESD_ENABLED native/esd

# Build the Pulseaudio native part
build_native $PULSEAUDIO_ENABLED native/pulseaudio

# Build the Qt3 native part
build_native $QT3_ENABLED native/microbackend/qt "QT3=yes"

# Build the Qt4 native part
if [ "$QTOPIA4_ENABLED" = "yes" ]; then
	build_native $QT4_ENABLED native/microbackend/qt "QTOPIA4=yes GREENPHONE_SDK_PATH=$GREENPHONE_SDK_PATH"
else
  build_native $QT4_ENABLED native/microbackend/qt 
fi

# Build the Linux framebuffer native part
build_native $FB_ENABLED native/microbackend/fb

# Build the SDLJava native part
build_native $SDL_ENABLED external/sdljava-cldc/native

# Build the Bluetooth library native part
build_native $BT_ENABLED external/avetanabt-cldc/native
