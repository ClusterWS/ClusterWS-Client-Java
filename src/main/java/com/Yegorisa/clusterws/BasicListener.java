package com.Yegorisa.clusterws;

import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

/**
 * <p></p>
 * Listener interface to receive ClusterWS events.
 * <p>
 * <p>
 * An implementation of this interface should be added by {@link
 * ClusterWS#setBasicListener(BasicListener)} to a {@link ClusterWS}
 * instance before calling {@link ClusterWS#connect()}.
 * </p>
 * <p>
 *
 * @since 1.0
 * </p>
 */
public interface BasicListener {
    /**
     * <p></p>
     * Called after the opening handshake of the ClusterWS connection succeeded.
     * <p>
     *
     * @param webSocket the ClusterWS instance.
     *                  </p>
     *                  <p>
     * @since 1.0
     * </p>
     */

    void onConnected(ClusterWS webSocket);

    /**
     * <p></p>
     * Called after the WebSocket connection was closed.
     * <p>
     *
     * @param webSocket        the ClusterWS instance.
     * @param serverCloseFrame the <a href="https://tools.ietf.org/html/rfc6455#section-5.5.1"
     *                         >close frame</a> which the server sent to this client.
     *                         This may be {@code null}.
     * @param clientCloseFrame the <a href="https://tools.ietf.org/html/rfc6455#section-5.5.1"
     *                         >close frame</a> which this client sent to the server.
     *                         This may be {@code null}.
     * @param closedByServer   {@code true} if the closing handshake was started by the server.
     *                         {@code false} if the closing handshake was started by the client.
     *                         </p>
     *                         <p>
     * @since 1.0
     * </p>
     */

    void onDisconnected(ClusterWS webSocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer);

    /**
     * <p></p>
     * Called when {@link ClusterWS#connect()} or {@link ClusterWS#connectAsynchronous()} failed.
     * <p>
     *
     * @param webSocket the ClusterWS instance.
     * @param exception The exception thrown by {@link ClusterWS#connect() connect()}
     *                  method.
     *                  </p>
     *                  <p>
     * @since 1.0
     * </p>
     */

    void onConnectError(ClusterWS webSocket, WebSocketException exception);
}
