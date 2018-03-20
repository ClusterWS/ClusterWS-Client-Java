package com.clusterws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.TimerTask;


class MessageHandler {
    String messageEncode(String event, Object data, String type) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        switch (type) {
            case "publish":
                jsonArray.add("p");
                jsonArray.add(event);
                jsonArray.add(data);
                jsonObject.put("#", jsonArray);
                return jsonObject.toJSONString();
            case "emit":
                jsonArray.add("e");
                jsonArray.add(event);
                jsonArray.add(data);
                jsonObject.put("#", jsonArray);
                return jsonObject.toJSONString();
            case "system":
                switch (event) {
                    case "subscribe":
                        jsonArray.add("s");
                        jsonArray.add("s");
                        jsonArray.add(data);
                        jsonObject.put("#", jsonArray);
                        return jsonObject.toJSONString();
                    case "unsubscribe":
                        jsonArray.add("s");
                        jsonArray.add("u");
                        jsonArray.add(data);
                        jsonObject.put("#", jsonArray);
                        return jsonObject.toJSONString();
                }
            case "ping":
                return event;
            default:
                return event;
        }
    }

    void messageDecode(final ClusterWS socket, String message) {
        JSONArray jsonArray = JSON.parseObject(message).getJSONArray("#");
        switch (jsonArray.getString(0)) {
            case "p":
                //
                List<Channel> channelArrayList = socket.getChannels();
                String channelName = jsonArray.getString(1);
                for (Channel channel :
                        channelArrayList) {
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
                    socket.getPingHandler().getPingTimer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (socket.getPingHandler().getMissedPing() < 3) {
                                socket.getPingHandler().incrementMissedPing();
                            } else {
                                socket.disconnect(4001, "No pings");
                                cancel();
                            }
                        }
                    }, 0, jsonArray.getJSONObject(2).getInteger("ping"));
                    boolean useBinary = jsonArray.getJSONObject(2).getBoolean("binary");
                    socket.setUseBinary(useBinary);
                    if (socket.getClusterWSListener() != null) {
                        socket.getClusterWSListener().onConnected();
                    }
                }
                break;
        }
    }
}
