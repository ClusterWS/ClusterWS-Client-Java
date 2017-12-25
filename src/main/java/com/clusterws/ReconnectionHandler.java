package com.clusterws;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

class ReconnectionHandler {
    private final boolean mAutoReconnect;
    private final int mReconnectionIntervalMin;
    private final int mReconnectionIntervalMax;
    private final int mReconnectionAttempts;
    private boolean mInReconnectionState;
    private int mReconnectionsAttempted;
    private Timer mReconnectionTimer;
    private Timer mTimerOff;
    private ClusterWS mSocket;

    ReconnectionHandler(Boolean autoReconnect, Integer reconnectionIntervalMin, Integer reconnectionIntervalMax, Integer reconnectionAttempts, ClusterWS socket) {
        mAutoReconnect = autoReconnect != null ? autoReconnect : false;
        mReconnectionIntervalMin = reconnectionIntervalMin != null ? reconnectionIntervalMin : 1000;
        mReconnectionIntervalMax = reconnectionIntervalMax != null ? reconnectionIntervalMax : 5000;
        mReconnectionAttempts = reconnectionAttempts != null ? reconnectionAttempts : 0;
        mSocket = socket;
        mInReconnectionState = false;
        mReconnectionsAttempted = 0;
    }

    void onOpen(){
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
        List<Channel> channels = mSocket.getChannels();

        for (Channel channel :
                channels) {
            channel.subscribe();
        }
    }

    void reconnect(){
        mInReconnectionState = true;
        mReconnectionTimer = new Timer();
        mReconnectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mSocket.getState() == WebSocket.READYSTATE.CLOSED) {
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
                                mSocket.connect();
                            }
                        }, randomDelay);
                    }
                }
            }
        }, 0, mReconnectionIntervalMin);
    }

    boolean isInReconnectionState() {
        return mInReconnectionState;
    }

    boolean isAutoReconnect() {
        return mAutoReconnect;
    }
}
