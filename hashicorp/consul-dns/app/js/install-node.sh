#!/bin/sh
export DEBIAN_FRONTEND=noninteractive
sudo dpkg-reconfigure debconf -f noninteractive -p critical
sudo apt-get -y update
sudo apt-get -y install curl
curl -fsSL https://deb.nodesource.com/setup_lts.x | bash -
sudo apt-get -y install nodejs
