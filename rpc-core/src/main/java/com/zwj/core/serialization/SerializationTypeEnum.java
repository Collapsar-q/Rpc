package com.zwj.core.serialization;

import lombok.Getter;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/7 22:25
 **/
public enum SerializationTypeEnum {
    // Hessian 序列化类型，对应数值为 0
        HESSIAN((byte) 0),
        JSON((byte) 1);
        @Getter
        private byte type;
    SerializationTypeEnum(byte type) {
        this.type=type;
    }
    /**
     * 根据类型名称解析对应的序列化类型枚举值
     *
     * @param typeName 类型名称
     * @return 对应的序列化类型枚举值，如果未找到则返回默认的 HESSIAN 类型
     */
    public static SerializationTypeEnum parseByName(String typeName){
        for (SerializationTypeEnum typeEnum:SerializationTypeEnum.values()
             ) {
            //equalsIgnoreCase(typeName) 是一个字符串方法，用于比较字符串是否相等，不区分大小写。
            if (typeEnum.name().equalsIgnoreCase(typeName)){
                return typeEnum;
            }
        }
        return HESSIAN;
    }
    /**
     * 根据类型值解析对应的序列化类型枚举值
     *
     * @param type 类型值
     * @return 对应的序列化类型枚举值，如果未找到则返回默认的 HESSIAN 类型
     */
    public static SerializationTypeEnum parseByType(byte type){
        for (SerializationTypeEnum typeEnum: SerializationTypeEnum.values()
             ) {
            if (typeEnum.getType()==type){
                return typeEnum;
            }
        }
        return HESSIAN;
    }
}
