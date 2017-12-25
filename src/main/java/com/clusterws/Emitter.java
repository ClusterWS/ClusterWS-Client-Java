package com.clusterws;

import java.util.concurrent.ConcurrentHashMap;

class Emitter {
    private ConcurrentHashMap<String,IEmitterListener> mEvents;

    Emitter() {
        mEvents = new ConcurrentHashMap<>();
    }

    void addEventListener(String event, IEmitterListener listener){
        if (mEvents.containsKey(event)){
            mEvents.replace(event,listener);
        } else {
            mEvents.put(event,listener);
        }
    }

    void emit(String event, Object object){
        IEmitterListener listener = mEvents.get(event);
        if (listener != null){
            listener.onDataReceived(object);
        }
    }

    void removeAllEvents(){
        mEvents = new ConcurrentHashMap<>();
    }
}
