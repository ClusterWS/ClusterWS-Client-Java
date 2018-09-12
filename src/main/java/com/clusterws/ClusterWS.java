package com.clusterws;

import org.java_websocket.WebSocket;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class ClusterWS {
    private Socket mSocket;
    private String mUrl;
    private Emitter mEmitter;
    private boolean mUseBinary;
    private IClusterWSListener mClusterWSListener;
    private MessageHandler mMessageHandler;
    private PingHandler mPingHandler;
    private List<Channel> mChannels;
    private ReconnectionParams mReconnectionParams;
    private static final byte[] PONG = "A".getBytes();

    public ClusterWS(String url) {
        if (url == null) {
            throw new NullPointerException("Url must be provided");
        }
        mUrl = url;
        mChannels = new ArrayList<>();
        mReconnectionParams = new ReconnectionParams(
                false,
                null,
                null,
                null);
        createSocket();
    }

    public ClusterWS setReconnection(Boolean autoReconnect,
                                     Integer reconnectionIntervalMin,
                                     Integer reconnectionIntervalMax,
                                     Integer reconnectionAttempts) {
        mReconnectionParams = new ReconnectionParams(autoReconnect,
                reconnectionIntervalMin,
                reconnectionIntervalMax,
                reconnectionAttempts);
        return this;
    }

    public ClusterWS setClusterWSListener(IClusterWSListener clusterWSListener) {
        mClusterWSListener = clusterWSListener;
        return this;
    }

    public void connect() {
        mSocket.connect();
    }

    public void disconnect(Integer closeCode, String reason) {
        mSocket.close(closeCode == null ? 1000 : closeCode, reason);
    }

    public void on(String event, IEmitterListener listener) {
        mEmitter.addEventListener(event, listener);
    }

    public void send(String event, Object data) {
        send(event, data, "emit");
    }

    public WebSocket.READYSTATE getState() {
        return mSocket.getReadyState();
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

    private void createSocket() {
        mSocket = new Socket(URI.create(mUrl), new ISocketEvents() {
            @Override
            public void onOpen() {
                for (Channel channel :
                        mChannels) {
                    channel.subscribe();
                }
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
                if (mReconnectionParams.isAutoReconnect()
                        && code != 1000
                        && (mReconnectionParams.getReconnectionAttempts() == 0 || mReconnectionParams.getReconnectionsAttempted() < mReconnectionParams.getReconnectionAttempts())) {
                    if (mSocket.getReadyState() == WebSocket.READYSTATE.CLOSED || mSocket.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED || mSocket.getReadyState() == WebSocket.READYSTATE.CLOSING) {
                        mReconnectionParams.incrementReconnectionsAttempted();
                        int randomDelay = ThreadLocalRandom.current().nextInt(1,
                                mReconnectionParams.getReconnectionIntervalMax() -
                                        mReconnectionParams.getReconnectionIntervalMin() +
                                        1);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                connect();
                            }
                        }, randomDelay);
                    }
                }
                mClusterWSListener.onDisconnected(code, reason);
            }

            @Override
            public void onBinaryMessage(ByteBuffer bytes) {
                byte[] arr = new byte[bytes.remaining()];
                bytes.get(arr);
                if (arr.length == 1 && arr[0] == 57) {
                    mPingHandler.setMissedPingToZero();
                    mSocket.send(PONG);
                } else {
                    String message = new String(arr, StandardCharsets.UTF_8);
                    onMessageReceived(message);
                }

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

    private void onMessageReceived(String message) {
        mMessageHandler.messageDecode(ClusterWS.this, message);
    }
}
