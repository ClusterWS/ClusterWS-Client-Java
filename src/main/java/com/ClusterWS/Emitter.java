package com.ClusterWS;

import java.util.concurrent.ConcurrentHashMap;


class Emitter {
    private ConcurrentHashMap<String, EmitterListener> mEvents;

    Emitter() {
        mEvents = new ConcurrentHashMap<>();
    }

    void on(String event, EmitterListener listener) {
        if (mEvents.containsKey(event)) {
            mEvents.replace(event,listener);
        } else {
            mEvents.put(event, listener);
        }
    }

    void emit(String event, Object object) {
        EmitterListener listener = mEvents.get(event);
        if (listener != null) {
            listener.onDataReceived(object);
        }
    }

    void removeAllEvents() {
        mEvents = new ConcurrentHashMap<>();
    }
}
