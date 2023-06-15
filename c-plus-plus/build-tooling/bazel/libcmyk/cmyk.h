#include <string>

struct cmyk {
    int c;
    int m;
    int y;
    int k;

    std::string to_rgb_hex();
};

cmyk parse_rgb(std::string rgb);
