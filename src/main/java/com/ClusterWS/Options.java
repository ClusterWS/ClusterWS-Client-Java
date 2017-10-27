package com.ClusterWS;

class Options {
    private String mUrl;
    private String mPort;

    Options(String url, String port) {
        if (port == null){
            throw new NullPointerException("Port must be provided!");
        }
        mUrl = url;
        mPort = port;
    }

    String getUrl() {
        return mUrl;
    }

    String getPort() {
        return mPort;
    }
}
