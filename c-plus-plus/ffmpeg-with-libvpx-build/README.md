## Building ffmpeg for encoding libvpx frames

These instructions will build `ffmpeg` with support for `libvp9` and `libvp8` encoding. The build can be customized with other formats using the `--enable-*` flags on `configure`.

Clone sources into your workspace dir:

```
git clone https://chromium.googlesource.com/webm/libvpx
git clone https://github.com/FFmpeg/FFmpeg.git ffmpeg
```

### Steps for OSX

#### Build libvpx

```
brew install yasm
./configure --target=x86-darwin20-gcc --enable-debug --enable-static --disable-shared --disable-examples --disable-tools --disable-docs --enable-unit-tests=0
make
make install
```

#### Build ffmpeg

```
./configure --disable-everything --enable-libvpx --enable-encoder=libvpx_vp8 --enable-encoder=libvpx_vp9 --enable-muxer=webm
make
make install
```

### Steps for Windows

Install MINGW64 Shell with MSYS2 and the following dependencies:

```
pacman -S make pkgconf diffutils git
```

#### Build libvpx

From the libvpx dir in a mingw64 shell:
```
source ~/x86.sh
./configure --target=x86-win32-vs16 --enable-debug --enable-static --disable-shared
CONFIG_STATIC_MSVCRT=1 make
cp Win32/Debug/vpxmdd.lib Win32/Debug/vpx.lib
```

#### Build ffmpeg

Set an env var for the workspace dir with the `libvpx` and `ffmpeg` directories:

```
FFMPEG_WORKSPACE=$(pwd)
```

From the ffmpeg dir in a mingw64 shell:

```
source ~/x86.sh
INCLUDE="$INCLUDE;$FFMPEG_WORKSPACE\libvpx"
LIB="$LIB;$FFMPEG_WORKSPACE\libvpx\Win32\Debug"
./configure --toolchain=msvc --arch=x86 --target-os=win32 --disable-everything --enable-libvpx --enable-encoder=libvpx_vp8 --enable-encoder=libvpx_vp9 --enable-muxer=webm
make
```
