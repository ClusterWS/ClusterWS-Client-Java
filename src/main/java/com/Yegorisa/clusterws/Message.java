package com.Yegorisa.clusterws;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by Egor on 01.10.2017.
 */
class Message {
    static String messageEncode(String event, Object data, String type) {
        JSONObject json = new JSONObject();
        switch (type) {
            case "publish":
                json.put("#", new JSONArray().put("p").put(event).put(data));
                return json.toString();
            case "emit":
                json.put("#", new JSONArray().put("e").put(event).put(data));
                return json.toString();
            case "system":
                switch (event) {
                    case "subscribe":
                        json.put("#", new JSONArray().put("s").put("s").put(data));
                        return json.toString();
                    case "unsubscribe":
                        json.put("#", new JSONArray().put("s").put("u").put(data));
                        return json.toString();
                }
            case "ping":
                return event;
            default:
                return event;
        }
    }

    static void messageDecode(final ClusterWS webSocket, String message){
        System.out.println("Decode called");
        JSONObject jsonObject = new JSONObject(message);
        ArrayList<Channel> channels = webSocket.getChannels();
        JSONArray jsonArray = jsonObject.getJSONArray("#");
        switch (jsonArray.getString(0)) {
            case "p":
                String channelName = jsonArray.getString(1);
                for (Channel channel :
                        channels) {
                    if (channel.getChannelName().equals(channelName)) {
                        channel.onMessage(jsonArray.getString(2));
                        break;
                    }
                }
                break;
            case "e":
                webSocket.getEmitter().emit(jsonArray.getString(1), jsonArray.get(2));
                break;
            case "s":
                switch (jsonArray.getString(1)) {
                    case "c":
                        webSocket.getPingTimer().scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                if (webSocket.getLost() < 3){
                                    webSocket.incrementLost();
                                } else {
                                    webSocket.disconnect(3001,"No pings");
                                }
                            }
                        },0,jsonArray.getJSONObject(2).getInt("ping"));
                        break;
                }
        }
    }
}
