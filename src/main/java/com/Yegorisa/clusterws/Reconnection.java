package com.Yegorisa.clusterws;

import java.util.Timer;

/**
 * Created by Egor on 05.10.2017.
 */
class Reconnection {
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

    Boolean getInReconnectionState() {
        return mInReconnectionState;
    }

    Integer getReconnectionAttempted() {
        return mReconnectionAttempted;
    }

    Timer getReconnectionTimer() {
        return mReconnectionTimer;
    }

    void setInReconnectionState(Boolean inReconnectionState) {
        mInReconnectionState = inReconnectionState;
    }

    void incrementReconnectionAttempted() {
        mReconnectionAttempted++;
    }

    void setReconnectionTimer(Timer reconnectionTimer) {
        mReconnectionTimer = reconnectionTimer;
    }
}
