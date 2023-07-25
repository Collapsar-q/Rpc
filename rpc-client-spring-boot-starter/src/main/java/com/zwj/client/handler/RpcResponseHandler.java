package com.zwj.client.handler;

import com.zwj.client.cache.LocalRpcResponseCache;
import com.zwj.core.common.RpcResponse;
import com.zwj.core.protocol.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/9 23:08
 **/
public class RpcResponseHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcResponse> rpcResponseMessageProtocol) throws Exception {
        String requestId = rpcResponseMessageProtocol.getMessageHeader().getRequestId();
        // 收到响应 设置响应数据
        LocalRpcResponseCache.fillResponse(requestId,rpcResponseMessageProtocol);
    }
}
