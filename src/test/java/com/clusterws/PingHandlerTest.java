package com.clusterws;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PingHandlerTest {

    private PingHandler mPingHandler;

    @Before
    public void init(){
        mPingHandler = new PingHandler();
    }

    @Test
    public void incrementMissedPing() throws Exception {
        mPingHandler.incrementMissedPing();
        assertEquals(1,mPingHandler.getMissedPing());
    }

    @Test
    public void setMissedPingToZero() throws Exception {
        mPingHandler.incrementMissedPing();
        mPingHandler.setMissedPingToZero();
        assertEquals(0,mPingHandler.getMissedPing());
    }


    @Test
    public void getMissedPing() throws Exception {
        assertEquals(0,mPingHandler.getMissedPing());
    }

}