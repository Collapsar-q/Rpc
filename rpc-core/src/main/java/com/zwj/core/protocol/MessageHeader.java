package com.zwj.core.protocol;

import com.zwj.core.serialization.SerializationTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/7 21:21
 **/
@Data
public class MessageHeader implements Serializable {
     /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    */
    //魔数
    private short magic;
    //协议版本号
    private byte version;
    //序列化算法
    private byte serialization;
    //报文类型
    private byte msgType;
    //状态
    private byte status;
    //消息ID
    private String requestId;
    //数据长度
    private int msgLen;
    /**
     * 构建消息头
     *
     * @param serialization 序列化算法
     * @return 构建的消息头对象
     */
    public static MessageHeader build(String serialization){
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setMagic(ProtocolConstants.MAGIC);
        messageHeader.setVersion(ProtocolConstants.VERSION);
        messageHeader.setRequestId(UUID.randomUUID().toString().replaceAll("-",""));
        messageHeader.setMsgType(MsgType.REQUEST.getType());
        messageHeader.setSerialization(SerializationTypeEnum.parseByName(serialization).getType());
        return messageHeader;
    }


}
