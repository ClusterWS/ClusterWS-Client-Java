package com.clusterws;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MessageHandlerTest {
    private MessageHandler mMessageHandler;
    private String mResult;

    @Before
    public void init() {
        mMessageHandler = new MessageHandler();
        mResult = null;
    }

    @Test
    public void messageEncodeEmitString() throws Exception {
        mResult = mMessageHandler.messageEncode("test", "testString", "emit");
        assertEquals("{\"#\":[\"e\",\"test\",\"testString\"]}", mResult);
    }

    @Test
    public void messageEncodeEmitInt() throws Exception {
        mResult = mMessageHandler.messageEncode("test", 20, "emit");
        assertEquals("{\"#\":[\"e\",\"test\",20]}", mResult);
    }

    @Test
    public void messageEncodeEmitBoolean() throws Exception {
        mResult = mMessageHandler.messageEncode("test", true, "emit");
        assertEquals("{\"#\":[\"e\",\"test\",true]}", mResult);
    }

    @Test
    public void messageEncodeEmitNull() throws Exception {
        mResult = mMessageHandler.messageEncode("test", null, "emit");
        assertEquals("{\"#\":[\"e\",\"test\",null]}", mResult);
    }

    @Test
    public void messageEncodeEmitObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("int", 30);
        jsonObject.put("bool", true);
        jsonObject.put("string", "CHLEN");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(30);
        jsonArray.add(true);
        jsonArray.add("CHLEN");
        jsonObject.put("array", jsonArray);
        mResult = mMessageHandler.messageEncode("test", jsonObject, "emit");
        assertEquals("{\"#\":[\"e\",\"test\",{\"bool\":true,\"string\":\"CHLEN\",\"array\":[30,true,\"CHLEN\"],\"int\":30}]}", mResult);
    }


    @Test
    public void messageEncodeEmitArray() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("int", 30);
        jsonObject.put("bool", true);
        jsonObject.put("string", "CHLEN");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(30);
        jsonArray.add(true);
        jsonArray.add("CHLEN");
        jsonObject.put("array", jsonArray);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(30);
        arrayList.add(false);
        arrayList.add("Test message");
        arrayList.add(jsonObject);
        mResult = mMessageHandler.messageEncode("test", arrayList, "emit");
        assertEquals("{\"#\":[\"e\",\"test\",[30,false,\"Test message\",{\"bool\":true,\"string\":\"CHLEN\",\"array\":[30,true,\"CHLEN\"],\"int\":30}]]}", mResult);
    }

    @Test
    public void messageEncodeSystemSubscribe() throws Exception{
        mResult = mMessageHandler.messageEncode("subscribe","channelName","system");
        assertEquals("{\"#\":[\"s\",\"s\",\"channelName\"]}",mResult);
    }

    @Test
    public void messageEncodeSystemUnsubscribe() throws Exception{
        mResult = mMessageHandler.messageEncode("unsubscribe","channelName","system");
        assertEquals("{\"#\":[\"s\",\"u\",\"channelName\"]}",mResult);
    }
    @Test
    public void messageEncodePing() throws Exception{
        mResult = mMessageHandler.messageEncode("#1",null,"ping");
        assertEquals("#1",mResult);
    }

    @Test
    public void messageEncodePublishString(){
        mResult = mMessageHandler.messageEncode("channelname", "testData", "publish");
        assertEquals("{\"#\":[\"p\",\"channelname\",\"testData\"]}", mResult);
    }

    @Test
    public void messageEncodePublishInt() throws Exception {
        mResult = mMessageHandler.messageEncode("channelname", 20, "publish");
        assertEquals("{\"#\":[\"p\",\"channelname\",20]}", mResult);

    }

    @Test
    public void messageEncodePublishBoolean() throws Exception {
        mResult = mMessageHandler.messageEncode("channelname", true, "publish");
        assertEquals("{\"#\":[\"p\",\"channelname\",true]}", mResult);

    }

    @Test
    public void messageEncodePublishNull() throws Exception {
        mResult = mMessageHandler.messageEncode("channelname", null, "publish");
        assertEquals("{\"#\":[\"p\",\"channelname\",null]}", mResult);

    }

    @Test
    public void messageEncodePublishObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("int", 30);
        jsonObject.put("bool", true);
        jsonObject.put("string", "CHLEN");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(30);
        jsonArray.add(true);
        jsonArray.add("CHLEN");
        jsonObject.put("array", jsonArray);
        mResult = mMessageHandler.messageEncode("channelname", jsonObject, "publish");
        assertEquals("{\"#\":[\"p\",\"channelname\",{\"bool\":true,\"string\":\"CHLEN\",\"array\":[30,true,\"CHLEN\"],\"int\":30}]}", mResult);
    }


    @Test
    public void messageEncodePublishArray() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("int", 30);
        jsonObject.put("bool", true);
        jsonObject.put("string", "CHLEN");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(30);
        jsonArray.add(true);
        jsonArray.add("CHLEN");
        jsonObject.put("array", jsonArray);

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(30);
        arrayList.add(false);
        arrayList.add("Test message");
        arrayList.add(jsonObject);
        mResult = mMessageHandler.messageEncode("channelname", arrayList, "publish");
        assertEquals("{\"#\":[\"p\",\"channelname\",[30,false,\"Test message\",{\"bool\":true,\"string\":\"CHLEN\",\"array\":[30,true,\"CHLEN\"],\"int\":30}]]}", mResult);
    }
}