#!/bin/sh

url="https://github.com/libgit2/libgit2/archive/refs/tags/v1.6.4.tar.gz"
out="libgit2.tar.gz"
if which curl >/dev/null 2>&1; then
  curl -Ls "$url" -o "$out"
elif which wget >/dev/null 2>&1; then
  wget -q -O "$out" -o /dev/null "$url"
else
  echo "unable to download libgit2"
  exit 1
fi

tar xf libgit2.tar.gz
rm libgit2.tar.gz
mv libgit2-1.6.4 libgit2

triplet=x64-osx
#triple=arm64-osx
vcpkg install --triplet "$triplet"

cd libgit2
cmake -DUSE_SSH=true -DOPENSSL_ROOT_DIR="$VCPKG_ROOT/packages/openssl_$triplet" -DOPENSSL_USE_STATIC_LIBS=true -DCMAKE_BUILD_TYPE=Release -Bout/release .
