#include "libhsv.h"
#include <cmath>

hsv parse_rgb(std::string rgb) {
    auto r = double(std::stoi(rgb.substr(0, 2), nullptr, 16)) / double(255);
    auto g = double(std::stoi(rgb.substr(2, 2), nullptr, 16)) / double(255);
    auto b = double(std::stoi(rgb.substr(4, 2), nullptr, 16)) / double(255);
    auto M = std::max({r, g, b});
    auto m = std::min({r, g, b});
    auto d = M - m;
    double h = 0;
    if (d != 0) {
        if (M == r) {
            h = std::fmod(((g - b) / d), 6);
        } else if (M == g) {
            h = ((b - r) / d) + 2;
        } else if (M == b) {
            h = ((r - g) / d) + 4;
        }
        h *= 60;
    }
    double s = 0;
    if (M != 0) {
        s = d / M;
    }
    return hsv{.h = h, .s = s, .v = M};
}
