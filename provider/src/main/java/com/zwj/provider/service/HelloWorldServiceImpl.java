package com.zwj.provider.service;

import com.zwj.api.service.HelloWorldService;
import com.zwj.server.annotation.RpcService;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 14:18
 **/
@RpcService(interfaceType = HelloWorldService.class,version = "1.0")
public class HelloWorldServiceImpl implements HelloWorldService {
    @Override
    public String sayHello(String name) {
        return String.format("你好：%s，rpc调用成功",name);
    }
}
