package com.zwj.server.store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zwj
 * @Description: 本地服务器缓存类，用于缓存服务器对象
 * @DateTime: 2023/5/5 21:12
 **/
public final class LocalServerCache {
    // 缓存服务器对象的Map，键为服务器名称，值为服务器对象
    private static final Map<String, Object> serverCacheMap = new  ConcurrentHashMap<>();
    /**
     存储服务器对象到缓存中
     @param serverName 服务器名称
     @param server 服务器对象
     */
    public static void store(String serverName,Object server){
        //将存储服务对象添加到map中规则如果存在则将老的覆盖
        serverCacheMap.merge(serverName,server,(Object oldObj,Object newObj)->newObj);
    }
    //查找
    public static Object get(String serverName){
        return serverCacheMap.get(serverName);
    }
    //查找所有服务器
    public static Map<String,Object> getAll(){
        return serverCacheMap;
    }
}
