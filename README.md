# Bittboy J2ME

## Usage & Device installation

See [INSTALL.txt](INSTALL.txt)

## Building project

Required tools

* Bittboy buildroot uClibc
* Java SE Development Kit 6u45

### Source code

Cloning the repo

```
cd <my-dev-folder>
git clone https://github.com/pthalin/bittboy-j2me.git
```

### Setup Bittboy buildroot uClibc
```
cd /opt
sudo mkdir buildroot-bittboy
sudo chown <USERNAME HERE> buildroot-bittboy
git clone https://github.com/bittboy/buildroot.git ./buildroot-bittboy
cp <my-dev-folder>/bittboy-j2me/buildroot_cfg/config buildroot-bittboy/.config 
cd buildroot-bittboy
make sdk
``` 
Note: You may have to install missing packages if it fails.
(The last step will take quite some time... go take a cup of tea)

### Ubuntu JDK installation
```
Download jdk-6u45-linux-i586.bin file from oracle.com

Make the downloaded bin file executable:
chmod +x jdk-6u45-linux-i586.bin

Extract the bin file;
./jdk-6u45-linux-i586.bin

Create a folder called "jvm" inside /usr/lib if it does not already exist:
sudo mkdir /usr/lib/jvm

Rename and move the extracted folder to the jvm folder:
sudo mv jdk1.6.0_45 jdk1.6.0_45_x86
sudo mv jdk1.6.0_45_x86 /usr/lib/jvm/

To install the Java source run the following commands:
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/jdk1.6.0_45_x86/bin/java 1
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk1.6.0_45_x86/bin/javac 1
sudo update-alternatives --install /usr/bin/javaws javaws /usr/lib/jvm/jdk1.6.0_45_x86/bin/javaws 1
sudo update-alternatives --install /usr/bin/jar jar /usr/lib/jvm/jdk1.6.0_45_x86/bin/jar 1

To make this default java:
 
sudo update-alternatives --config java
sudo update-alternatives --config javac
sudo update-alternatives --config javaws
sudo update-alternatives --config jar

To verify Java has installed correctly run this command.
java -version
```

### Compiling
```
cd <my-dev-folder>/bittboy-j2me/phoneme_advanced_mr2/cdc/build/linux-arm-generic
make
cd <my-dev-folder>/bittboy-j2me/midpath
./build.sh
cd <my-dev-folder>/bittboy-j2me/
./release.sh

This is the output:
<my-dev-folder>/bittboy-j2me/release/bitboy-j2me.zip
```

## Authors
```
Porting to Bittboy: pthalin (github.com/pthalin)
Icon by: MediaDesign (https://www.deviantart.com/mediadesign)
```
