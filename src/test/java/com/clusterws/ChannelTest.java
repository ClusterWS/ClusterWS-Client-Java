package com.clusterws;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ChannelTest {
    private Channel mChannel;
    private static final String CHANNEL_NAME = "testChannel";
    private static final String TEST_STRING_DATA = "testData";

    @Mock
    private ClusterWS mClusterWS;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void init(){
        mChannel = new Channel(CHANNEL_NAME,mClusterWS);
    }

    @Test
    public void watchAndOnMessage() throws Exception {
        mChannel.watch(new Channel.IChannelListener() {
            @Override
            public void onDataReceived(String channelName, Object data) {
                assertEquals("Data is not the same",TEST_STRING_DATA,data);
            }
        });
        mChannel.onMessage(TEST_STRING_DATA);
    }


    @Test
    public void unsubscribe() throws Exception {
        ArrayList<Channel> channelArrayList = new ArrayList<>();
        channelArrayList.add(mChannel);
        channelArrayList.add(new Channel("GAY",mClusterWS));
        channelArrayList.add(new Channel("SHIT",mClusterWS));
        when(mClusterWS.getChannels()).thenReturn(channelArrayList);
        mChannel.unsubscribe();
        assertNotEquals("There is an old channel",CHANNEL_NAME,channelArrayList.get(0).getChannelName());
    }

    @Test
    public void getChannelName() throws Exception {
        assertEquals(mChannel.getChannelName(),CHANNEL_NAME);
    }


}