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

import org.webrtc.IceCandidate;

import java.net.URISyntaxException;

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
            mSocket = IO.socket("http://10.0.140.194:3000/", options);
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on("offerVideoCall", onReceiveOfferVideoCall);
            if(!mSocket.connected()) {
                mSocket.connect();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static Emitter.Listener onConnect = args -> Log.d("SOCKETIO", "Socket Connected!");
    private static final Emitter.Listener onConnectError = args -> Log.d("SOCKETIO", "onConnectError");
    private static final Emitter.Listener onDisconnect = args -> Log.d("SOCKETIO", "onDisconnect");
    private static final Emitter.Listener onReceiveOfferVideoCall = args -> {
        SdpOfferModel sdpOfferModel = new Gson().fromJson(args[0].toString(), SdpOfferModel.class);
        Intent callVideoIntent = new Intent(application, CallVideoActivity.class);
        callVideoIntent.putExtra("isCaller", false);
        callVideoIntent.putExtra("callOffer", sdpOfferModel.getSdp());
        callVideoIntent.putExtra("chatRoomId", sdpOfferModel.getChatRoomId());
        callVideoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(callVideoIntent);
    };
}
