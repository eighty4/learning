#include <cstdint>
#include <string>

const int dib_header_size = 56;
const int ttl_header_size = 70;

void setBmpHeader(uint8_t *bmp, int file_size, int img_data_size, int width, int height) {
    for (int i = 0; i < ttl_header_size; i++) {
        bmp[i] = 0;
    }

    // bmp header bytes 0-13
    // 0h, 2 bytes, id field
    bmp[0] = (unsigned char) ('B');
    bmp[1] = (unsigned char) ('M');
    // 2h, 2d, 4 bytes, bmp file size
    bmp[2] = (unsigned char) file_size;
    bmp[3] = (unsigned char) (file_size >> 8);
    bmp[4] = (unsigned char) (file_size >> 16);
    bmp[5] = (unsigned char) (file_size >> 24);
    // 6h, 6d, 2 bytes, unused
    // 8h, 8d, 2 bytes, unused
    // ah, 10d, 4 bytes, pixel data offset
    bmp[10] = (unsigned char) (ttl_header_size);

    // dib header bytes 14-53
    // eh, 14d, 4 bytes, dib header size
    bmp[14] = (unsigned char) (dib_header_size);
    // 12h, 18d, 4 bytes, pixel width
    bmp[18] = (unsigned char) (width);
    bmp[19] = (unsigned char) (width >> 8);
    bmp[20] = (unsigned char) (width >> 16);
    bmp[21] = (unsigned char) (width >> 24);
    // 16h, 22d, 4 bytes, pixel height
    bmp[22] = (unsigned char) (height);
    bmp[23] = (unsigned char) (height >> 8);
    bmp[24] = (unsigned char) (height >> 16);
    bmp[25] = (unsigned char) (height >> 24);
    // 1ah, 26d, 2 bytes, color plane count
    bmp[26] = (unsigned char) (1);
    // 1ch, 28d, 2 bytes, bits/pixel
    bmp[28] = (unsigned char) (32);
    // 1eh, 30d, 4 bytes, compression method
    bmp[30] = (unsigned char) (3);
    // 22h, 34d, 4 bytes, pixel data size
    bmp[34] = (unsigned char) img_data_size;
    bmp[35] = (unsigned char) (img_data_size >> 8);
    bmp[36] = (unsigned char) (img_data_size >> 16);
    bmp[37] = (unsigned char) (img_data_size >> 24);
    // 26h, 38d, 4 bytes, pixels/meter horizontal
    // 2ah, 42d, 4 bytes, pixels/meter vertical
    // 2eh, 46d, 4 bytes, color palette
    // 32h, 50d, 4 bytes, important colors

    // color table
    // 36h, 54d, 4 bytes, red channel bit mask
    bmp[56] = (unsigned char) (255);
    // 3ah, 58d, 4 bytes, green channel bit mask
    bmp[59] = (unsigned char) (255);
    // 3eh, 62d, 4 bytes, blue channel bit mask
    bmp[62] = (unsigned char) (255);
    // 42h, 66d, 4 bytes, alpha channel bit mask
    bmp[69] = (unsigned char) (255);
}

int encode_bmp(uint8_t **bmp_bytes, const uint8_t *bgra_bytes, int bmp_len, int width, int height) {
    int ttl_file_size = ttl_header_size + bmp_len;
    *bmp_bytes = new uint8_t[ttl_file_size];
    setBmpHeader(*bmp_bytes, ttl_file_size, bmp_len, width, height);
    for (int i = 0; i < bmp_len; i++) {
        (*bmp_bytes)[i + ttl_header_size] = bgra_bytes[i];
    }
    return ttl_file_size;
}

int main() {
    int width = 5;
    int height = 5;
    int stride = width * 4;
    auto bmp_size = stride * height;
    auto bmp_bytes = new uint8_t[bmp_size];
    int pixel = 0;
    for (int i = 0; i < bmp_size; i += 4) {
        if (i < width * 4) {
            bmp_bytes[i] = 0;
            bmp_bytes[i + 1] = 0;
            bmp_bytes[i + 2] = 255;
        } else {
            int color = pixel % 2 == 0 ? 255 : 0;
            bmp_bytes[i] = color;
            bmp_bytes[i + 1] = color;
            bmp_bytes[i + 2] = color;
        }
        bmp_bytes[i + 3] = 255;
        pixel++;
    }

    uint8_t *result;
    int result_len = encode_bmp(&result, bmp_bytes, bmp_size, width, -height);

    auto image_file = fopen("encoded.bmp", "wb");
    fwrite(result, 1, result_len, image_file);
    fclose(image_file);

    delete[] bmp_bytes;

    return 0;
}
