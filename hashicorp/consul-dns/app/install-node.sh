#!/bin/bash

node --version > /dev/null 2>&1
if [[ $? == 0 ]]; then
    echo "nodejs already installed"
    exit 0
fi

echo "installing nodejs"
export DEBIAN_FRONTEND=noninteractive
sudo dpkg-reconfigure debconf -f noninteractive -p critical
sudo apt-get -y update
sudo apt-get -y install curl

curl -fsSL https://deb.nodesource.com/setup_lts.x | bash -
sudo apt-get -y install nodejs
