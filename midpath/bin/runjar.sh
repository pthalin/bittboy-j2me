#!/bin/sh
cd "$(dirname "$0")"

# Start MIDlet from a valid MIDlet suite jar
./midpath-midlet_launcher.sh -jar $1
