package com.ClusterWS;

import java.util.ArrayList;

/**
 * Created by Egor on 08.10.2017.
 */
public class Channel {
    public interface ChannelListener {
        void onDataReceived(String channelName, Object data);
    }

    private ChannelListener mChannelListener;
    private String mChannelName;
    private ClusterWS mSocket;

    public Channel(String channelName, ClusterWS socket) {
        mChannelName = channelName;
        mSocket = socket;
        subscribe();
    }

    public Channel watch(ChannelListener listener) {
        mChannelListener = listener;
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
        mSocket.setChannels(channelArrayList);
    }

    String getChannelName() {
        return mChannelName;
    }

    void onMessage(Object data) {
        if (mChannelListener != null) {
            mChannelListener.onDataReceived(mChannelName, data);
        }
    }

    void subscribe() {
        mSocket.send("subscribe", mChannelName, "system");
    }
}
