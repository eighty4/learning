#include <string>

struct hsv {
    double h;
    double s;
    double v;
};

hsv parse_rgb(std::string rgb);
