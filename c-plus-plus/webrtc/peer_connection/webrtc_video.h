#ifndef EIGHTY4_WEB_RTC_VIDEO_H
#define EIGHTY4_WEB_RTC_VIDEO_H

#include "session_connection.h"

#include "webrtc/api/media_stream_track.h"
#include "webrtc/api/video_codecs/video_encoder_factory.h"
#include "webrtc/rtc_base/ref_counted_object.h"

namespace eighty4 {

    class DesktopEncoder : public webrtc::VideoEncoder {
    public:
        DesktopEncoder();

        int32_t InitEncode(
                const webrtc::VideoCodec *codec_settings,
                int32_t number_of_cores,
                size_t max_payload_size) override;

        int32_t RegisterEncodeCompleteCallback(webrtc::EncodedImageCallback *callback) override;

        int32_t Release() override;

        int32_t Encode(
                const webrtc::VideoFrame &frame,
                const std::vector<webrtc::VideoFrameType> *frame_types) override;

        void SetRates(const RateControlParameters &parameters) override;

        EncoderInfo GetEncoderInfo() const override;

//        webrtc::EncodedImageCallback::Result SendEncodedFrame(
//                const WebrtcVideoEncoder::EncodedFrame &frame,
//                base::TimeTicks capture_time,
//                base::TimeTicks encode_started_time,
//                base::TimeTicks encode_finished_time);
    };

    class DesktopEncoderFactory : public webrtc::VideoEncoderFactory {
    public:
        DesktopEncoderFactory();

        std::vector<webrtc::SdpVideoFormat> GetSupportedFormats() const override;

        std::unique_ptr<webrtc::VideoEncoder> CreateVideoEncoder(const webrtc::SdpVideoFormat &format) override;

    private:
        std::vector<webrtc::SdpVideoFormat> supported_formats_;
    };

    class DesktopVideoTrackSource : public rtc::RefCountedObject<webrtc::VideoTrackSourceInterface> {
    public:
        void RegisterObserver(webrtc::ObserverInterface *observer) override;

        void UnregisterObserver(webrtc::ObserverInterface *observer) override;

        SourceState state() const override;

        bool remote() const override;

        bool is_screencast() const override;

        absl::optional<bool> needs_denoising() const override;

        bool GetStats(Stats *stats) override;

        bool SupportsEncodedOutput() const override;

        void GenerateKeyFrame() override;

        void AddEncodedSink(rtc::VideoSinkInterface <webrtc::RecordableEncodedFrame> *sink) override;

        void RemoveEncodedSink(rtc::VideoSinkInterface <webrtc::RecordableEncodedFrame> *sink) override;

        void AddOrUpdateSink(rtc::VideoSinkInterface <webrtc::VideoFrame> *sink,
                             const rtc::VideoSinkWants &wants) override;

        void RemoveSink(rtc::VideoSinkInterface <webrtc::VideoFrame> *sink) override;

    private:
        webrtc::ObserverInterface *observer_;
    };

} // namespace eighty4

#endif // EIGHTY4_WEB_RTC_VIDEO_H
