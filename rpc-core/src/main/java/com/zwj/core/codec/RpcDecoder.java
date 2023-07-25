package com.zwj.core.codec;

import com.zwj.core.common.RpcRequest;
import com.zwj.core.common.RpcResponse;
import com.zwj.core.protocol.MessageHeader;
import com.zwj.core.protocol.MessageProtocol;
import com.zwj.core.protocol.MsgType;
import com.zwj.core.protocol.ProtocolConstants;
import com.zwj.core.serialization.RpcSerialization;
import com.zwj.core.serialization.SerializationFactory;
import com.zwj.core.serialization.SerializationTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Author: zwj
 * @Description: 解码器
 * @DateTime: 2023/5/7 21:15
 **/
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * +---------------------------------------------------------------+
     * | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte|
     * +---------------------------------------------------------------+
     * | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
     * +---------------------------------------------------------------+
     * |                   数据内容 （长度不定）                         |
     * +---------------------------------------------------------------+
     * <p>
     * decode 这个方法会被循环调用
     *
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            //可读数据小于请求头的大小 直接丢弃
            return;
        }
        //标记指针位置
        byteBuf.markReaderIndex();
        //魔数
        short magic = byteBuf.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }
        byte version = byteBuf.readByte();
        byte serializeType = byteBuf.readByte();
        byte msgType = byteBuf.readByte();
        byte status = byteBuf.readByte();
        CharSequence requestId = byteBuf.readCharSequence(ProtocolConstants.REQ_LEN, Charset.forName("UTF-8"));
        int length = byteBuf.readInt();
        if (byteBuf.readableBytes() < length) {
            //可读数据小于请求体数据长度直接丢弃重置回读指针位置
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setMagic(magic);
        messageHeader.setSerialization(serializeType);
        messageHeader.setVersion(version);
        messageHeader.setMsgType(msgType);
        messageHeader.setStatus(status);
        messageHeader.setMsgLen(length);
        messageHeader.setRequestId(String.valueOf(requestId));
        RpcSerialization serialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(serializeType));
        switch (msgTypeEnum) {
            case REQUEST:
                RpcRequest request = serialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    MessageProtocol<RpcRequest> protocol = new MessageProtocol<>();
                    protocol.setMessageHeader(messageHeader);
                    protocol.setBody(request);
                    list.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = serialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    MessageProtocol<RpcResponse> protocol = new MessageProtocol<>();
                    protocol.setMessageHeader(messageHeader);
                    protocol.setBody(response);
                    list.add(protocol);
                }
                break;
        }


    }
}
