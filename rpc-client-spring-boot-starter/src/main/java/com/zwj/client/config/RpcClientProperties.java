package com.zwj.client.config;

import lombok.Data;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/9 20:39
 **/
@Data
public class RpcClientProperties {
    //负载均衡算法
    private String balance;
    //序列化
    private String serialization;
    //服务发现的地址
    private String discoveryAddr="127.0.0.1:2181";
    //服务超时
    private Integer timeout;
}
