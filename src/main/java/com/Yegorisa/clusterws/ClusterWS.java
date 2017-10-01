package com.Yegorisa.clusterws;

import com.neovisionaries.ws.client.*;
import org.json.JSONObject;
import com.Yegorisa.clusterws.utils.BasicListener;
import com.Yegorisa.clusterws.utils.Emitter;
import com.Yegorisa.clusterws.utils.Message;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Egor on 01.10.2017.
 */
public class ClusterWS {
    private static final Logger LOGGER = Logger.getLogger(ClusterWS.class.getName());

    private Options mOptions;
    private Emitter mEmitter;
    private BasicListener mBasicListener;
    private WebSocket mWebSocket;

    public ClusterWS(String url, Integer port, Boolean autoReconnect, Integer reconnectionInterval, Integer reconnectionAttempts) {
        mEmitter = new Emitter();
        mOptions = new Options(url, port, autoReconnect, reconnectionInterval, reconnectionAttempts);
        create();
    }

    private void create() {
        try {
            mWebSocket = new WebSocketFactory().createSocket("ws://" + mOptions.getUrl() + ":" + mOptions.getPort());
            mWebSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    mBasicListener.onConnected();
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
                    mBasicListener.onConnectError(cause);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    mBasicListener.onDisconnected(serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    if (message.equals("#0")) {
                        send("#1", null, "ping");
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(message);
                    switch (jsonObject.getJSONArray("m").getString(0)) {
                        case "p":
                            break;
                        case "e":
                            mEmitter.emit(jsonObject.getJSONArray("m").getString(1), jsonObject.getJSONArray("m").get(2));
                            break;
                        case "s":
                            switch (jsonObject.getJSONArray("m").getString(1)) {
                                case "c":
                                    break;
                            }

                    }
                }
            });
            mWebSocket.connectAsynchronously();
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    private void send(String event, Object data, String type) {
        mWebSocket.sendText(Message.messageEncode(event, data, type));
    }

    public void on(String event, Emitter.Listener fn) {
        mEmitter.on(event, fn);
    }

    public void connect(){
        try {
            mWebSocket.connect();
        } catch (WebSocketException e){
            mBasicListener.onConnectError(e);
        }
    }

    public void connectAsynchronosly(){
        mWebSocket.connectAsynchronously();
    }


    public void send(String event, Object data) {
        mWebSocket.sendText(Message.messageEncode(event, data, "emit"));
    }

    public void setBasicListener(BasicListener basicListener) {
        mBasicListener = basicListener;
    }

    public WebSocketState getState() {
        return mWebSocket.getState();
    }

    public void disconnect(Integer closeCode, String reason) {
        mWebSocket.disconnect(closeCode == null ? 1000 : closeCode, reason);
    }
}
