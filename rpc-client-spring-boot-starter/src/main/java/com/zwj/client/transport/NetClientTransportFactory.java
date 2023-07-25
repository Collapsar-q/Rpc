package com.zwj.client.transport;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/9 22:54
 **/
public class NetClientTransportFactory {
    public static NetClientTransport getNetClientTransport(){
        return new NettyNetClientTransport();
    }
}
