package com.Yegorisa.clusterws;

import com.neovisionaries.ws.client.WebSocketState;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Egor on 05.10.2017.
 */
class Reconnection {
    private Boolean mAutoReconnect;
    private Boolean mInReconnectionState;
    private Integer mReconnectionAttempted;
    private Timer mReconnectionTimer;

    Reconnection() {
        mInReconnectionState = false;
        mReconnectionAttempted = 0;
    }

    void onConnected(){
        if (mReconnectionTimer != null) {
            mReconnectionTimer.cancel();
            mReconnectionTimer = null;
        }
        mInReconnectionState = false;
        mReconnectionAttempted = 0;
    }

    Boolean isInReconnectionState() {
        return mInReconnectionState;
    }

    Boolean getAutoReconnect() {
        return mAutoReconnect;
    }

    void setAutoReconnect(Boolean autoReconnect) {
        mAutoReconnect = autoReconnect;
    }

    void reconnect(final ClusterWS clusterWS) {
        mInReconnectionState = true;
        mReconnectionTimer = new Timer();
        mReconnectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (clusterWS.getState() == WebSocketState.CLOSED) {
                    mReconnectionAttempted++;
                    if (clusterWS.getOptions().getReconnectionAttempts() != 0 && mReconnectionAttempted >= clusterWS.getOptions().getReconnectionAttempts()) {
                        cancel();
                        mAutoReconnect = false;
                        mInReconnectionState = false;
                    } else {
                        if (clusterWS.isConnectAsynchronous()) {
                            clusterWS.connectAsynchronous();
                        } else {
                            clusterWS.connect();
                        }
                    }
                }
            }
        }, 0, clusterWS.getOptions().getReconnectionInterval());
    }
}
