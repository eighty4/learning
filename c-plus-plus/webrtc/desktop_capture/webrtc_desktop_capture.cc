#include <fstream>
#include <memory>
#include <thread>
#include "webp/encode.h"
#include "webrtc/modules/desktop_capture/desktop_capture_options.h"

// works on mac, fails on windows
class Callback : public webrtc::DesktopCapturer::Callback {
public:
    bool _capped = false;

    void OnCaptureResult(webrtc::DesktopCapturer::Result result,
                         std::unique_ptr<webrtc::DesktopFrame> frame) override {
        uint8_t *webp_data = nullptr;
        size_t webp_len = WebPEncodeLosslessBGRA(
                frame->data(), frame->size().width(), frame->size().height(), frame->stride(), &webp_data);
        std::ofstream image_file("screenshot.webp");
        for (int i = 0; i < webp_len; i++) {
            image_file << webp_data[i];
        }
        image_file.close();
        delete[] webp_data;
        _capped = true;
    }
};

int main() {
    auto opts = webrtc::DesktopCaptureOptions::CreateDefault();
    auto screen_capturer = webrtc::DesktopCapturer::CreateScreenCapturer(opts);
    auto callback = new Callback();
    screen_capturer->Start(callback);
    screen_capturer->CaptureFrame();

    for (;;) {
        std::this_thread::sleep_for(std::chrono::milliseconds(50));
        if (callback->_capped) {
            break;
        }
    }
}
