package com.worthybitbuilders.squadsense.utils;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketUtil {
    private static Socket mSocket;

    public static Socket getInstance() {
        return mSocket;
    }

    /**
     * Initialized in MyApplication or when user logs in
     * there is no need to initialize anymore anywhere
     */
    public static void InitializeIO(String userId) {
        IO.Options options = new IO.Options();
        options.query = "userId=" + userId;
        options.forceNew = true;
        options.reconnection = true;
        options.reconnectionDelay = 2000;
        options.reconnectionDelayMax = 5000;
        try {
            mSocket = IO.socket("http://10.0.140.194:3000/", options);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            if(!mSocket.connected()){
                mSocket.connect();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static Emitter.Listener onConnect = args -> Log.d("SOCKETIO", "Socket Connected!");
    private static final Emitter.Listener onConnectError = args -> Log.d("SOCKETIO", "onConnectError");
    private static final Emitter.Listener onDisconnect = args -> Log.d("SOCKETIO", "onDisconnect");
}
