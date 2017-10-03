package com.Yegorisa.clusterws;

import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

/**
 * Created by Egor on 01.10.2017.
 */
public interface BasicListener {
    void onConnected(ClusterWS webSocket);

    void onDisconnected(ClusterWS webSocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer);

    void onConnectError(ClusterWS webSocket, WebSocketException exception);
}
