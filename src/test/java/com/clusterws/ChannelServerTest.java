package com.clusterws;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ChannelServerTest {
    private ClusterWS mClusterWS;
    private boolean mGotTheData;
    private Object mReceivedData;

    @Before
    public void init() throws Exception {
        mClusterWS = new ClusterWS("ws://localhost:80");
        mGotTheData = false;
        mReceivedData = null;
    }

    @Test
    public void testChannelPublishAndWatchInt() throws Exception {
        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.subscribe("testChannel")
                .watch(new Channel.IChannelListener() {
                    @Override
                    public void onDataReceived(String channelName, Object data) {
                        mGotTheData = true;
                        mReceivedData = data;
                    }
                })
                .publish(24);
        Thread.sleep(1000);

        assertTrue("Did not get the data", mGotTheData);
        assertEquals("Data send and data received are not the same", 24, mReceivedData);
    }

    @Test
    public void testChannelPublishAndWatchString() throws Exception {
        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.subscribe("testChannel")
                .watch(new Channel.IChannelListener() {
                    @Override
                    public void onDataReceived(String channelName, Object data) {
                        mGotTheData = true;
                        mReceivedData = data;
                    }
                })
                .publish("test string");
        Thread.sleep(1000);

        assertTrue("Did not get the data", mGotTheData);
        assertEquals("Data send and data received are not the same", "test string", mReceivedData);
    }

    @Test
    public void testChannelPublishAndWatchObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("int", 30);
        jsonObject.put("bool", true);
        jsonObject.put("string", "CHLEN");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(30);
        jsonArray.add(true);
        jsonArray.add("CHLEN");
        jsonObject.put("array", jsonArray);

        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.subscribe("testChannel")
                .watch(new Channel.IChannelListener() {
                    @Override
                    public void onDataReceived(String channelName, Object data) {
                        mGotTheData = true;
                        mReceivedData = data;
                    }
                })
                .publish(jsonObject);
        Thread.sleep(1000);

        assertTrue("Did not get the data", mGotTheData);
        assertEquals("Data send and data received are not the same", jsonObject, mReceivedData);
    }

    @Test
    public void testChannelPublishAndWatchNull() throws Exception {
        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.subscribe("testChannel")
                .watch(new Channel.IChannelListener() {
                    @Override
                    public void onDataReceived(String channelName, Object data) {
                        mGotTheData = true;
                        mReceivedData = data;
                    }
                })
                .publish(null);
        Thread.sleep(1000);

        assertTrue("Did not get the data", mGotTheData);
        assertEquals("Data send and data received are not the same", null, mReceivedData);
    }

    @Test
    public void testChannelPublishAndWatchArray() throws Exception {
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

        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.subscribe("testChannel")
                .watch(new Channel.IChannelListener() {
                    @Override
                    public void onDataReceived(String channelName, Object data) {
                        mGotTheData = true;
                        mReceivedData = data;
                    }
                })
                .publish(mObjectArrayList);
        Thread.sleep(1000);

        assertTrue("Did not get the data", mGotTheData);
        assertEquals("Data send and data received are not the same", mObjectArrayList, mReceivedData);
    }

    @Test
    public void testGetChannelByName() throws Exception {
        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.subscribe("testChannel");
        assertNotNull(mClusterWS.getChannelByName("testChannel"));
    }

    @Test
    public void testGetChannelList() throws Exception {
        mClusterWS.connect();
        Thread.sleep(1000);
        mClusterWS.subscribe("testChannel1");
        mClusterWS.subscribe("testChannel2");
        mClusterWS.subscribe("testChannel3");

        assertEquals(mClusterWS.getChannels().size(), 3);
        assertEquals(mClusterWS.getChannels().get(0).getChannelName(), "testChannel1");
        assertEquals(mClusterWS.getChannels().get(1).getChannelName(), "testChannel2");
        assertEquals(mClusterWS.getChannels().get(2).getChannelName(), "testChannel3");
    }

    @Test
    public void testTryGetChannelByNameAfterUnsubscribing() throws Exception {
        mClusterWS.connect();
        Thread.sleep(1000);
        Channel channel = mClusterWS.subscribe("testChannel1");
        channel.unsubscribe();
        assertNull(mClusterWS.getChannelByName("testChannel1"));
    }

    @Test
    public void testResubscribeOnAllChannelsAfterReconnection() throws Exception {
        mClusterWS.setReconnection(true, 1000, 2000, null);
        mClusterWS.connect();
        Thread.sleep(1000);
        Channel channel = mClusterWS.subscribe("testChannel")
                .watch(new Channel.IChannelListener() {
                    @Override
                    public void onDataReceived(String channelName, Object data) {
                        mGotTheData = true;
                        mReceivedData = data;
                    }
                });
        mClusterWS.disconnect(4001, "hui");
        Thread.sleep(1000);
        channel.publish("testData");
        Thread.sleep(1000);
        assertTrue("Did not get the data", mGotTheData);
        assertEquals("Data send and data received are not the same", "testData", mReceivedData);
    }

}