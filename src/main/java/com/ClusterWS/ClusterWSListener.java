package com.ClusterWS;

import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

public interface ClusterWSListener {
    void onConnected(ClusterWS socket);

    void onConnectError(ClusterWS socket, WebSocketException exception);

    void onDisconnected(ClusterWS socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer);
}
