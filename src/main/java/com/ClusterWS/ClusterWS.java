package com.ClusterWS;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Logger;

/**
 * Not casual
 * I hope work
 */

public class ClusterWS {
    private static final Logger LOGGER = Logger.getLogger(ClusterWS.class.getName());

    private boolean mIsConnectedAsynchronously;

    private Options mOptions;
    private ClusterWSListener mClusterWSListener;
    private WebSocket mWebSocket;
    private Emitter mEmitter;
    private ArrayList<Channel> mChannels;
    private Message mMessageHandler;
    private boolean testVariable;

    //Ping
    private Timer mPingTimer;
    private int mLost;

    private Reconnection mReconnectionHandler;

    /**
     * test
     *
     * @param url
     * @param port
     */

    public ClusterWS(String url, String port) {
        mOptions = new Options(url, port);
        mEmitter = new Emitter();
        mChannels = new ArrayList<>();
        mMessageHandler = new Message();
        mReconnectionHandler = new Reconnection(null, null, null, null, this);
        create();
    }

    private void create() {
        try {
            mWebSocket = new WebSocketFactory()
                    .createSocket("ws://" + mOptions.getUrl() + ":" + mOptions.getPort())
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                            mReconnectionHandler.onConnected();
                            if (mClusterWSListener != null) {
                                mClusterWSListener.onConnected(ClusterWS.this);
                            }
                        }

                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                            if (mReconnectionHandler.isAutoReconnect() && !mReconnectionHandler.isInReconnectionState()) {
                                mReconnectionHandler.reconnect(ClusterWS.this);
                            }
                            if (mClusterWSListener != null) {
                                mClusterWSListener.onConnectError(ClusterWS.this, exception);
                            }

                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            if (text.equals("#0")) {
                                mLost = 0;
                                send("#1", null, "ping");
                            } else {
                                mMessageHandler.messageDecode(ClusterWS.this, text);
                            }
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                            mLost = 0;
                            if (mPingTimer != null) {
                                mPingTimer.cancel();
                                mPingTimer = new Timer();
                            }

                            if (serverCloseFrame == null) {
                                serverCloseFrame = new WebSocketFrame().setCloseFramePayload(1006, "Unknown");
                            }

                            if (clientCloseFrame == null) {
                                clientCloseFrame = new WebSocketFrame().setCloseFramePayload(1006, "Unknown");
                            }
                            if (mReconnectionHandler.isInReconnectionState()) {
                                return;
                            }

                            if (mReconnectionHandler.isAutoReconnect() && serverCloseFrame.getCloseCode() != 1000 && clientCloseFrame.getCloseCode() != 1000) {
                                mReconnectionHandler.reconnect(ClusterWS.this);
                            }

                            if (mClusterWSListener != null) {
                                mClusterWSListener.onDisconnected(ClusterWS.this, serverCloseFrame, clientCloseFrame, closedByServer);
                            }
                        }

                    });

        } catch (IOException e) {
            LOGGER.severe("Failed to create a socket. Or, HTTP proxy handshake or SSL handshake failed.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The given URI violates RFC 2396. " + e.getMessage());
        }
    }

    public void setReconnection(Boolean autoReconnect, Integer reconnectionIntervalMin, Integer reconnectionIntervalMax, Integer reconnectionAttempts) {
        mReconnectionHandler = new Reconnection(autoReconnect, reconnectionIntervalMin, reconnectionIntervalMax, reconnectionAttempts, this);
    }

    public void connect() {
        try {
            mIsConnectedAsynchronously = false;
            create();
            mWebSocket.connect();
        } catch (WebSocketException e) {
            if (mReconnectionHandler.isAutoReconnect() && !mReconnectionHandler.isInReconnectionState()) {
                mReconnectionHandler.reconnect(ClusterWS.this);
            }
            if (mClusterWSListener != null) {
                mClusterWSListener.onConnectError(ClusterWS.this, e);
            }
        }
    }

    public void connectAsynchronously() {
        mIsConnectedAsynchronously = true;
        create();
        mWebSocket.connectAsynchronously();
    }

    public void setClusterWSListener(ClusterWSListener clusterWSListener) {
        mClusterWSListener = clusterWSListener;
    }

    public void on(String event, EmitterListener listener) {
        mEmitter.on(event, listener);
    }

    public void send(String event, Object data) {
        mWebSocket.sendText(mMessageHandler.messageEncode(event, data, "emit"));
    }

    public Channel subscribe(String channelName) {
        for (Channel channel :
                mChannels) {
            if (channel.getChannelName().equals(channelName)) {
                return channel;
            }
        }
        Channel newChannel = new Channel(channelName, this);
        mChannels.add(newChannel);
        return newChannel;
    }

    public WebSocketState getState() {
        return mWebSocket.getState();
    }

    public ArrayList<Channel> getChannels() {
        return mChannels;
    }

    public Channel getChannelByName(String channelName) {
        for (Channel channel :
                mChannels) {
            if (channel.getChannelName().equals(channelName)) {
                return channel;
            }
        }
        return null;
    }

    public void disconnect(Integer closeCode, String reason) {
        mWebSocket.disconnect(closeCode == null ? 1000 : closeCode, reason);
    }

    void send(String event, Object data, String type) {
        mWebSocket.sendText(mMessageHandler.messageEncode(event, data, type));
    }

    Emitter getEmitter() {
        return mEmitter;
    }

    void setChannels(ArrayList<Channel> channels) {
        mChannels = channels;
    }

    Timer getPingTimer() {
        return mPingTimer;
    }

    int getLost() {
        return mLost;
    }

    void incrementLost() {
        mLost++;
    }

    boolean isConnectedAsynchronously() {
        return mIsConnectedAsynchronously;
    }
}
