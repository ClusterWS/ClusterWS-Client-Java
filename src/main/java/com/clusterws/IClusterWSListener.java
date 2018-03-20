package com.clusterws;

public interface IClusterWSListener {
    void onConnected();

    void onError(Exception exception);

    void onDisconnected(int code, String reason);
}
