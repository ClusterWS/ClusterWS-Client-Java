package com.clusterws;

import java.nio.ByteBuffer;

public interface ISocketEvents {
    void onOpen();

    void onError(Exception exception);

    void onClose(int code, String reason);

    void onBinaryMessage(ByteBuffer bytes);

    void onMessage(String message);

}
