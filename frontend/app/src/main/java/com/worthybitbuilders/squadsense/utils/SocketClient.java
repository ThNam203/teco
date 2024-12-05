package com.worthybitbuilders.squadsense.utils;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.activities.CallVideoActivity;
import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.models.IceCandidateModel;
import com.worthybitbuilders.squadsense.models.SdpOfferModel;
import com.worthybitbuilders.squadsense.viewmodels.MessageActivityViewModel;

import org.webrtc.IceCandidate;

import java.net.URISyntaxException;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketClient {
    private static Socket mSocket;
    private static Application application;
    private static String userId;
    public static Socket getInstance() {
        return mSocket;
    }

    /**
     * Initialized in MyApplication or when user logs in
     * there is no need to initialize anymore anywhere
     */
    public static void InitializeIO(Application application, String userId) {
        SocketClient.application = application;
        SocketClient.userId = userId;
        IO.Options options = new IO.Options();
        options.query = "userId=" + userId;
        options.forceNew = true;
        options.reconnection = true;
        options.reconnectionDelay = 2000;
        options.reconnectionDelayMax = 5000;
        try {
            mSocket = IO.socket("http://192.168.1.7:3000/", options);
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on("offerVideoCall", onReceiveOfferVideoCall);
            mSocket.on("newMessageNotify", onReceiveNewMessageNotify);
            if(!mSocket.connected()) {
                mSocket.connect();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Emitter.Listener onReceiveNewMessageNotify = args -> {
        ChatMessage newMessage = new Gson().fromJson(args[0].toString(), ChatMessage.class);
        // if the notification coming is the same as the one user is chatting then we don't need to notify it
        if (!Objects.equals(MessageActivityViewModel.currentChatRoomId, newMessage.getChatRoomId())) {
            NotificationUtil.createNewMessageNotification(application, newMessage.getSender().name, newMessage.getMessage(), newMessage.getChatRoomId());
        }
    };

    public static Emitter.Listener onConnect = args -> Log.d("SOCKETIO", "Socket Connected!");
    private static final Emitter.Listener onConnectError = args -> Log.d("SOCKETIO", "onConnectError");
    private static final Emitter.Listener onDisconnect = args -> Log.d("SOCKETIO", "onDisconnect");
    private static final Emitter.Listener onReceiveOfferVideoCall = args -> {
        SdpOfferModel sdpOfferModel = new Gson().fromJson(args[0].toString(), SdpOfferModel.class);
        Intent callVideoIntent = new Intent(application, CallVideoActivity.class);
        callVideoIntent.putExtra("isCaller", false);
        callVideoIntent.putExtra("callOffer", sdpOfferModel.getSdp());
        callVideoIntent.putExtra("chatRoomId", sdpOfferModel.getChatRoomId());
        callVideoIntent.putExtra("callerId", sdpOfferModel.getCallerId());
        callVideoIntent.putExtra("callerName", sdpOfferModel.getCallerName());
        callVideoIntent.putExtra("callerImagePath", sdpOfferModel.getCallerImagePath());
        callVideoIntent.putExtra("isVideoCall", sdpOfferModel.getIsVideoCall());
        callVideoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(callVideoIntent);
    };
}
