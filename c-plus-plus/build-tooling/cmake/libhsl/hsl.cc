#include "hsl.h"
#include <fmt/format.h>
#include <string>

std::string hsl::to_css_fn() {
    return fmt::format("hsl({:.0f}, {:.0f}%, {:.0f}%)", h, s * 100, l * 100);
}

hsl parse_rgb(const std::string &rgb_str) {
    auto r = double(std::stoi(rgb_str.substr(0, 2), nullptr, 16)) / double(255);
    auto g = double(std::stoi(rgb_str.substr(2, 2), nullptr, 16)) / double(255);
    auto b = double(std::stoi(rgb_str.substr(4, 2), nullptr, 16)) / double(255);
    auto M = std::max({r, g, b});
    auto m = std::min({r, g, b});
    auto c = M - m;
    double h = 0;
    double s = 0;
    double l = (M + m) / 2;
    if (c > 0) {
        double shift = 0;
        double segment = 0;
        if (M == r) {
            segment = (g - b) / c;
            if (segment < 0) {
                shift = 6;
            }
        } else if (M == g) {
            segment = (b - r) / c;
            shift = 2;
        } else if (M == b) {
            segment = (r - g) / c;
            shift = 4;
        }
        h = (segment + shift) * 60;
        s = c / (1 - std::abs(2 * l - 1));
    }
    return hsl{.h = h, .s = s, .l = l};
}
