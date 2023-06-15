#include "libhsl/hsl.h"
#include <iostream>

int main() {
    auto hsl = parse_rgb("aabbcc");
    std::cout << hsl.to_css_fn() << std::endl;
}
