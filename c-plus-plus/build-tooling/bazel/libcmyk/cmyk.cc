#include "cmyk.h"
#include <format>
#include <iostream>

int convert_percent(double v) {
    return (v * 100) + .5;
}

cmyk parse_rgb(std::string rgb) {
    auto r = double(std::stoi(rgb.substr(0, 2), nullptr, 16)) / double(255);
    auto g = double(std::stoi(rgb.substr(2, 2), nullptr, 16)) / double(255);
    auto b = double(std::stoi(rgb.substr(4, 2), nullptr, 16)) / double(255);
    double k = 1 - std::max({r, g, b});
    double c = (1 - r - k) / (1 - k);
    double m = (1 - g - k) / (1 - k);
    double y = (1 - b - k) / (1 - k);
    return cmyk{
        .c = convert_percent(c),
        .m = convert_percent(m),
        .y = convert_percent(y),
        .k = convert_percent(k)
    };
}

std::string cmyk::to_rgb_hex() {
    double r = 255 * (1 - c) * (1 - k);
    double g = 255 * (1 - m) * (1 - k);
    double b = 255 * (1 - y) * (1 - k);
    std::cout << r << std::endl << g << std::endl << b << std::endl;
//    return std::format("{:x}{:x}{:x}", r, g, b);
    return "";
}
