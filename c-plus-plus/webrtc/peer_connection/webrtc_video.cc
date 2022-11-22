#include "webrtc_video.h"

#include "iostream"

#include "media/base/vp9_profile.h"

namespace eighty4 {

    DesktopEncoder::DesktopEncoder() = default;

    int32_t DesktopEncoder::InitEncode(const webrtc::VideoCodec *codec_settings, int32_t number_of_cores,
                                       size_t max_payload_size) {
        return 0;
    }

    int32_t DesktopEncoder::RegisterEncodeCompleteCallback(webrtc::EncodedImageCallback *callback) {
        return 0;
    }

    int32_t DesktopEncoder::Release() {
        return 0;
    }

    int32_t DesktopEncoder::Encode(const webrtc::VideoFrame &frame,
                                   const std::vector<webrtc::VideoFrameType> *frame_types) {
        std::cout << std::endl;
        return 0;
    }

    void DesktopEncoder::SetRates(const RateControlParameters &parameters) {

    }

    webrtc::VideoEncoder::EncoderInfo DesktopEncoder::GetEncoderInfo() const {
        EncoderInfo info;
        info.is_hardware_accelerated = true;
        info.has_internal_source = true;
        return info;
    }

    DesktopEncoderFactory::DesktopEncoderFactory() {
        supported_formats_.emplace_back("VP8");
        supported_formats_.emplace_back("VP9");
        supported_formats_.push_back(webrtc::SdpVideoFormat("VP9", {{webrtc::kVP9FmtpProfileId, "1"}}));
    }

    std::vector<webrtc::SdpVideoFormat> DesktopEncoderFactory::GetSupportedFormats() const {
        return supported_formats_;
    }

    std::unique_ptr<webrtc::VideoEncoder>
    DesktopEncoderFactory::CreateVideoEncoder(const webrtc::SdpVideoFormat &format) {
        return std::make_unique<DesktopEncoder>();
    }

    void DesktopVideoTrackSource::RegisterObserver(webrtc::ObserverInterface *observer) {
        observer_ = observer;
    }

    void DesktopVideoTrackSource::UnregisterObserver(webrtc::ObserverInterface *observer) {
        if (observer_ == observer) {
            observer_ = nullptr;
        }
    }

    webrtc::MediaSourceInterface::SourceState DesktopVideoTrackSource::state() const {
        std::cout << "SCVTS." << "state" << std::endl;
        return kLive;
    }

    bool DesktopVideoTrackSource::remote() const {
        std::cout << "SCVTS." << "remote" << std::endl;
        return false;
    }

    bool DesktopVideoTrackSource::is_screencast() const {
        std::cout << "SCVTS." << "is_screencast" << std::endl;
        return true;
    }

    absl::optional<bool> DesktopVideoTrackSource::needs_denoising() const {
        std::cout << "SCVTS." << "needs_denoising" << std::endl;
        return absl::optional<bool>();
    }

    bool DesktopVideoTrackSource::GetStats(Stats *stats) {
        std::cout << "SCVTS." << "GetStats" << std::endl;
        return false;
    }

    bool DesktopVideoTrackSource::SupportsEncodedOutput() const {
        std::cout << "SCVTS." << "SupportsEncodedOutput" << std::endl;
        return false;
    }

    void DesktopVideoTrackSource::GenerateKeyFrame() {
        std::cout << "SCVTS." << "GenerateKeyFrame" << std::endl;
    }

    void DesktopVideoTrackSource::AddEncodedSink(rtc::VideoSinkInterface<webrtc::RecordableEncodedFrame> *sink) {
        std::cout << "SCVTS." << "AddEncodedSink" << std::endl;
    }

    void DesktopVideoTrackSource::RemoveEncodedSink(rtc::VideoSinkInterface<webrtc::RecordableEncodedFrame> *sink) {
        std::cout << "SCVTS." << "RemoveEncodedSink" << std::endl;
    }

    void DesktopVideoTrackSource::AddOrUpdateSink(rtc::VideoSinkInterface<webrtc::VideoFrame> *sink,
                                                  const rtc::VideoSinkWants &wants) {
        std::cout << "SCVTS." << "AddOrUpdateSink" << std::endl;
    }

    void DesktopVideoTrackSource::RemoveSink(rtc::VideoSinkInterface<webrtc::VideoFrame> *sink) {
        std::cout << "SCVTS." << "RemoveSink" << std::endl;
    }

}