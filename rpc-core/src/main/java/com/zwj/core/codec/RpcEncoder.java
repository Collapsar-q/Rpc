package com.zwj.core.codec;

import com.zwj.core.protocol.MessageHeader;
import com.zwj.core.protocol.MessageProtocol;
import com.zwj.core.serialization.RpcSerialization;
import com.zwj.core.serialization.SerializationFactory;
import com.zwj.core.serialization.SerializationTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @Author: zwj
 * @Description: 编码器
 * *
 *      *  +---------------------------------------------------------------+
 *      *  | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte|
 *      *  +---------------------------------------------------------------+
 *      *  | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte    |
 *      *  +---------------------------------------------------------------+
 *      *  |                   数据内容 （长度不定）                         |
 *      *  +---------------------------------------------------------------+
 *      *
 * @DateTime: 2023/5/7 21:15
 **/
@Slf4j
public class RpcEncoder<T> extends MessageToByteEncoder<MessageProtocol<T>> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageProtocol<T> messageProtocol, ByteBuf byteBuf) throws Exception {
        MessageHeader messageHeader = messageProtocol.getMessageHeader();
        // 魔数
        byteBuf.writeShort(messageHeader.getMagic());
        // 协议版本号
        byteBuf.writeByte(messageHeader.getVersion());
        // 序列化算法
        byteBuf.writeByte(messageHeader.getSerialization());
        // 报文类型
        byteBuf.writeByte(messageHeader.getMsgType());
        // 状态
        byteBuf.writeByte(messageHeader.getStatus());
        //消息ID
        byteBuf.writeCharSequence(messageHeader.getRequestId(), Charset.forName("UTF-8"));
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(messageHeader.getSerialization()));
        byte[] data = rpcSerialization.serialize(messageProtocol.getBody());
        //数据长度
        byteBuf.writeInt(data.length);
        //数据内容
        byteBuf.writeBytes(data);

    }
}
