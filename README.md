# Bittboy J2ME


## Building project

Required tools

* Bittboy buildroot uclibc
* JAVA 2 SOFTWARE DEVELOPMENT KIT (J2SDK), STANDARD EDITION, VERSION 1.4.2

### Source code

Cloning the repo

```
cd <my-dev-folder>
clone https://github.com/pthalin/bittboy-j2me.git
```


### Setup Bittboy buildroot ulibc
```
cd /opt
sudo mkdir buildroot-bittboy
sudo chown <USERNAME HERE> buildroot-bittboy
git clone https://github.com/bittboy/buildroot.git buildroot-bittboy
cp <my-dev-folder>/bittboy-j2me/buildroot_cfg/config buildroot-bittboy/.config 
cd buildroot-bittboy
make sdk
``` 
Note: You may have to insatall missing packages if it fail.
(The last step will take quite some time... go take cup of tea)

### Ubuntu manual J2SDK installation
```
Download j2sdk-1_4_2_16-linux-i586.bin file from oracle.com

Make the downloaded bin file executable:
chmod +x j2sdk-1_4_2_16-linux-i586.bin

Extract the bin file;
./j2sdk-1_4_2_16-linux-i586.bin

Create a folder called "jvm" inside /usr/lib if it does not already exist:
sudo mkdir /usr/lib/jvm

Move the extracted folder into the newly created jvm folder:
sudo mv j2sdk1.4.2_19 /usr/lib/jvm/

To install the Java source run the following commands:
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/j2sdk1.4.2_19/bin/java 1
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/j2sdk1.4.2_19/bin/javac 1
sudo update-alternatives --install /usr/bin/javaws javaws /usr/lib/jvm/j2sdk1.4.2_19/bin/javaws 1

To make this default java:
 
sudo update-alternatives --config java
sudo update-alternatives --config javac
sudo update-alternatives --config javaws

To verify Java has installed correctly run this command.
java -version
```

### Compiling
```
cd <my-dev-folder>/bittboy-j2me/phoneme_advanced_mr2/cdc/build/
make
cd <my-dev-folder>/bittboy-j2me/midpath
./build.sh
cd <my-dev-folder>/bittboy-j2me/
./release.sh

This is the output:
<my-dev-folder>/bittboy-j2me/release/bitboy-j2me.zip
```

