package com.zwj.client.proxy;

import com.zwj.client.config.RpcClientProperties;
import com.zwj.core.discovery.DiscoveryService;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zwj
 * @Description: 客户端代理工厂类,该工厂类的作用是根据接口和服务版本创建代理对象，并使用缓存进行重复创建的优化，以提高性能
 * @DateTime: 2023/5/10 23:00
 **/
public class ClientStubProxyFactory {
    private Map<Class<?>,Object>objectCache=new HashMap<>();
    /**
     * 获取代理对象
     *
     * @param clazz   接口
     * @param version 服务版本
     * @param <T>
     * @return 代理对象
     */
    public <T> T getProxy(Class<?> clazz, String version, DiscoveryService discoveryService, RpcClientProperties properties){
        return (T) objectCache.computeIfAbsent(clazz,clz-> Proxy.newProxyInstance(clz.getClassLoader(),new Class[]{clz},new ClientStubInvocationHandler(discoveryService,properties,clz,version)));

    }
}
