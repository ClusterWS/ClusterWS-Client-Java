package com.ClusterWS;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimerTask;

class Message {
    String messageEncode(String event, Object data, String type) {
        JSONObject jsonObject = new JSONObject();
        switch (type) {
            case "publish":
                jsonObject.put("#", new JSONArray().put("p").put(event).put(data));
                return jsonObject.toString();
            case "emit":
                jsonObject.put("#", new JSONArray().put("e").put(event).put(data));
                return jsonObject.toString();
            case "system":
                switch (event) {
                    case "subscribe":
                        jsonObject.put("#", new JSONArray().put("s").put("s").put(data));
                        return jsonObject.toString();
                    case "unsubscribe":
                        jsonObject.put("#", new JSONArray().put("s").put("u").put(data));
                        return jsonObject.toString();
                }
            case "ping":
                return event;
            default:
                return event;
        }
    }

    void messageDecode(final ClusterWS socket, String message) {
        JSONArray jsonArray = new JSONObject(message).getJSONArray("#");
        switch (jsonArray.getString(0)) {
            case "p":
                ArrayList<Channel> channels = socket.getChannels();
                String channelName = jsonArray.getString(1);
                for (Channel channel :
                        channels) {
                    if (channel.getChannelName().equals(channelName)) {
                        channel.onMessage(jsonArray.get(2));
                        break;
                    }
                }
                break;
            case "e":
                socket.getEmitter().emit(jsonArray.getString(1), jsonArray.get(2));
                break;
            case "s":
                if (jsonArray.getString(1).equals("c")) {
                    socket.getPingTimer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (socket.getMissedPing() < 3) {
                                socket.incrementLost();
                            } else {
                                socket.disconnect(4001, "No pings");
                                cancel();
                            }
                        }
                    }, 0, jsonArray.getJSONObject(2).getInt("ping"));
                    boolean useBinary = jsonArray.getJSONObject(2).getBoolean("binary");
                    socket.setUseBinary(useBinary);
                    if (socket.getClusterWSListener() != null) {
                        socket.getClusterWSListener().onConnected(socket);
                    }
                }
                break;
        }
    }
}
