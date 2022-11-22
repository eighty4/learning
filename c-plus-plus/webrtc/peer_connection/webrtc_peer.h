#ifndef EIGHTY4_WEB_RTC_PEER_H
#define EIGHTY4_WEB_RTC_PEER_H

#pragma comment (lib, "msdmo.lib")
#pragma comment (lib, "secur32.lib")
#pragma comment (lib, "dmoguids.lib")
#pragma comment (lib, "wmcodecdspuuid.lib")
//#pragma comment (lib, "iphlpapi.lib")
//#pragma comment (lib, "winmm.lib")
//#pragma comment (lib, "amstrmid.lib")
//#pragma comment (lib, "oleaut32.lib")
//#pragma comment (lib, "avrt.lib")

#include "proxy_socket.h"

#include "webrtc/api/peer_connection_interface.h"

namespace eighty4 {

    class WebRtcPeerSignaling {
    public:
        virtual void InitializeMedia() = 0;

        virtual void OnLocalDescriptionCreated(std::string sdp) = 0;
    };

    class WebRtcPeerConnection : public webrtc::PeerConnectionObserver {
    public:
        explicit WebRtcPeerConnection(WebRtcPeerSignaling *webrtc_signaling);

        ~WebRtcPeerConnection() {};

        void AddIceCandidate(webrtc::IceCandidateInterface *ice_json);

        void OnLocalDescriptionCreated(webrtc::SessionDescriptionInterface *sdp);

        void OnRemoteDescription(std::unique_ptr<webrtc::SessionDescriptionInterface> sdp);

        void OnRemoteDescriptionSet();

    private:
        friend class WebRtcMediaStreaming;

        void OnSignalingChange(webrtc::PeerConnectionInterface::SignalingState new_state) override;

        void OnDataChannel(rtc::scoped_refptr<webrtc::DataChannelInterface> data_channel) override;

        void OnRenegotiationNeeded() override;

        void OnIceGatheringChange(webrtc::PeerConnectionInterface::IceGatheringState new_state) override;

        void OnIceCandidate(const webrtc::IceCandidateInterface *candidate) override;

        void OnIceCandidateError(const std::string &host_candidate, const std::string &url, int error_code,
                                 const std::string &error_text) override;

        void OnIceCandidateError(const std::string &address, int port, const std::string &url, int error_code,
                                 const std::string &error_text) override;

        void OnTrack(rtc::scoped_refptr<webrtc::RtpTransceiverInterface> transceiver) override;

        void OnAddTrack(rtc::scoped_refptr<webrtc::RtpReceiverInterface> receiver,
                        const std::vector<rtc::scoped_refptr<webrtc::MediaStreamInterface>> &streams) override;

        rtc::scoped_refptr<webrtc::PeerConnectionInterface> peer_connection_;

        rtc::scoped_refptr<webrtc::PeerConnectionFactoryInterface> peer_connection_factory_;

        rtc::scoped_refptr<webrtc::SetRemoteDescriptionObserverInterface> set_sdp_obs_;

        WebRtcPeerSignaling *webrtc_signaling_;
    };

    class WebRtcMediaStreaming {
    public:
        explicit WebRtcMediaStreaming(WebRtcPeerConnection *webrtc_peer_);

        void Initialize();

    private:
        WebRtcPeerConnection *webrtc_peer_;
    };

    class WebRtcPeer : WebRtcPeerSignaling {
    public:
        WebRtcPeer(std::shared_ptr<ProxySocket> proxy_socket, std::string user_id);

        ~WebRtcPeer();

        void AddIceCandidate(std::string *ice_json);

        void HandleSdpOffer(std::string *sdp);

        void InitializeMedia() override;

        void OnLocalDescriptionCreated(std::string sdp) override;

    private:
        std::unique_ptr<WebRtcMediaStreaming> media_streaming_;

        std::shared_ptr<ProxySocket> proxy_socket_;

        WebRtcPeerConnection *peer_connection_;

        std::string user_id_;
    };

} // namespace eighty4

#endif // EIGHTY4_WEB_RTC_PEER_H
