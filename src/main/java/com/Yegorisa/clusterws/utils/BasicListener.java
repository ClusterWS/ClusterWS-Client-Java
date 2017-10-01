package com.Yegorisa.clusterws.utils;

import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

/**
 * Created by Egor on 01.10.2017.
 */
public interface BasicListener {
    void onConnected();
    void onDisconnected(WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer);
    void onConnectError(WebSocketException exception);
}
