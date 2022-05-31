#include "webp/encode.h"
#include "fstream"

int main() {
    int height = 5, width = 5;
    int stride = width * 4;
    int data_size = height * width * 4;
    auto data = new uint8_t[data_size];
    int pixel = 0;
    for (int i = 0; i < data_size; i += 4) {
        int color = pixel % 2 == 0 ? 255 : 0;
        data[i] = color;
        data[i + 1] = color;
        data[i + 2] = color;
        data[i + 3] = 255;
        pixel++;
    }

    uint8_t *webp_data = nullptr;
    size_t webp_len = WebPEncodeLosslessBGRA(data, width, height, stride, &webp_data);
    std::ofstream image_file("screenshot.webp");
    for (int i = 0; i < webp_len; i++) {
        image_file << webp_data[i];
    }
    image_file.close();

    delete[] webp_data;
    delete[] data;
}
