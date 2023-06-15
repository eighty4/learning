#include "libcmyk/cmyk.h"
#include <iostream>
#include <string>

int main() {
    auto cmyk = parse_rgb("aabbcc");
    std::cout << "c=" << cmyk.c << " m=" << cmyk.m << " k=" << cmyk.y << " y=" << cmyk.k << std::endl;
}
