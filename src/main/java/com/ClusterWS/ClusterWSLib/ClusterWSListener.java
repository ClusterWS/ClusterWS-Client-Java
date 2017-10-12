package com.ClusterWS.ClusterWSLib;

import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

/**
 * Created by Egor on 06.10.2017.
 */

public interface ClusterWSListener {
    void onConnected(ClusterWS socket);

    void onConnectError(ClusterWS socket, WebSocketException exception);

    void onDisconnected(ClusterWS socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer);
}
