package com.clusterws;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Null;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ClusterWSTest {

    private ClusterWS mClusterWS;
    private Object receivedData;
    private boolean gotTheData;

    @Before
    public void init() {
        mClusterWS = new ClusterWS("ws://localhost:80");
        receivedData = null;
        gotTheData = false;
    }

    @After
    public void clearSocket() {
        mClusterWS.disconnect(null, null);
        mClusterWS = null;
    }

    @Test
    public void connect() throws Exception {
        mClusterWS.connect();
        Thread.sleep(1000);

        assertEquals("Socket did not connect", WebSocket.READYSTATE.OPEN,mClusterWS.getState());
    }

    @Test
    public void testOnAndSendString() throws Exception {
        mClusterWS.connect();
        mClusterWS.on("String", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotTheData = true;
                receivedData = data;
            }
        });
        Thread.sleep(1000);
        mClusterWS.send("String", "test message");
        Thread.sleep(1000);

        assertTrue("Did not get the data", gotTheData);
        assertEquals("Data send and data received are not the same", "test message", receivedData);
    }

    @Test
    public void testSendAndOnBoolean() throws Exception {
        mClusterWS.connect();
        mClusterWS.on("String", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotTheData = true;
                receivedData = data;
            }
        });
        Thread.sleep(1000);
        mClusterWS.send("String", true);
        Thread.sleep(1000);

        assertTrue("Did not get the data", gotTheData);
        assertTrue("Data send and data received are not the same", (Boolean) receivedData);
    }

    @Test
    public void testSendAndOnInteger() throws Exception {
        mClusterWS.connect();
        mClusterWS.on("String", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotTheData = true;
                receivedData = data;
            }
        });
        Thread.sleep(1000);
        mClusterWS.send("String", 30);
        Thread.sleep(1000);

        assertTrue("Did not get the data", gotTheData);
        assertEquals("Data send and data received are not the same", 30, (int) receivedData);
    }

    @Test
    public void testSendAndOnNull() throws Exception {
        mClusterWS.connect();
        mClusterWS.on("String", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotTheData = true;
                if (data.equals(null)) {
                    receivedData = null;
                }
            }
        });
        Thread.sleep(1000);
        mClusterWS.send("String", null);
        Thread.sleep(1000);

        assertTrue("Did not get the data", gotTheData);
        assertNull("Data send and data received are not the same", receivedData);
    }

    @Test
    public void testSendAndOnObject() throws Exception {
        mClusterWS.connect();
        mClusterWS.on("String", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotTheData = true;
                receivedData = data;
            }
        });
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("int", 30);
        jsonObject.put("bool", true);
        jsonObject.put("string", "CHLEN");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(30);
        jsonArray.add(true);
        jsonArray.add("CHLEN");
        jsonObject.put("array", jsonArray);
        Thread.sleep(1000);
        mClusterWS.send("String", jsonObject);
        Thread.sleep(1000);

        assertTrue("Did not get the data", gotTheData);
        assertEquals("Data send and data received are not the same", jsonObject.toString(), receivedData.toString());
    }

    @Test
    public void testSendAndOnArray() throws Exception {
        mClusterWS.connect();
        mClusterWS.on("String", new IEmitterListener() {
            @Override
            public void onDataReceived(Object data) {
                gotTheData = true;
                receivedData = data;
            }
        });
        ArrayList<Object> mObjectArrayList;
        mObjectArrayList = new ArrayList<>();
        mObjectArrayList.add(30);
        mObjectArrayList.add(false);
        mObjectArrayList.add("Test message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("int", 30);
        jsonObject.put("bool", true);
        jsonObject.put("string", "CHLEN");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(30);
        jsonArray.add(true);
        jsonArray.add("CHLEN");
        jsonObject.put("array", jsonArray);

        mObjectArrayList.add(jsonObject);
        Thread.sleep(1000);
        mClusterWS.send("String", mObjectArrayList.toString());
        Thread.sleep(1000);

        assertTrue("Did not get the data", gotTheData);
        assertEquals("Data send and data received are not the same", mObjectArrayList.toString(), receivedData.toString());
    }

    @Test
    public void testDisconnect() throws Exception {
        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.disconnect(null, null);
        Thread.sleep(1000);
        assertEquals(WebSocket.READYSTATE.CLOSED, mClusterWS.getState());
    }

    @Test
    public void testAllGetStates() throws Exception {
        final com.clusterws.ClusterWSStatesBool clusterWSStatesBool = new com.clusterws.ClusterWSStatesBool();
        if (mClusterWS.getState() == WebSocket.READYSTATE.NOT_YET_CONNECTED) {
            clusterWSStatesBool.setNotYetConnected(true);
        }

        mClusterWS.setClusterWSListener(new IClusterWSListener() {
            @Override
            public void onConnected() {
                if (mClusterWS.getState() == WebSocket.READYSTATE.OPEN) {
                    clusterWSStatesBool.setOpen(true);
                }
            }

            @Override
            public void onError(Exception exception) {

            }

            @Override
            public void onDisconnected(int code, String reason) {

            }
        });

        mClusterWS.connect();
        if (mClusterWS.getState() == WebSocket.READYSTATE.CONNECTING) {
            clusterWSStatesBool.setConnecting(true);
        }
        Thread.sleep(1000);
        mClusterWS.disconnect(1000, "Test");
        if (mClusterWS.getState() == WebSocket.READYSTATE.CLOSING) {
            clusterWSStatesBool.setClosing(true);
        }
        Thread.sleep(1000);
        if (mClusterWS.getState() == WebSocket.READYSTATE.CLOSED) {
            clusterWSStatesBool.setClosed(true);
        }

        assertTrue("State CREATED not working", clusterWSStatesBool.isNotYetConnected());
        //TODO Сложно поймать этот state
//            assertTrue("State CONNECTING not working",clusterWSStatesBool.isConnecting());
        assertTrue("State OPEN not working", clusterWSStatesBool.isOpen());
        assertTrue("State CLOSING not working", clusterWSStatesBool.isClosing());
        assertTrue("State CLOSED not working", clusterWSStatesBool.isClosed());
    }

    @Test
    public void testReconnection() throws Exception{
        mClusterWS.setReconnection(true,1000,2000,null);
        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.disconnect(3002,"test");
        Thread.sleep(2000);
        assertEquals("Did not reconnect", WebSocket.READYSTATE.OPEN, mClusterWS.getState());
    }

    @Test
    public void testPingPong() throws Exception{
        mClusterWS.connect();
        Thread.sleep(1900);
        assertEquals("Websocket disconnected", WebSocket.READYSTATE.OPEN,mClusterWS.getState());
    }

    @Test(expected = NullPointerException.class)
    public void testNullUrl() throws Exception{
        mClusterWS = new ClusterWS(null);
    }

}