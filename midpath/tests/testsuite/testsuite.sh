#!/bin/bash
# Usage: type ./testsuite.sh --help

# Default commands and library locations
JAVAC_CMD=ecj
JAVA_CMD=java
JAR_CMD=jar
JAR_FLAGS=cvf
FASTJAR_ENABLED=no

DIST_HOME=`pwd`
JAR_DIST_HOME=$DIST_HOME/dist

# The file to use if you do not built it
# yourself or the target name if it is
# build
CLDC_JAR=${JAR_DIST_HOME}/cldc1.1.jar

CLDC_FLAGS="-source 1.3 -target 1.1"

# Defaults
BUILD=no
TEST=no
CLDC_TESTSUITE_ENABLED=yes
JNI_TESTSUITE_ENABLED=yes

# Default include headers location (CC syntax)
JNI_INCLUDE=-I/usr/include/classpath

# Overridable file names and default locations
CLDC_TESTSUITE_JAR=${JAR_DIST_HOME}/testsuite-cldc.jar
JNI_TESTSUITE_JAR=${JAR_DIST_HOME}/testsuite-jni.jar

#==========================================
# You should not change anything below
#==========================================

OPTIONS="\
help,\
\
build,\
test,\
\
disable-cldc-testuite,\
disable-jni-testuite,\
\
with-cldc-jar:,\
with-jni-include:,\
\
with-jar:,\
with-javac:,\
with-java:\
"

TEMP=`getopt -l $OPTIONS -o h -- "$@"`

eval set -- "$TEMP"

while true; do
  case $1 in
    --help ) echo "Usage : $(basename $0) [options...]"
      echo "  --help                    : Show this help"
      echo
      echo "Core features:"
      echo "  --build                   : Build test suites
      echo "  --test                    : Start test suites
      echo "  --disable-cldc-testsuite  : Do not build/test CLDC test suite (default: yes)"
      echo "  --disable-jni-testuite    : Do not build/test JNI test suite (default: yes)"
      echo
      echo "Providable libraries:"
      echo "  --with-cldc-jar           : Location of the CLDC class library"
      echo
      echo "External programs:"
      echo "  --with-jar                : Location and name of the jar tool (default: $JAR_CMD)"
      echo "  --with-javac              : Location and name of the javac tool (default: $JAVAC_CMD)"
      echo "  --with-java               : Location and name of the java tool (default: $JAVA_CMD)"
      echo
      echo "Header file locations:"
      echo "Note: Quoting is neccessary for multiple path elements."
      echo "  --with-jni-include        : Location of the JNI headers (CC syntax) (default: $JNI_INCLUDE)"
      exit 0
      ;;
    --build ) BUILD=yes
      echo "Building test suites enabled"
      shift ;;
    --test ) TEST=yes
      echo "Starting test suites enabled"
      shift ;;
    --disable-cldc-testsuite ) CLDC_TESTSUITE_ENABLED=no
      echo "Compiling CLDC testsuite disabled"
      shift ;;
    --disable-jni-testuite ) JNI_TESTSUITE_ENABLED=no
      echo "Compiling JNI testsuite disabled"
      shift ;;
    --with-cldc-jar )
      CLDC_JAR=$2
      echo "Using CLDC class library at: $CLDC_JAR"
      shift 2 ;;
    --with-jar )
      JAR_CMD=$2
      echo "Using jar command: $JAR_CMD"
      shift 2 ;;
    --with-javac )
      JAVAC_CMD=$2
      echo "Using javac command: $JAVAC_CMD"
      shift 2 ;;
    --with-java )
      JAVA_CMD=$2
      echo "Using java command: $JAVA_CMD"
      shift 2 ;;
    --with-jni-include )
      JNI_INCLUDE=$2
      echo "Using JNI include paths: $JNI_INCLUDE"
      shift 2 ;;
    -- ) shift; break;;
    * ) echo "Unknown argument: $1"; break ;;
  esac
done

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

#------------------------
# Build test suites
#------------------------

if [ $BUILD = yes ]; then

  # Create the dist directory
  if [ ! -d $JAR_DIST_HOME ]; then
    mkdir $JAR_DIST_HOME
  fi

  # Build CLDC test suite
  build_java_res $CLDC_TESTSUITE_ENABLED cldc/java cldc/resources $CLDC_TESTSUITE_JAR

  # Build JNI test suite 
  build_java $JNI_TESTSUITE_ENABLED jni/java $JNI_TESTSUITE_JAR :$CLDC_TESTSUITE_JAR

  # Build native part of the JNI test suite
  build_native $JNI_TESTSUITE_ENABLED jni/native

fi

#------------------------
# Start test suites
#------------------------

if [ $TEST = yes ]; then

  if [ $CLDC_TESTSUITE_ENABLED = yes ]; then
    echo "Starting CLDC test suite..."
    ${JAVA_CMD} -Xbootclasspath/c:$CLDC_JAR:$CLDC_TESTSUITE_JAR org.thenesis.midpath.test.suite.cldc.CLDCTestSuite || exit 1
    # Test against a Java SE VM
    # ${JAVA_CMD} -cp $CLDC_JAR:$CLDC_TESTSUITE_JAR org.thenesis.midpath.test.suite.cldc.CLDCTestSuite || exit 1
  fi
  
  if [ $JNI_TESTSUITE_ENABLED = yes ]; then
    echo "Starting JNI test suite...."
    ${JAVA_CMD} -Xbootclasspath/c:$CLDC_JAR:$CLDC_TESTSUITE_JAR:$JNI_TESTSUITE_JAR org.thenesis.midpath.test.suite.jni.JNITestSuite
  fi

fi
