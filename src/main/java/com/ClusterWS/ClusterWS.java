package com.clusterws;

import org.java_websocket.WebSocket;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClusterWS {
    private Socket mSocket;
    private Options mOptions;
    private Emitter mEmitter;
    private boolean mUseBinary;
    private IClusterWSListener mClusterWSListener;
    private MessageHandler mMessageHandler;
    private PingHandler mPingHandler;
    private List<Channel> mChannels;
    private ReconnectionHandler mReconnectionHandler;

    public ClusterWS(String url) {
        mOptions = new Options(url);
        mChannels = new ArrayList<>();
        mReconnectionHandler = new ReconnectionHandler(null, null, null, null, this);
        createSocket();
    }

    private void createSocket() {
        mSocket = new Socket(URI.create(mOptions.getUrl()), new ISocketEvents() {
            @Override
            public void onOpen() {
                mReconnectionHandler.onOpen();
            }

            @Override
            public void onError(Exception exception) {
                if (mClusterWSListener != null) {
                    mClusterWSListener.onError(exception);
                }
            }

            @Override
            public void onClose(int code, String reason) {
                if (mPingHandler.getPingTimer() != null) {
                    mPingHandler.getPingTimer().cancel();
                }

                if (mReconnectionHandler.isInReconnectionState()) {
                    return;
                }
                if (mReconnectionHandler.isAutoReconnect() && code != 1000) {
                    mReconnectionHandler.reconnect();
                }

                if (mClusterWSListener != null) {
                    mClusterWSListener.onDisconnected(code, reason);
                }
            }

            @Override
            public void onBinaryMessage(ByteBuffer bytes) {
                String message = StandardCharsets.UTF_8.decode(bytes).toString();
                onMessageReceived(message);
            }

            @Override
            public void onMessage(String message) {
                onMessageReceived(message);
            }
        });
        mUseBinary = false;
        mEmitter = new Emitter();
        mMessageHandler = new MessageHandler();
        mPingHandler = new PingHandler();
    }

    public ClusterWS setReconnection(Boolean autoReconnect, Integer reconnectionIntervalMin, Integer reconnectionIntervalMax, Integer reconnectionAttempts) {
        mReconnectionHandler = new ReconnectionHandler(autoReconnect, reconnectionIntervalMin, reconnectionIntervalMax, reconnectionAttempts, this);
        return this;
    }

    public void connect() {
        createSocket();
        mSocket.connect();
    }


    public ClusterWS setClusterWSListener(IClusterWSListener clusterWSListener) {
        mClusterWSListener = clusterWSListener;
        return this;
    }

    public void on(String event, IEmitterListener listener) {
        mEmitter.addEventListener(event, listener);
    }

    public void send(String event, Object data) {
        if (mUseBinary) {
            mSocket.send(mMessageHandler.messageEncode(event, data, "emit").getBytes());
        } else {
            mSocket.send(mMessageHandler.messageEncode(event, data, "emit"));
        }
    }

    public WebSocket.READYSTATE getState() {
        return mSocket.getReadyState();
    }

    public void disconnect(Integer closeCode, String reason) {
        mSocket.close(closeCode == null ? 1000 : closeCode, reason);
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

    public Channel subscribe(String channelName) {
        for (Channel channel :
                mChannels) {
            if (channel.getChannelName().equals(channelName)) {
                return channel;
            }
        }
        Channel newChannel = new Channel(channelName, this);
        newChannel.subscribe();
        mChannels.add(newChannel);
        return newChannel;
    }

    public List<Channel> getChannels() {
        return mChannels;
    }

    IClusterWSListener getClusterWSListener() {
        return mClusterWSListener;
    }

    Emitter getEmitter() {
        return mEmitter;
    }

    void setUseBinary(boolean useBinary) {
        mUseBinary = useBinary;
    }


    void send(String event, Object data, String type) {
        if (mUseBinary) {
            mSocket.send(mMessageHandler.messageEncode(event, data, type).getBytes());
        } else {
            mSocket.send(mMessageHandler.messageEncode(event, data, type));

        }
    }

    PingHandler getPingHandler() {
        return mPingHandler;
    }

    private void onMessageReceived(String message) {
        if (message.equals("#0")) {
            mPingHandler.setMissedPingToZero();
            send("#1", null, "ping");
        } else {
            mMessageHandler.messageDecode(ClusterWS.this, message);
        }
    }
}
