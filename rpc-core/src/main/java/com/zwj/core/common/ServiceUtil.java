package com.zwj.core.common;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 21:34
 **/
public class ServiceUtil {
    public static String serviceKey(String serviceName,String version){
        return String.join("-",serviceName,version);
    }
}
