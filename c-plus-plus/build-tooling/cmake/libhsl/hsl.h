#include <string>

struct hsl {
    double h;
    double s;
    double l;

    std::string to_css_fn();
};

hsl parse_rgb(const std::string &rgb);
