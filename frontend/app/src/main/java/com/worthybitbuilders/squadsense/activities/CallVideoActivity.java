package com.worthybitbuilders.squadsense.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityCallVideoBinding;
import com.worthybitbuilders.squadsense.models.IceCandidateModel;
import com.worthybitbuilders.squadsense.utils.RTCClient;
import com.worthybitbuilders.squadsense.utils.SocketClient;
import com.worthybitbuilders.squadsense.utils.ToastUtils;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SessionDescription;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CallVideoActivity extends AppCompatActivity {
    public static boolean isRunning = false;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private ActivityCallVideoBinding binding;
    // chat room and video call use the same id
    private String chatRoomId;
    private RTCClient rtcClient = null;
    private final Socket socket = SocketClient.getInstance();
    private final Gson gson = new Gson();
    // video call and voice only call
    private boolean isVideoCall;
    private boolean isGroupChat;
    private boolean isCameraOn = true;
    private boolean isCaller;
    private boolean isAudioOn = true;
    private int callTimeCounter = 0;
    private MediaPlayer mediaPlayer;

    // Prepare the MediaPlayer with the media file
    private void prepareMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        try {
            Uri ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ringtone);
            mediaPlayer.setDataSource(CallVideoActivity.this, ringtoneUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = true;
        binding = ActivityCallVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        Intent getIntent = getIntent();
        this.chatRoomId = getIntent.getStringExtra("chatRoomId");
        this.isGroupChat = getIntent.getBooleanExtra("isGroupChat", false);
        // determine if this user init the call
        this.isCaller = getIntent.getBooleanExtra("isCaller", false);
        this.isVideoCall = getIntent.getBooleanExtra("isVideoCall", true);

        if (isCaller) {
            binding.incomingCallUserName.setText(getIntent.getStringExtra("chatRoomTitle"));
            setUpCallLayout();
            socket.on("callDeny", args -> {
                runOnUiThread(() -> {
                    ToastUtils.showToastError(CallVideoActivity.this, "Call denied", Toast.LENGTH_LONG);
                });
                rtcClient.endCall();
                finish();
            });
        } else if (!isGroupChat) {
            // if user is called, we get the call offer (sdp)
            String callOffer = getIntent.getStringExtra("callOffer");
            setIncomingCallUserInfo(getIntent);
            prepareMediaPlayer();
            mediaPlayer.start();

            binding.incomingCallAcceptBtn.setOnClickListener(view -> {
                SessionDescription session = new SessionDescription(
                        SessionDescription.Type.OFFER,
                        callOffer
                );

                rtcClient.onRemoteSessionReceived(session);
                rtcClient.answer();
                runOnUiThread(() -> {
                    setUpCallLayout();
                    binding.remoteViewLoading.setVisibility(View.GONE);
                });

                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            });

            binding.incomingCallDenyBtn.setOnClickListener(view -> {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;

                String callerId = getIntent.getStringExtra("callerId");
                socket.emit("callDeny", callerId);
                finish();
            });
        } else {
            binding.incomingCallAcceptBtn.setOnClickListener(view -> {
                JitsiMeetConferenceOptions options
                        = new JitsiMeetConferenceOptions.Builder()
                        .setRoom("CHATROOM" + chatRoomId)
                        // Settings for audio and video
                        .setAudioMuted(true)
                        .setVideoMuted(true)
                        // When using JaaS, set the obtained JWT here
                        //.setToken("")
                        // Different features flags can be set
                        //.setFeatureFlag("toolbox.enabled", false)
                        //.setFeatureFlag("prejoinpage.enabled", false)
                        .setFeatureFlag("pip.enabled", true)
                        .setFeatureFlag("welcomepage.enabled", false)
                        .build();
                // Launch the new activity with the given options. The launch() method takes care
                // of creating the required Intent and passing the options.

                JitsiMeetActivity.launch(CallVideoActivity.this, options);
                finish();
            });

            binding.incomingCallDenyBtn.setOnClickListener(view -> {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                finish();
            });
        }

        if (!isGroupChat) {
            binding.videoButton.setOnClickListener(view -> {
                if (isCameraOn) {
                    isCameraOn = false;
                    binding.videoButton.setImageResource(R.drawable.ic_videocam_off);
                }else{
                    isCameraOn = true;
                    binding.videoButton.setImageResource(R.drawable.ic_videocam);
                }

                rtcClient.toggleCamera(isCameraOn);
            });

            binding.micButton.setOnClickListener(view -> {
                if (isAudioOn) {
                    isAudioOn = false;
                    rtcClient.toggleAudio(false);
                    binding.micButton.setImageResource(R.drawable.ic_microphone_off);
                } else {
                    isAudioOn = true;
                    rtcClient.toggleAudio(true);
                    binding.micButton.setImageResource(R.drawable.ic_microphone);
                }
            });

            binding.endCallButton.setOnClickListener(view ->{
                rtcClient.endCall();
                finish();
            });

            socket.on("answerOfferVideoCall", onOfferAnswerReceived);
            socket.on("iceCandidate", onReceiveIceCandidate);
            checkPermissionsAndAccessCamera();
        }
    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_CODE)
    private void doWorkIfPermissionsAccepted() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            rtcClient = new RTCClient(getApplication(), chatRoomId, isVideoCall, isGroupChat, peerConnectionObserver);
            if (isVideoCall) {
                rtcClient.initializeSurfaceView(binding.localView);
                rtcClient.initializeSurfaceView(binding.remoteView);
            }
            rtcClient.startLocalVideo(binding.localView);
            if (isCaller) {
                rtcClient.call();
            }
        } else EasyPermissions.requestPermissions(
                this,
                "Please accept to use video call features",
                PERMISSIONS_REQUEST_CODE,
                permissions
        );
    }

    private void checkPermissionsAndAccessCamera() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            doWorkIfPermissionsAccepted();
        } else EasyPermissions.requestPermissions(
                this,
                "Please accept to use video call features",
                PERMISSIONS_REQUEST_CODE,
                permissions
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void setUpCallLayout() {
        if (isVideoCall) {
            binding.videoCallLayout.setVisibility(View.VISIBLE);
            binding.incomingCallLayout.setVisibility(View.GONE);
        }
        else {
            binding.videoButton.setVisibility(View.GONE);
            binding.videoCallLayout.setVisibility(View.GONE);
        }

        binding.incomingControls.setVisibility(View.GONE);
        binding.callControls.setVisibility(View.VISIBLE);
    }

    private void setIncomingCallUserInfo(Intent getIntent) {
        String callerName = getIntent.getStringExtra("callerName");
        String callerImagePath = getIntent.getStringExtra("callerImagePath");
        binding.incomingCallUserName.setText(callerName);
        Glide
            .with(CallVideoActivity.this)
            .load(callerImagePath)
            .placeholder(R.drawable.ic_user)
            .into(binding.incomingUserCallAvatar);
    }

    private final PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
        @Override
        public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
            Log.d("CallVideoActivity", "onConnectionChange" + newState.toString());
            if (newState == PeerConnection.PeerConnectionState.DISCONNECTED) {
                runOnUiThread(() -> {
                    ToastUtils.showToastSuccess(CallVideoActivity.this, "Call ended", Toast.LENGTH_SHORT);
                    finish();
                });
            }
            // TODO: i dont know why but at the time im working, this never got called so i moved to IceConnectionState
//            else if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
//                startCallTimer();
//            }
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d("CallVideoActivity", "onSignalingChange" + signalingState.toString());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d("CallVideoActivity", "onIceConnectionChange" + iceConnectionState.toString());
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                runOnUiThread(() -> {
                    ToastUtils.showToastSuccess(CallVideoActivity.this, "Call ended", Toast.LENGTH_SHORT);
                    finish();
                });
            } else if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
//                startCallTimer();
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.d("CallVideoActivity", "onIceConnectionReceivingChange");
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.d("CallVideoActivity", "onIceGatheringChange" + iceGatheringState.toString());
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            Log.d("CallVideoActivity", "onIceCandidate");
            if (rtcClient != null)
                rtcClient.addIceCandidate(iceCandidate);
            JSONObject candidate = new JSONObject();
            try {
                candidate.put("chatRoomId", chatRoomId);
                candidate.put("sdpMid", iceCandidate.sdpMid);
                candidate.put("sdpMLineIndex", String.valueOf(iceCandidate.sdpMLineIndex));
                candidate.put("sdpCandidate", iceCandidate.sdp);
            } catch (JSONException e) {
                throw new RuntimeException();
            }

            if (rtcClient.isCanSendBufferedICECandidates() || !isCaller) socket.emit("iceCandidate", candidate);
            else rtcClient.getBufferedIceCandidates().add(candidate);
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
            Log.d("CallVideoActivity", "onIceCandidatesRemoved");
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.d("CallVideoActivity", "onAddStream");
            mediaStream.videoTracks.get(0).addSink(binding.remoteView);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d("CallVideoActivity", "onRemoveStream");
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.d("CallVideoActivity", "onDataChannel");
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.d("CallVideoActivity", "onRenegotiationNeeded");
        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
//            Arrays.stream(mediaStreams).forEach(stream -> {
//                stream.videoTracks.get(0).addSink(binding.remoteView);
//            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        socket.off("iceCandidate");
        socket.off("answerOfferVideoCall");
    }

    private final Emitter.Listener onReceiveIceCandidate = args -> {
        Log.d("CallVideoActivity", "onReceiveIceCandidate");
        IceCandidateModel receivingCandidate = gson.fromJson(args[0].toString(), IceCandidateModel.class);
        IceCandidate iceCandidate = new IceCandidate(
                receivingCandidate.getSdpMid(),
                receivingCandidate.getSdpMLineIndex(),
                receivingCandidate.getSdpCandidate()
        );

        if (rtcClient != null) rtcClient.addIceCandidate(iceCandidate);
    };

    private final Emitter.Listener onOfferAnswerReceived = args -> {
        String answerOffer = args[0].toString();
        SessionDescription session = new SessionDescription(SessionDescription.Type.ANSWER, answerOffer);
        rtcClient.onRemoteSessionReceived(session);
        runOnUiThread(() -> binding.remoteViewLoading.setVisibility(View.GONE));
    };

    public void startCallTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    callTimeCounter += 1;
                    int hours = callTimeCounter / 3600;
                    int minutes = (callTimeCounter % 3600) / 60;
                    int seconds = callTimeCounter % 60;

                    if (hours == 0) {
                        binding.callTimer.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
                    } else {
                        binding.callTimer.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
                    }
                });
            }
        }, 0, 1000);
    }
}