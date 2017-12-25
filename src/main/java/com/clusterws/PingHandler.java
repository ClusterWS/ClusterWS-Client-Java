package com.clusterws;

import java.util.Timer;

class PingHandler {
    private Timer mPingTimer;
    private int mMissedPing;

    PingHandler() {
        mPingTimer = new Timer();
        mMissedPing = 0;
    }

    void incrementMissedPing() {
        mMissedPing++;
    }

    void setMissedPingToZero(){
        mMissedPing = 0;
    }

    Timer getPingTimer() {
        return mPingTimer;
    }

    int getMissedPing() {
        return mMissedPing;
    }
}
