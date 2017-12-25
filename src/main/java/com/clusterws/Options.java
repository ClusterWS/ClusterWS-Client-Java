package com.clusterws;


class Options {
    private String mUrl;

    Options(String url) {
        if (url == null){
            throw new NullPointerException("Url must be provided");
        }
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }
}
