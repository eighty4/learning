# MSVC

This directory creates a static library with a `hsv parse_rgb(std::string rgb)` function.
An executable links with the library and prints out computed HSV values from an RGB string.

## RGB to HSV

Algorithm reproduced [from here](https://www.rapidtables.com/convert/color/rgb-to-hsv.html).

## Compile and link steps

```bash
mkdir build
cl.exe /c /EHsc /Fobuild\libhsv.obj -std:c++20 .\libhsv.cc
lib /out:build\libhsv.lib build\*.obj
cl.exe /c /EHsc /Fobuild\rgb_to_hsv.obj -std:c++20 .\rgb_to_hsv.cc
link /ENTRY:main /MACHINE:x64 /OUT:build\rgb_to_hsv.exe build\libhsv.lib build\rgb_to_hsv.obj
```

The final link command fails - looks like it's throwing up on linking the Windows C Runtime Library.

I need to learn more [about it](https://learn.microsoft.com/en-us/cpp/c-runtime-library/crt-library-features?view=msvc-170).
