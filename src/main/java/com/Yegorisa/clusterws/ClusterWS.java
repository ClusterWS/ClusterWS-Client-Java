package com.Yegorisa.clusterws;

import com.neovisionaries.ws.client.*;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * ClusterWS.
 * <p>
 * <h3>Create ClusterWS</h3>
 *
 * @author Egor Egorov
 */

public class ClusterWS {
    private static final Logger LOGGER = Logger.getLogger(ClusterWS.class.getName());

    private Options mOptions;
    private Emitter mEmitter;
    private BasicListener mBasicListener;
    private WebSocket mWebSocket;
    private ArrayList<Channel> mChannels;

    /**
     * <p></p>
     * Returns new {@code ClusterWS} instance.
     * <p>
     *
     * @param url                  the URI of the WebSocket endpoint on the server side. It must be provided.
     * @param port                 the port of the WebSocket endpoint on the server side. Must be provided.
     * @param autoReconnect        if you want to auto reconnect {@code true}, and {@code false} if you do not. Default false.
     * @param reconnectionInterval the number of milliseconds between each reconnection attempt. Default 5000 milliseconds.
     * @param reconnectionAttempts the number of reconnection attempts. Default 0.
     *                             </p>
     *                             <p>
     * @throws NullPointerException the given URI is {@code null} or the port is {@code null}.
     *                              </p>
     *                              <p>
     * @since 1.0
     * </p>
     */

    public ClusterWS(@NotNull String url, @NotNull Integer port, @Nullable Boolean autoReconnect, @Nullable Integer reconnectionInterval, @Nullable Integer reconnectionAttempts) {
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
                    mBasicListener.onConnectError(ClusterWS.this, cause);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    mBasicListener.onDisconnected(ClusterWS.this, serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    if (message.equals("#0")) {
                        send("#1", null, "ping");
                        return;
                    }
                    Message.messageDecode(ClusterWS.this, message);
                }
            });
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    void send(String event, Object data, String type) {
        mWebSocket.sendText(Message.messageEncode(event, data, type));
    }

    /**
     * <p></p>
     * Sets listener to receive event on this socket.
     * <p>
     *
     * @param event    the name of the event which will be emitted from the server.
     * @param listener a listener to add.
     *                 </p>
     *                 <p>
     * @throws NullPointerException The given event name is {@code null}.
     *                              </p>
     *                              <p>
     * @since 1.0
     * </p>
     */

    public void on(@NotNull String event, Emitter.Listener listener) {
        if (event == null) {
            throw new NullPointerException("The event name must be provided");
        }
        mEmitter.on(event, listener);
    }

    /**
     * <p></P>
     * Connect to the server with the provided url and port
     * <p>
     * If {@code connect()} failed,
     * {@link BasicListener#onConnectError(ClusterWS, WebSocketException)} method is called.
     * </P>
     * <p>
     * Note that this method can be called at most only once regardless of
     * whether this method succeeded or failed. If you want to re-connect to
     * the WebSocket endpoint, you have to create a new {@code WebSocket}
     * instance again by  {@code new ClusterWS} constructor of a
     * {@link ClusterWS}.
     * </p>
     * <p>
     *
     * @since 1.0
     * </p>
     */

    public void connect() {
        try {
            mWebSocket.connect();
        } catch (WebSocketException e) {
            mBasicListener.onConnectError(this, e);
        }
    }

    /**
     * <p></P>
     * Executes {@link #connect()} asynchronous by creating a new thread and
     * calling {@code connect()} in the thread.
     * <p>
     * If {@code connect()} failed,
     * {@link BasicListener#onConnectError(ClusterWS, WebSocketException)}
     * method is called.
     * </p>
     * <p>
     *
     * @since 1.0
     * </p>
     */

    public void connectAsynchronous() {
        mWebSocket.connectAsynchronously();
    }

    /**
     * <p></p>
     * Sending message with data to a server
     * <p></p>
     *
     * @param event the name of the event which will be emitted to the server.
     * @param data  a data which will be sent to a server.
     *              <p></p>
     * @throws NullPointerException The given event name is {@code null}.
     *                              <p></p>
     * @since 1.0
     */

    public void send(String event, Object data) {
        if (event == null) {
            throw new NullPointerException("Event name must be provided");
        }
        mWebSocket.sendText(Message.messageEncode(event, data, "emit"));
    }

    /**
     * <p></p>
     * Sending message with data to a server
     * <p></p>
     *
     * @param channelName the name of the channel to subscribe.
     *                    <p></p>
     * @throws NullPointerException The given event name is {@code null}.
     *                              <p></p>
     * @since 1.0
     */

    public Channel subscribe(String channelName) {
        for (Channel channel :
                mChannels) {
            if (channel.getChannelName().equals(channelName)) {
                return channel;
            }
        }
        Channel newChannel = new Channel(channelName, this);
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

    public ArrayList<Channel> getChannels() {
        return mChannels;
    }

    public Channel getChannel(String channelName){
        for (Channel channel:
             mChannels) {
            if (channel.getChannelName().equals(channelName)){
                return channel;
            }
        }
        return null;
    }

    Emitter getEmitter() {
        return mEmitter;
    }
}
