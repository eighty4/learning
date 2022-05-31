# Encoding a WebP image with libwebp

See [libwebp_encoding.cc](./libwebp_encoding.cc) for an API example

## Building

Clone sources in your workspace:

```
git clone https://chromium.googlesource.com/webm/libwebp
cd libwebp
```

### Mac

Debug build:
```
mkdir -p out/debug
cd out/debug
cmake -DCMAKE_BUILD_TYPE=Debug ../..
make
```

Release build:
```
mkdir -p out/release
cd out/release
cmake -DCMAKE_BUILD_TYPE=Release ../..
make
```

### Windows

Run build from libwebp dir in a 'Developer Command Prompt' shell.
Use 'x86 Native Tools Command Prompt' or 'x64 Native Tools Command Prompt' to build for respective target cpu
(search for the command prompts from the Start menu).

Debug build:
```
nmake /f Makefile.vc CFG=debug-static RTLIBCFB=static OBJDIR=output
```

Release build:
```
nmake /f Makefile.vc CFG=release-static RTLIBCFB=static OBJDIR=output
```
