package com.Yegorisa.clusterws;

/**
 * Created by Egor on 01.10.2017.
 */
class Options {
    private String mUrl;
    private Integer mPort;
    private Boolean mAutoReconnect;
    private Integer mReconnectionInterval;
    private Integer mReconnectionAttempts;

    /**
     * If port is null, than standard
     */

    Options(String url, Integer port, Boolean autoReconnect, Integer reconnectionInterval, Integer reconnectionAttempts) {
        if (url == null) {
            throw new NullPointerException("Url must be provided");
        }
        if (port == null) {
            throw new NullPointerException("Port must be provided");
        }
        mUrl = url;
        mPort = port;
        mAutoReconnect = autoReconnect != null ? autoReconnect : false;
        mReconnectionInterval = reconnectionInterval != null ? reconnectionInterval : 5000;
        mReconnectionAttempts = reconnectionAttempts != null ? reconnectionAttempts : 0;
    }

    String getUrl() {
        return mUrl;
    }

    Integer getPort() {
        return mPort;
    }

    Boolean getAutoReconnect() {
        return mAutoReconnect;
    }

    Integer getReconnectionInterval() {
        return mReconnectionInterval;
    }

    Integer getReconnectionAttempts() {
        return mReconnectionAttempts;
    }
}
