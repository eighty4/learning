# CMake

This directory creates a static library with a `hsl parse_rgb(std::string rgb)` function.
An executable links with the library and prints out a CSS HSL function from an RGB string.

## RGB to HSL

Algorithm reproduced [from here](https://www.rapidtables.com/convert/color/rgb-to-hsl.html).

## CMake build

These instructions are for MSVC on Windows but the code is portable to any other platform.
These commands can be adapted for your platform by modifying the `-DVCPKG_TARGET_TRIPLET` value, which will build the fmt and gtest dependencies for your platform.

```bash
cmake -B build-vcpkg-x64 -S . -DCMAKE_TOOLCHAIN_FILE=C:/Users/adamm/work/lib/vcpkg/scripts/buildsystems/vcpkg.cmake -DVCPKG_TARGET_TRIPLET=x64-windows -DCMAKE_GENERATOR_PLATFORM=x64
cmake --build build-vcpkg-x64
.\build-vcpkg-x64\Debug\rgb_to_hsl.exe
```
