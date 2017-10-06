package com.Yegorisa.clusterws;

import java.util.ArrayList;

/**
 * Created by Egor on 03.10.2017.
 */
public class Channel {
    private Emitter.Listener mListener;
    private String mChannelName;
    private ClusterWS mSocket;

    Channel(String channelName, ClusterWS socket) {
        if (channelName == null) {
            throw new NullPointerException("Channel name must be provided");
        }
        mChannelName = channelName;
        mSocket = socket;
        subscribe();
    }

    public Channel watch(Emitter.Listener listener) {
        mListener = listener;
        return this;
    }

    public Channel publish(Object data) {
        mSocket.send(mChannelName, data, "publish");
        return this;
    }

    public void unsubscribe() {
        mSocket.send("unsubscribe", mChannelName, "system");
        ArrayList<Channel> channelArrayList = mSocket.getChannels();
        channelArrayList.remove(this);
    }

    String getChannelName() {
        return mChannelName;
    }

    void onMessage(Object data) {
        if (mListener != null) {
            mListener.call(mChannelName, data);
        }
    }

    void subscribe() {
        mSocket.send("subscribe", mChannelName, "system");
    }

}
