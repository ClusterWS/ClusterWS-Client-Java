package com.clusterws;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

class Socket extends WebSocketClient {
    private ISocketEvents mSocketEvents;

    Socket(URI serverUri, ISocketEvents socketEvents) {
        super(serverUri);
        mSocketEvents = socketEvents;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        mSocketEvents.onOpen();
    }

    @Override
    public void onMessage(String message) {
        mSocketEvents.onMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        mSocketEvents.onClose(code, reason);
    }

    @Override
    public void onError(Exception ex) {
        mSocketEvents.onError(ex);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        mSocketEvents.onBinaryMessage(bytes);
    }
}
