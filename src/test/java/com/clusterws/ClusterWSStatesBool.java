package com.clusterws;

public class ClusterWSStatesBool {
    private boolean mClosed;
    private boolean mClosing;
    private boolean mNotYetConnected;
    private boolean mConnecting;
    private boolean mOpen;

    public ClusterWSStatesBool() {
        mClosed = false;
        mClosing = false;
        mNotYetConnected = false;
        mConnecting = false;
        mOpen = false;
    }

    public boolean isClosed() {
        return mClosed;
    }

    public void setClosed(boolean closed) {
        mClosed = closed;
    }

    public boolean isClosing() {
        return mClosing;
    }

    public void setClosing(boolean closing) {
        mClosing = closing;
    }

    public boolean isNotYetConnected() {
        return mNotYetConnected;
    }

    public void setNotYetConnected(boolean notYetConnected) {
        mNotYetConnected = notYetConnected;
    }

    public boolean isConnecting() {
        return mConnecting;
    }

    public void setConnecting(boolean connecting) {
        mConnecting = connecting;
    }

    public boolean isOpen() {
        return mOpen;
    }

    public void setOpen(boolean open) {
        mOpen = open;
    }
}
