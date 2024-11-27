//package com.worthybitbuilders.squadsense.utils;
//
//import android.app.Application;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.webrtc.AudioSource;
//import org.webrtc.AudioTrack;
//import org.webrtc.Camera2Enumerator;
//import org.webrtc.CameraVideoCapturer;
//import org.webrtc.DefaultVideoDecoderFactory;
//import org.webrtc.DefaultVideoEncoderFactory;
//import org.webrtc.EglBase;
//import org.webrtc.IceCandidate;
//import org.webrtc.MediaConstraints;
//import org.webrtc.MediaStream;
//import org.webrtc.PeerConnection;
//import org.webrtc.PeerConnectionFactory;
//import org.webrtc.SdpObserver;
//import org.webrtc.SessionDescription;
//import org.webrtc.SurfaceTextureHelper;
//import org.webrtc.SurfaceViewRenderer;
//import org.webrtc.VideoSource;
//import org.webrtc.VideoTrack;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.socket.client.Socket;
//
//public class RTCClientForGroup {
//    private final Application application;
//    // chatroom and video call use the same chat room id
//    private final String chatRoomId;
//    private final Socket socket = SocketClient.getInstance();
//    private final PeerConnection.Observer observer;
//    private EglBase.Context eglContext;
//    private PeerConnectionFactory peerConnectionFactory;
//    private List<PeerConnection.IceServer> iceServer;
//    private List<PeerConnection> peerConnection;
//    private VideoSource localVideoSource;
//    private AudioSource localAudioSource;
//    private CameraVideoCapturer videoCapturer;
//    private AudioTrack localAudioTrack;
//    private VideoTrack localVideoTrack;
//    // buffer the candidates and only send if caller has created an answer
//    private final List<JSONObject> bufferedIceCandidates = new ArrayList<>();
//    // can send buffered ice candidates
//    private boolean canSendBufferedICECandidates = false;
//
//    public RTCClientForGroup(Application application, String chatRoomId, PeerConnection.Observer observer) {
//        this.application = application;
//        this.chatRoomId = chatRoomId;
//        this.observer = observer;
//        init();
//    }
//
//    private void init() {
//        eglContext = EglBase.create().getEglBaseContext();
//        initPeerConnectionFactory(application);
//        iceServer = createIceServers();
//        peerConnection = createPeerConnection(observer);
//        localVideoSource = peerConnectionFactory.createVideoSource(false);
//        localAudioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
//    }
//
//    private void initPeerConnectionFactory(Application application) {
//        PeerConnectionFactory.InitializationOptions.Builder initializationOptionsBuilder =
//                PeerConnectionFactory.InitializationOptions.builder(application)
//                        .setEnableInternalTracer(true)
//                        .setFieldTrials("WebRTC-H264HighProfile/Enabled/");
//
//        PeerConnectionFactory.initialize(initializationOptionsBuilder.createInitializationOptions());
//
//        PeerConnectionFactory.Builder builder = PeerConnectionFactory.builder()
//                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(eglContext, true, true))
//                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(eglContext));
//
//        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
//        options.disableEncryption = true;
//        options.disableNetworkMonitor = true;
//        peerConnectionFactory = builder.setOptions(options).createPeerConnectionFactory();
//    }
//
//    private List<PeerConnection.IceServer> createIceServers() {
//        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
//        iceServers.add(PeerConnection.IceServer.builder("stun:iphone-stun.strato-iphone.de:3478").createIceServer());
//        iceServers.add(new PeerConnection.IceServer("stun:openrelay.metered.ca:80"));
//        iceServers.add(new PeerConnection.IceServer("turn:openrelay.metered.ca:80", "openrelayproject", "openrelayproject"));
//        iceServers.add(new PeerConnection.IceServer("turn:openrelay.metered.ca:443", "openrelayproject", "openrelayproject"));
//        iceServers.add(new PeerConnection.IceServer("turn:openrelay.metered.ca:443?transport=tcp", "openrelayproject", "openrelayproject"));
//        return iceServers;
//    }
//
//    private PeerConnection createPeerConnection(PeerConnection.Observer observer) {
//        return peerConnectionFactory.createPeerConnection(iceServer, observer);
//    }
//
//    public void initializeSurfaceView(SurfaceViewRenderer surface) {
//        surface.setEnableHardwareScaler(true);
//        surface.setMirror(true);
//        surface.init(eglContext, null);
//    }
//
//    public void startLocalVideo(SurfaceViewRenderer surface) {
//        SurfaceTextureHelper surfaceTextureHelper =
//                SurfaceTextureHelper.create(Thread.currentThread().getName(), eglContext);
//        videoCapturer = getVideoCapturer(application);
//        videoCapturer.initialize(surfaceTextureHelper, surface.getContext(), localVideoSource.getCapturerObserver());
//        videoCapturer.startCapture(320, 240, 30);
//        localVideoTrack = peerConnectionFactory.createVideoTrack("local_track", localVideoSource);
//        localVideoTrack.addSink(surface);
//        localAudioTrack = peerConnectionFactory.createAudioTrack("local_track_audio", localAudioSource);
//        MediaStream localStream = peerConnectionFactory.createLocalMediaStream("local_stream");
//        localStream.addTrack(localAudioTrack);
//        localStream.addTrack(localVideoTrack);
//        peerConnection.addStream(localStream);
//    }
//
////    private VideoCapturer createVideoCapturer() {
////        CameraEnumerator enumerator = new Camera2Enumerator(this);
////        final String[] deviceNames = enumerator.getDeviceNames();
////
////        // First, try to find front facing camera
////        for (String deviceName : deviceNames) {
////            if (enumerator.isFrontFacing(deviceName)) {
////                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
////                if (videoCapturer != null) return videoCapturer;
////            }
////        }
////
////        // Front facing camera not found, try something else
////        for (String deviceName : deviceNames) {
////            if (!enumerator.isFrontFacing(deviceName)) {
////                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
////                if (videoCapturer != null) return videoCapturer;
////            }
////        }
////
////        return null;
////    }
//
//    private CameraVideoCapturer getVideoCapturer(Application application) {
//        Camera2Enumerator enumerator = new Camera2Enumerator(application);
//        String[] deviceNames = enumerator.getDeviceNames();
//        for (String deviceName : deviceNames) {
//            if (enumerator.isFrontFacing(deviceName)) {
//                return enumerator.createCapturer(deviceName, null);
//            }
//        }
//        throw new IllegalStateException("No front-facing camera found.");
//    }
//
//    public void call() {
//        MediaConstraints mediaConstraints = new MediaConstraints();
//        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
//        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
//
//        peerConnection.createOffer(new SdpObserver() {
//            @Override
//            public void onCreateSuccess(SessionDescription sessionDescription) {
//                peerConnection.setLocalDescription(new SdpObserver() {
//                    @Override
//                    public void onCreateSuccess(SessionDescription sessionDescription) {
//
//                    }
//
//                    @Override
//                    public void onSetSuccess() {
//                        JSONObject offer = new JSONObject();
//                        try {
//                            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
//                            offer.put("callerId", userId);
//                            offer.put("chatRoomId", chatRoomId);
//                            offer.put("sdp", sessionDescription.description);
//                            offer.put("type", sessionDescription.type.canonicalForm());
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                        socket.emit("offerVideoCall", offer);
//                    }
//
//                    @Override
//                    public void onCreateFailure(String s) {
//
//                    }
//
//                    @Override
//                    public void onSetFailure(String s) {
//
//                    }
//                }, sessionDescription);
//            }
//
//            @Override
//            public void onSetSuccess() {
//
//            }
//
//            @Override
//            public void onCreateFailure(String s) {
//
//            }
//
//            @Override
//            public void onSetFailure(String s) {
//
//            }
//        }, mediaConstraints);
//    }
//
//    public void onRemoteSessionReceived(SessionDescription session) {
//        peerConnection.setRemoteDescription(new SdpObserver() {
//            @Override
//            public void onCreateSuccess(SessionDescription sessionDescription) {}
//
//            @Override
//            public void onSetSuccess() {
//                canSendBufferedICECandidates = true;
//                for (int i = 0; i < bufferedIceCandidates.size(); i++) {
//                    socket.emit("iceCandidate", bufferedIceCandidates.get(i));
//                }
//
//                bufferedIceCandidates.clear();
//            }
//
//            @Override
//            public void onCreateFailure(String s) {}
//
//            @Override
//            public void onSetFailure(String s) {}
//        }, session);
//    }
//
//    public void answer() {
//        MediaConstraints constraints = new MediaConstraints();
//        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
//        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
//
//        peerConnection.createAnswer(new SdpObserver() {
//            @Override
//            public void onCreateSuccess(SessionDescription sessionDescription) {
//                peerConnection.setLocalDescription(new SdpObserver() {
//                    @Override
//                    public void onCreateSuccess(SessionDescription sessionDescription) {
//
//                    }
//
//                    @Override
//                    public void onSetSuccess() {
//                        JSONObject answer = new JSONObject();
//                        try {
//                            answer.put("chatRoomId", chatRoomId);
//                            answer.put("sdp", sessionDescription.description);
//                            answer.put("type", sessionDescription.type.canonicalForm());
//                        } catch (JSONException e) { throw new RuntimeException(e); }
//
//                        socket.emit("answerOfferVideoCall", answer);
//                    }
//
//                    @Override
//                    public void onCreateFailure(String s) {
//
//                    }
//
//                    @Override
//                    public void onSetFailure(String s) {
//
//                    }
//                }, sessionDescription);
//            }
//
//            @Override
//            public void onSetSuccess() {
//
//            }
//
//            @Override
//            public void onCreateFailure(String s) {
//
//            }
//
//            @Override
//            public void onSetFailure(String s) {
//
//            }
//        }, constraints);
//    }
//
//    public void addIceCandidate(IceCandidate candidate) {
//        peerConnection.addIceCandidate(candidate);
//    }
//
//    public void switchCamera() {
//        videoCapturer.switchCamera(null);
//    }
//
//    public void toggleAudio(boolean mute) {
//        localAudioTrack.setEnabled(mute);
//    }
//
//    public void toggleCamera(boolean cameraPause) {
//        localVideoTrack.setEnabled(cameraPause);
//    }
//
//    public void endCall() {
//        try {
//            videoCapturer.stopCapture();
//        } catch (InterruptedException ignored) {}
//        peerConnection.close();
//    }
//}
//
