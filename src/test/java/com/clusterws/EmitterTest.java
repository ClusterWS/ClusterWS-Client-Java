package com.clusterws;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmitterTest {
    private boolean gotMessage;
    private Emitter mEmitter;

    @Before
    public void init(){
        mEmitter = new Emitter();
        gotMessage = false;
    }

    @After
    public void deleteEmitter(){
        mEmitter = null;
    }

    @Test
    public void addEventListenerAndEmit() throws Exception {
        mEmitter.addEventListener("test", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotMessage = true;
            }
        });
        mEmitter.emit("test","HUI");
        assertTrue(gotMessage);
    }

    @Test
    public void addDifferentEventListenersWithTheSameEventName() throws Exception{
        mEmitter.addEventListener("test", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotMessage = false;
            }
        });
        mEmitter.addEventListener("test", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotMessage = true;
            }
        });
        mEmitter.emit("test","HUI");
        assertTrue(gotMessage);
    }

    @Test
    public void removeAllEvents() throws Exception {
        mEmitter.addEventListener("test", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotMessage = true;
            }
        });
        mEmitter.removeAllEvents();
        mEmitter.emit("test","HUI");
        assertFalse(gotMessage);
    }

}