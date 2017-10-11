# ClusterWS Client Java

## Overview
This is official Java client for [ClusterWS](https://github.com/ClusterWS/ClusterWS).

[ClusterWS](https://github.com/ClusterWS/ClusterWS) - is a minimal **Node JS http & real-time** framework which allows to scale WebSocket ([uWS](https://github.com/uNetworking/uWebSockets) - one of the fastest WebSocket libraries) between **Workers** in [Node JS Cluster](https://nodejs.org/api/cluster.html) and utilize all available CPU.

**This library requires [ClusterWS](https://github.com/ClusterWS/ClusterWS) on the server**

## Installation

### Gradle

```Gradle
dependencies {
    compile 'com.neovisionaries:nv-websocket-client:2.3'
}
```
### Maven

```xml
<dependency>
    <groupId>com.neovisionaries</groupId>
    <artifactId>nv-websocket-client</artifactId>
    <version>2.3</version>
</dependency>
```


## Socket
### 1. Connecting
You can connect to the server with the following code: 
```java
/**
    First parameter: string - url of the server without http or https. (must be provided)
    Second parameter: string - port of the server. (must be provided)
 */
ClusterWS clusterWS = new ClusterWS("localhost","80");
clusterWS.connect();

```

in case if you are using this library on Android you have to connect on separate thread or use method connectAsynchronously: 
```java
ClusterWS clusterWS = new ClusterWS("localhost","80");
clusterWS.connectAsynchronously();
```

If you want to set auto reconnection use setReconnection method
```java
/**
    First parameter: boolean - allow to auto-reconnect to the server on lost connection. (default false)
    Second parameter: int - how long min time wait. (default 1000) in ms
    Third parameter: int -  how long max time wait. (default 5000) in ms
    Fourth parameter: int  - how many times to try, 0 means without limit. (default 0)
*/
clusterWS.setReconnection(true, 1000, 2000, 3);
```

*Auto reconnect count random time between Max and Min interval value this will reduce amount of users which are connection at the same time on reconnection and reduce server load on restart of the server*

### 2. Listen on events
To listen on events from the server you should use `on` method witch is provided by `clusterWS`
```java
/**
    event name: string - can be any string you wish
    data: any - is what you send from the client
*/
clusterWS.on("event name", new EmitterListener() {
    @Override
    public void onDataReceived(Object data) {
        // in here you can write any logic
    }
});
```

*Also `clusterWS` gets **Reserved Events** such as `'onConnected'`, `'onDisconnected'` and `'onConnectError'`*
```java
clusterWS.setClusterWSListener(new ClusterWSListener() {
    @Override
    public void onConnected(final ClusterWS socket) {
        // in here you can write any logic
    }

    @Override
    public void onConnectError(ClusterWS socket, WebSocketException exception) {
        // in here you can write any logic
    }

    @Override
    public void onDisconnected(ClusterWS socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
        // in here you can write any logic
    }
});
```

### 3. Send events
To send events to the server use `send` method witch is provided by `clusterWS`
```java
/**
    event name: string - can be any string you wish (client must listen on this event name)
    data: any - is what you want to send to the client
*/
clusterWS.send("event name",data);

```

*Avoid emitting **Reserved Events** such as `'connect'`, `'connection'`, `'disconnect'` and `'error'`. Also avoid emitting  event and events with `'#'` at the start.*

## Pub/Sub
You can `subscribe`, `watch`, `unsubscribe`, `publish` and `getChannelByName` to/from the channels
```java
/**
    channel name: string - can be any string you wish
*/
Channel channel = clusterWS.subscribe("channel name");

/**
    channelName: string - name of the channel to which data was sent to
    data: any - is what you get when you or some one else publish to the channel
*/
channel.watch(new Channel.ChannelListener() {
    @Override
    public void onDataReceived(String channelName, Object data) {
        // in here you can write any logic
    }
})

/**
    data: any - is what you want to publish to the channel (everyone who is subscribed will get it)
*/
channel.publish(data);

/**
    This method is used to unsubscribe from the channel
*/
channel.unsubscribe();

/**
    Also you can chain everything in one expression
*/
Channel channel = clusterWS.subscribe("channel name").watch(new Channel.ChannelListener() {
    @Override
    public void onDataReceived(String channelName, Object data) {
        // in here you can write any logic
    }
}).publish(data);


/**
    You can get channel by channel name only if you were subscribed before
*/

clusterWS.getChannelByName('channel name').publish(data);
clusterWS.getChannelByName('channel name').unsubscribe();
```

**To make sure that user is connected to the server before subscribing, do it on `onConnected` event or on any other events which you emit from the server, otherwise subscription may not work properly**

## See also
* [Medium ClusterWS](https://medium.com/clusterws)


#### *Docs is still under development.*

## Good luck and Have fun :balloon: :running: