package com.zwj.core.serialization;

import java.io.IOException;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/7 23:46
 **/
public interface RpcSerialization {
    /**
     * 将对象序列化为字节数组
     *
     * @param obj 待序列化的对象
     * @return 序列化后的字节数组
     * @throws IOException 如果序列化过程中发生 I/O 异常，抛出 IOException 异常
     */
    <T> byte[] serialize(T obj)throws IOException;
    /**
     * 将字节数组反序列化为对象
     *
     * @param data 待反序列化的字节数组
     * @param clz  反序列化后的对象类型
     * @return 反序列化后的对象
     * @throws IOException 如果反序列化过程中发生 I/O 异常，抛出 IOException 异常
     */
    <T> T deserialize(byte[] data,Class<T> clz)throws IOException;
}
