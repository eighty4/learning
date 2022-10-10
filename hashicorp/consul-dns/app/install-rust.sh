#!/bin/bash

rustup --version > /dev/null 2>&1
if [[ $? == 0 ]]; then
    echo "rust already installed"
    exit 0
fi

echo "installing rust"
export DEBIAN_FRONTEND=noninteractive
sudo dpkg-reconfigure debconf -f noninteractive -p critical
sudo apt-get -y update
sudo apt-get -y install curl

# curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
sudo -u vagrant -i bash -c 'curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y'

echo "PATH=\"\$PATH:/home/vagrant/.cargo/bin\"" >> /home/vagrant/.profile
