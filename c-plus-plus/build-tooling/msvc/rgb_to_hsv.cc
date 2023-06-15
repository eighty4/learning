#include <iostream>
#include <format>
#include "libhsv.h"

int main() {
    auto hsv = parse_rgb("961e3c");
    std::cout << std::format("{}h {}s {}v", hsv.h, hsv.s, hsv.v) << std::endl;
    return 0;
}
