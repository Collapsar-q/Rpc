package com.zwj.core.protocol;

import lombok.Getter;

public enum MsgType {
    // 请求消息类型，对应数值为 1
    REQUEST((byte) 1),
    // 响应消息类型，对应数值为 2
    RESPONSE((byte) 2);

    @Getter
    private byte type;

    MsgType(byte type) {
        this.type = type;
    }
    /**
     * 根据类型查找对应的消息类型枚举值
     *
     * @param type 消息类型
     * @return 对应的消息类型枚举值，如果未找到则返回 null
     */
    public static MsgType findByType(byte type) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        return null;
    }
}
