package com.clusterws;

public class ReconnectionParams {
    private Boolean mAutoReconnect;
    private Integer mReconnectionIntervalMin;
    private Integer mReconnectionIntervalMax;
    private Integer mReconnectionAttempts;
    private Integer mReconnectionsAttempted;

    public ReconnectionParams(Boolean autoReconnect,
                              Integer reconnectionIntervalMin,
                              Integer reconnectionIntervalMax,
                              Integer reconnectionAttempts) {
        mAutoReconnect = autoReconnect != null ? autoReconnect : false;
        mReconnectionIntervalMin = reconnectionIntervalMin != null ? reconnectionIntervalMin : 1000;
        mReconnectionIntervalMax = reconnectionIntervalMax != null ? reconnectionIntervalMax : 5000;
        mReconnectionAttempts = reconnectionAttempts != null ? reconnectionAttempts : 0;
        mReconnectionsAttempted = 0;
    }

    public boolean isAutoReconnect() {
        return mAutoReconnect;
    }

    public Integer getReconnectionIntervalMin() {
        return mReconnectionIntervalMin;
    }

    public Integer getReconnectionIntervalMax() {
        return mReconnectionIntervalMax;
    }

    public Integer getReconnectionAttempts() {
        return mReconnectionAttempts;
    }

    public void incrementReconnectionsAttempted() {
        mReconnectionsAttempted++;
    }

    public void resetReconnectionsAttempted() {
        mReconnectionsAttempted = 0;
    }

    public Integer getReconnectionsAttempted() {
        return mReconnectionsAttempted;
    }
}
