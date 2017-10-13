package com.ClusterWS;

import com.neovisionaries.ws.client.WebSocketState;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Egor on 07.10.2017.
 */
class Reconnection {
    private final boolean mAutoReconnect;
    private final int mReconnectionIntervalMin;
    private final int mReconnectionIntervalMax;
    private final int mReconnectionAttempts;
    private boolean mInReconnectionState;
    private int mReconnectionsAttempted;
    private Timer mReconnectionTimer;
    private Timer mTimerOff;
    private ClusterWS mSocket;

    Reconnection(Boolean autoReconnect, Integer reconnectionIntervalMin, Integer reconnectionIntervalMax, Integer reconnectionAttempts, ClusterWS socket) {
        mAutoReconnect = autoReconnect != null ? autoReconnect : false;
        mReconnectionIntervalMin = reconnectionIntervalMin != null ? reconnectionIntervalMin : 1000;
        mReconnectionIntervalMax = reconnectionIntervalMax != null ? reconnectionIntervalMax : 5000;
        mReconnectionAttempts = reconnectionAttempts != null ? reconnectionAttempts : 0;
        mSocket = socket;
        mInReconnectionState = false;
        mReconnectionsAttempted = 0;
    }

    void onConnected() {
        if (mReconnectionTimer != null) {
            mReconnectionTimer.cancel();
            mReconnectionTimer = null;
        }
        if (mTimerOff != null) {
            mTimerOff.cancel();
            mTimerOff = null;
        }
        mInReconnectionState = false;
        mReconnectionsAttempted = 0;

        ArrayList<Channel> channels = mSocket.getChannels();
        for (Channel channel :
                channels) {
            channel.subscribe();
        }

    }

    void reconnect(final ClusterWS socket) {
        mInReconnectionState = true;
        mReconnectionTimer = new Timer();
        mReconnectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (socket.getState() == WebSocketState.CLOSED) {
                    mReconnectionsAttempted++;
                    if (mReconnectionAttempts != 0 && mReconnectionsAttempted >= mReconnectionAttempts) {
                        cancel();
                        mInReconnectionState = false;
                    } else {
                        if (mTimerOff != null) {
                            mTimerOff.cancel();
                        }
                        mTimerOff = new Timer();
                        int randomDelay = ThreadLocalRandom.current().nextInt(1, mReconnectionIntervalMax- mReconnectionIntervalMin + 1);
                        mTimerOff.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (socket.isConnectedAsynchronously()) {
                                    socket.connectAsynchronously();
                                } else {
                                    socket.connect();
                                }
                            }
                        }, randomDelay);
                    }
                }
            }
        }, 0, mReconnectionIntervalMin);
    }

    boolean isAutoReconnect() {
        return mAutoReconnect;
    }

    boolean isInReconnectionState() {
        return mInReconnectionState;
    }
}
