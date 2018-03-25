package com.clusterws;

import java.util.List;

public class Channel {
    public interface IChannelListener {
        void onDataReceived(String channelName, Object data);
    }

    private IChannelListener mChannelListener;
    private String mChannelName;
    private ClusterWS mClusterWS;

    public Channel(String channelName, ClusterWS clusterWS) {
        mChannelName = channelName;
        mClusterWS = clusterWS;
    }

    public Channel watch(IChannelListener listener) {
        mChannelListener = listener;
        return this;
    }

    public Channel publish(Object data) {
        mClusterWS.send(mChannelName, data, "publish");
        return this;
    }

    public void unsubscribe() {
        mClusterWS.send("unsubscribe", mChannelName, "system");
        List<Channel> channelArrayList = mClusterWS.getChannels();
        channelArrayList.remove(this);
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
        mClusterWS.send("subscribe", mChannelName, "system");
    }
}
