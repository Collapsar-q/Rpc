package com.zwj.client.cache;

import com.zwj.client.transport.RpcFuture;
import com.zwj.core.common.RpcResponse;
import com.zwj.core.protocol.MessageProtocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/9 17:59
 **/
public class LocalRpcResponseCache {
    // 使用ConcurrentHashMap作为线程安全的Map来存储请求和响应的映射关系
    private static Map<String, RpcFuture<MessageProtocol<RpcResponse>>> requestResponseCache =new ConcurrentHashMap<>();
    /**
     * 添加请求和响应的映射关系到缓存中
     * @param reqId   请求的唯一标识
     * @param future  请求对应的RpcFuture对象
     *
     */
    public static void add(String reqId,RpcFuture<MessageProtocol<RpcResponse>> future){
        requestResponseCache.put(reqId,future);
    }
    /**
     *  设置响应数据
     * @param reqId
     * @param messageProtocol
     */
    public static void fillResponse(String reqId,MessageProtocol<RpcResponse> messageProtocol){
        // 获取缓存中的 future
        RpcFuture<MessageProtocol<RpcResponse>> future = requestResponseCache.get(reqId);
        future.setResponse(messageProtocol);
        requestResponseCache.remove(reqId);
    }
    }
