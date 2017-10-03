package com.Yegorisa.clusterws.utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Egor on 01.10.2017.
 */
public class Message {
    public static String messageEncode(String event, Object data, String type) {
        JSONObject json = new JSONObject();
        switch (type) {
            case "publish":
                json.put("m", new JSONArray().put("p").put(event).put(data));
                return json.toString();
            case "emit":
                json.put("m", new JSONArray().put("e").put(event).put(data));
                return json.toString();
            case "system":
                switch (event) {
                    case "subscribe":
                        json.put("m", new JSONArray().put("s").put("s").put(data));
                        return json.toString();
                    case "unsubscribe":
                        json.put("m", new JSONArray().put("s").put("u").put(data));
                        return json.toString();
                }
            case "ping":
                return event;
            default:
                return event;
        }
    }
}
