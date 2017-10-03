package com.Yegorisa.clusterws;

import com.Yegorisa.clusterws.channel.Channel;
import com.Yegorisa.clusterws.utils.BasicListener;
import com.Yegorisa.clusterws.utils.Emitter;
import com.Yegorisa.clusterws.utils.Message;
import com.neovisionaries.ws.client.*;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    private ArrayList<Channel> mChannels;

    public ClusterWS(@NotNull String url, @NotNull Integer port, @Nullable Boolean autoReconnect, @Nullable Integer reconnectionInterval,@Nullable Integer reconnectionAttempts) {
        mEmitter = new Emitter();
        mOptions = new Options(url, port, autoReconnect, reconnectionInterval, reconnectionAttempts);
        mChannels = new ArrayList<>();
        create();
    }

    private void create() {
        try {
            mWebSocket = new WebSocketFactory().createSocket("ws://" + mOptions.getUrl() + ":" + mOptions.getPort());
            mWebSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    mBasicListener.onConnected(ClusterWS.this);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
                    mBasicListener.onConnectError(ClusterWS.this,cause);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    mBasicListener.onDisconnected(ClusterWS.this,serverCloseFrame, clientCloseFrame, closedByServer);
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
                            String channelName = jsonObject.getJSONArray("m").getString(1);
                            for (Channel channel:
                                 mChannels) {
                                if (channel.getChannelName().equals(channelName)){
                                    channel.onMessage(jsonObject.getJSONArray("m").getString(2));
                                    break;
                                }
                            }
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
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    public void send(String event, Object data, String type) {
        mWebSocket.sendText(Message.messageEncode(event, data, type));
    }

    public void on(String event, Emitter.Listener fn) {
        mEmitter.on(event, fn);
    }

    public void connect(){
        try {
            mWebSocket.connect();
        } catch (WebSocketException e){
            mBasicListener.onConnectError(ClusterWS.this,e);
        }
    }

    public void connectAsynchronous(){
        mWebSocket.connectAsynchronously();
    }


    public void send(String event, Object data) {
        mWebSocket.sendText(Message.messageEncode(event, data, "emit"));
    }

    public Channel subscribe(String channelName){
        for (Channel channel:
             mChannels) {
            if (channel.getChannelName().equals(channelName)){
                return channel;
            }
        }
        Channel newChannel = new Channel(channelName,this);
        mChannels.add(newChannel);
        return newChannel;
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

    public ArrayList<Channel> getChannels(){
        return mChannels;
    }
}
