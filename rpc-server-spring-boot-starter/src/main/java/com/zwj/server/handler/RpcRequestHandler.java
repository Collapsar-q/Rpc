package com.zwj.server.handler;

import com.zwj.core.common.RpcRequest;
import com.zwj.core.common.RpcResponse;
import com.zwj.core.protocol.MessageHeader;
import com.zwj.core.protocol.MessageProtocol;
import com.zwj.core.protocol.MsgStatus;
import com.zwj.core.protocol.MsgType;
import com.zwj.server.store.LocalServerCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/8 19:07
 **/
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcRequest>> {
    //10：核心线程数,10：最大线程数,60L：线程的空闲时间,工作队列。使用ArrayBlockingQueue作为线程池的阻塞队列，最大容量为10000。当线程池无法立即执行新任务时，新任务将被放置在工作队列中等待执行。
    private final ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(10,10,60L, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10000));
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcRequest> rpcRequestMessageProtocol) throws Exception {
        threadPoolExecutor.submit(()->{
            MessageProtocol<RpcResponse> messageProtocol = new MessageProtocol<>();
            RpcResponse rpcResponse = new RpcResponse();
            MessageHeader header = rpcRequestMessageProtocol.getMessageHeader();
            //设置消息头部消息类型为响应
            header.setMsgType(MsgType.RESPONSE.getType());
            try{
                Object header1 = header(rpcRequestMessageProtocol.getBody());
                rpcResponse.setData(header1);
                header.setStatus(MsgStatus.SUCCESS.getCode());
                messageProtocol.setMessageHeader(header);
                messageProtocol.setBody(rpcResponse);
            }catch (Throwable throwable){
                header.setStatus(MsgStatus.FAIL.getCode());
                rpcResponse.setMessage(throwable.toString());
                log.error("请求进程错误{}",header.getRequestId(),throwable);
            }
            //把数据写回去
            channelHandlerContext.writeAndFlush(messageProtocol);
        });
    }
    private Object header(RpcRequest rpcRequest){
        try{
            Object bean = LocalServerCache.get(rpcRequest.getServiceName());
            if (bean==null){
                throw new RuntimeException(String.format("服务不存在：%s",rpcRequest.getServiceName()));
            }
            //反射调用
            Method method = bean.getClass().getMethod(rpcRequest.getMethod(), rpcRequest.getParameterTypes());
            return method.invoke(bean,rpcRequest.getParameters());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
