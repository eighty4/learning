# Bazel

This directory creates a static library with a `cmyk parse_rgb(std::string rgb)` function.
An executable links with the library and prints out computed CMYK values from an RGB string.

## RGB to CMYK

Algorithm reproduced [from here](https://www.rapidtables.com/convert/color/rgb-to-cmyk.html).

## Bazel build

```bash
bazelisk build //rgb_to_cmyk
.\bazel-bin\rgb_to_cmyk\rgb_to_cmyk.exe
```
