package com.zwj.core.serialization;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/7 23:40
 **/
public class SerializationFactory {
    /**
     * 根据序列化类型枚举值获取对应的 RpcSerialization 实例
     *
     * @param typeEnum 序列化类型枚举值
     * @return RpcSerialization 实例
     * @throws IllegalArgumentException 如果序列化类型不合法，抛出 IllegalArgumentException 异常
     */
    public static RpcSerialization getRpcSerialization(SerializationTypeEnum typeEnum){
        switch (typeEnum){
            case HESSIAN:
                return new HessianSerialization();
            case JSON:
                return new JsonSerialization();
            default:
                throw  new IllegalArgumentException("serialization type is illegal");
        }
    }
}
