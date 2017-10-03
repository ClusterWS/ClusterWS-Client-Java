package com.Yegorisa.clusterws.channel;

import com.Yegorisa.clusterws.ClusterWS;
import com.Yegorisa.clusterws.utils.Emitter;

import java.util.ArrayList;

/**
 * Created by Egor on 03.10.2017.
 */
public class Channel {
    private Emitter.Listener mListener;
    private String mChannelName;
    private ClusterWS mSocket;

    public Channel(String channelName, ClusterWS socket) {
        if (channelName == null){
            throw new NullPointerException("Channel name must be provided");
        }
        mChannelName = channelName;
        mSocket = socket;
        subscribe();
    }

    public Channel watch(Emitter.Listener listener){
        mListener = listener;
        return this;
    }

    public Channel publish(Object data){
        mSocket.send(mChannelName,data,"publish");
        return this;
    }

    public void unsubscribe(){
        mSocket.send("unsubscribe",mChannelName,"system");
        ArrayList<Channel> channelArrayList = mSocket.getChannels();
        channelArrayList.remove(this);
    }

    public void onMessage(Object data){
        if (mListener != null){
            mListener.call(mChannelName,data);
        }
    }

    private void subscribe(){
        mSocket.send("subscribe",mChannelName,"system");
    }

    public String getChannelName() {
        return mChannelName;
    }
}
