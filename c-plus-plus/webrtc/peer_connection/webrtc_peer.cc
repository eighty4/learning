#include "desktop_capture.h"
#include "webrtc_peer.h"
#include "webrtc_video.h"

#include "iostream"
#include "memory"
#include "utility"

#include "picojson/picojson.h"

#include "webrtc/api/create_peerconnection_factory.h"
#include "webrtc/api/audio_codecs/builtin_audio_decoder_factory.h"
#include "webrtc/api/audio_codecs/builtin_audio_encoder_factory.h"
#include "webrtc/api/video_codecs/builtin_video_decoder_factory.h"

namespace eighty4 {

    class WebRtcCreateSdpObserver : public rtc::RefCountedObject<webrtc::CreateSessionDescriptionObserver> {
    public:
        explicit WebRtcCreateSdpObserver(WebRtcPeerConnection *webrtc_peer) {
            webrtc_peer_ = webrtc_peer;
        }

        void OnSuccess(webrtc::SessionDescriptionInterface *desc) override {
            std::cout << "webrtc local sdp created" << std::endl;
            webrtc_peer_->OnLocalDescriptionCreated(desc);
        }

        void OnFailure(webrtc::RTCError error) override {
            std::cout << "webrtc error creating local sdp " << error.message() << std::endl;
            exit(1);
        }

    private:
        class WebRtcPeerConnection *webrtc_peer_;
    };

    class WebRtcSetSdpObserver : public rtc::RefCountedObject<webrtc::SetRemoteDescriptionObserverInterface> {
    public:
        explicit WebRtcSetSdpObserver(class WebRtcPeerConnection *webrtc_peer) {
            webrtc_peer_ = webrtc_peer;
        }

        void OnSetRemoteDescriptionComplete(webrtc::RTCError error) override {
            if (error.ok()) {
                std::cout << "webrtc remote sdp set" << std::endl;
                webrtc_peer_->OnRemoteDescriptionSet();
            } else {
                std::cout << "webrtc error setting remote sdp " << error.message() << std::endl;
                exit(1);
            }
        }

    private:
        class WebRtcPeerConnection *webrtc_peer_;
    };

    WebRtcPeerConnection::WebRtcPeerConnection(WebRtcPeerSignaling *webrtc_signaling)
            : webrtc_signaling_(webrtc_signaling) {
        set_sdp_obs_ = new WebRtcSetSdpObserver(this);
        std::cout << "initializing webrtc peer connection" << std::endl;
//                webrtc::PeerConnectionFactoryDependencies pcf_deps;
//                pcf_deps.network_thread = nullptr;
//                pcf_deps.worker_thread = nullptr;
//                pcf_deps.signaling_thread = nullptr;
//                pcf_deps.task_queue_factory = webrtc::CreateDefaultTaskQueueFactory();
//                pcf_deps.call_factory = webrtc::CreateCallFactory();
//                pcf_deps.event_log_factory = std::make_unique<webrtc::RtcEventLogFactory>(
//                        pcf_deps.task_queue_factory.get());
//                cricket::MediaEngineDependencies me_deps;
//                me_deps.task_queue_factory = me_deps.task_queue_factory;
//                me_deps.adm = nullptr;
//                me_deps.audio_encoder_factory = webrtc::CreateBuiltinAudioEncoderFactory();
//                me_deps.audio_decoder_factory = webrtc::CreateBuiltinAudioDecoderFactory();
//                me_deps.audio_frame_processor = nullptr;
//                me_deps.audio_processing = webrtc::AudioProcessingBuilder().Create();
//                me_deps.audio_mixer = nullptr;
//                me_deps.video_decoder_factory = webrtc::CreateBuiltinVideoDecoderFactory();
//                me_deps.video_encoder_factory = webrtc::CreateBuiltinVideoEncoderFactory();
//                pcf_deps.media_engine = cricket::CreateMediaEngine(std::move(me_deps));
//                peer_connection_factory_ = webrtc::CreateModularPeerConnectionFactory(std::move(pcf_deps));
        auto pc_thread = rtc::Thread::Create();
        pc_thread->Start();
        auto pc_thread_ptr = pc_thread.release();
        peer_connection_factory_ = webrtc::CreatePeerConnectionFactory(
                pc_thread_ptr,
                pc_thread_ptr,
                pc_thread_ptr,
                nullptr,
                webrtc::CreateBuiltinAudioEncoderFactory(),
                webrtc::CreateBuiltinAudioDecoderFactory(),
                std::make_unique<DesktopEncoderFactory>(),
                webrtc::CreateBuiltinVideoDecoderFactory(),
                nullptr,
                nullptr,
                nullptr
        );
        if (!peer_connection_factory_) {
            std::cout << "failed creating webrtc peer connection factory" << std::endl;
            exit(1);
        }
        webrtc::PeerConnectionInterface::RTCConfiguration configuration;
        configuration.sdp_semantics = webrtc::SdpSemantics::kUnifiedPlan;
        webrtc::PeerConnectionInterface::IceServer ice_server;
        ice_server.urls.emplace_back("stun:stun.l.google.com:19302");
        configuration.servers.push_back(std::move(ice_server));
        peer_connection_ = peer_connection_factory_->CreatePeerConnection(
                configuration, nullptr, nullptr, this);
        if (peer_connection_.get()->GetConfiguration().sdp_semantics != webrtc::SdpSemantics::kUnifiedPlan) {
            std::cout << "webrtc sdp semantics plan b" << std::endl;
            exit(1);
        }
    };

    void WebRtcPeerConnection::AddIceCandidate(webrtc::IceCandidateInterface *ice_candidate) {
        if (!peer_connection_->AddIceCandidate(ice_candidate)) {
            std::cout << "failed adding ice candidate" << std::endl;
        }
    }

    void WebRtcPeerConnection::OnLocalDescriptionCreated(webrtc::SessionDescriptionInterface *sdp) {
        std::string sdp_string;
        sdp->ToString(&sdp_string);
        webrtc_signaling_->OnLocalDescriptionCreated(std::move(sdp_string));
    }

    void WebRtcPeerConnection::OnRemoteDescription(std::unique_ptr<webrtc::SessionDescriptionInterface> sdp) {
        peer_connection_->SetRemoteDescription(std::move(sdp), set_sdp_obs_);
    }

    void WebRtcPeerConnection::OnRemoteDescriptionSet() {
        webrtc_signaling_->InitializeMedia();
        webrtc::PeerConnectionInterface::RTCOfferAnswerOptions opts;
        peer_connection_->CreateAnswer(new WebRtcCreateSdpObserver(this), opts);
    }

    void WebRtcPeerConnection::OnSignalingChange(webrtc::PeerConnectionInterface::SignalingState new_state) {
        std::cout << "webrtc peer signaling change " << new_state << std::endl;
    }

    void WebRtcPeerConnection::OnDataChannel(rtc::scoped_refptr<webrtc::DataChannelInterface> data_channel) {
        std::cout << "webrtc peer data channel " << data_channel->label() << ": " << data_channel->state() << std::endl;
    }

    void WebRtcPeerConnection::OnRenegotiationNeeded() {
        std::cout << "webrtc peer renegotiation needed" << std::endl;
    }

    void WebRtcPeerConnection::OnIceGatheringChange(webrtc::PeerConnectionInterface::IceGatheringState new_state) {
        std::cout << "webrtc peer ice gathering change " << new_state << std::endl;
    }

    void WebRtcPeerConnection::OnIceCandidate(const webrtc::IceCandidateInterface *candidate) {
        std::cout << "webrtc peer ice candidate " << candidate->server_url() << std::endl;
    }

    void WebRtcPeerConnection::OnIceCandidateError(const std::string &host_candidate, const std::string &url,
                                                   int error_code, const std::string &error_text) {
        std::cout << "webrtc peer ice candidate error " << host_candidate << " " << url << " " << error_text
                  << std::endl;
    }

    void WebRtcPeerConnection::OnIceCandidateError(const std::string &address, int port, const std::string &url,
                                                   int error_code, const std::string &error_text) {
        std::cout << "webrtc peer ice candidate error " << address << ":" << port << " " << url << " " << error_text
                  << std::endl;
    }

    void WebRtcPeerConnection::OnTrack(rtc::scoped_refptr<webrtc::RtpTransceiverInterface> transceiver) {
        std::cout << "webrtc track media type " << transceiver->media_type()
                  << " id " << transceiver->receiver()->id() << std::endl;
    }

    void WebRtcPeerConnection::OnAddTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver,
                                          const std::vector<rtc::scoped_refptr<webrtc::MediaStreamInterface>> &streams) {
        std::cout << "webrtc add track media type " << receiver->media_type() << " id " << receiver->id() << std::endl;
    }

    WebRtcMediaStreaming::WebRtcMediaStreaming(WebRtcPeerConnection *webrtc_peer_) : webrtc_peer_(webrtc_peer_) {};

    void WebRtcMediaStreaming::Initialize() {
        auto video_track = webrtc_peer_->peer_connection_factory_->CreateVideoTrack(
                "s0-track", new DesktopVideoTrackSource());
        webrtc::RtpTransceiverInit transceiver_init;
        transceiver_init.direction = webrtc::RtpTransceiverDirection::kSendOnly;
        transceiver_init.stream_ids.emplace_back("s0-stream");
        auto result = webrtc_peer_->peer_connection_->AddTransceiver(video_track, transceiver_init);
        if (result.ok()) {
            result.value();
        } else {
            std::cout << "error adding transceiver " << result.error().message() << std::endl;
            exit(1);
        }
    }

    WebRtcPeer::WebRtcPeer(std::shared_ptr<ProxySocket> proxy_socket, std::string user_id)
            : proxy_socket_(std::move(proxy_socket)),
              user_id_(std::move(user_id)) {
        peer_connection_ = new WebRtcPeerConnection(this);
        media_streaming_ = std::make_unique<WebRtcMediaStreaming>(peer_connection_);
    };

    WebRtcPeer::~WebRtcPeer() {
        std::cout << "webrtc peer destructor" << std::endl;
        delete peer_connection_;
    }

    void WebRtcPeer::AddIceCandidate(std::string *ice_json) {
        std::cout << "ice candidate: " << *ice_json << std::endl;
        picojson::value ice_properties;
        std::string error = picojson::parse(ice_properties, *ice_json);
        if (!error.empty()) {
            std::cerr << "ice candidate json error " << error << std::endl;
            exit(1);
        }
        if (!ice_properties.is<picojson::object>()) {
            std::cerr << "ice candidate json was not an object" << std::endl;
            exit(1);
        }
        auto sdp_mid = ice_properties.get("sdpMid").get<std::string>();
        auto sdp_mline_index = ice_properties.get("sdpMLineIndex").get<double>();
        auto candidate = ice_properties.get("candidate").get<std::string>();
        auto ice_candidate = webrtc::CreateIceCandidate(sdp_mid, sdp_mline_index, candidate, nullptr);
        peer_connection_->AddIceCandidate(ice_candidate);
    }

    void WebRtcPeer::InitializeMedia() {
        media_streaming_->Initialize();
    }

    void WebRtcPeer::HandleSdpOffer(std::string *sdp_msg) {
        std::cout << "webrtc remote sdp offer received" << std::endl;
        webrtc::SdpParseError error;
        auto sdp = webrtc::CreateSessionDescription(webrtc::SdpType::kOffer, *sdp_msg, &error);
        if (!sdp) {
            std::cout << "webrtc peer error parsing sdp " << error.line << " " << error.description << std::endl;
            exit(1);
        } else {
            peer_connection_->OnRemoteDescription(std::move(sdp));
        }
    }

    void WebRtcPeer::OnLocalDescriptionCreated(std::string sdp) {
        proxy_socket_->Send("X" + user_id_ + "22" + sdp);
    }
}
