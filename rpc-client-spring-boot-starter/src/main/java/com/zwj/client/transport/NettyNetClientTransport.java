package com.zwj.client.transport;

import com.zwj.client.cache.LocalRpcResponseCache;
import com.zwj.client.handler.RpcResponseHandler;
import com.zwj.core.codec.RpcDecoder;
import com.zwj.core.codec.RpcEncoder;
import com.zwj.core.common.RpcRequest;
import com.zwj.core.common.RpcResponse;
import com.zwj.core.protocol.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/9 22:43
 **/
@Slf4j
public class NettyNetClientTransport implements NetClientTransport {
    //创建TCP连接
    private final Bootstrap bootstrap;
    //事件循环组
    private final EventLoopGroup eventLoopGroup;
    //处理响应
    private final RpcResponseHandler handler;

    public NettyNetClientTransport() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        //bootstrap对象配置了NIO Socket通道和处理器。
        handler=new RpcResponseHandler();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                //解码将二进制解码成消息
                            .addLast(new RpcDecoder())
                                //接受响应
                            .addLast(handler)
                                //编码出站将消息编写二进制
                                .addLast(new RpcEncoder<>());
                    }
                });

    }

    @Override
    public MessageProtocol<RpcResponse> sendRequest(RequestMetadata metadata) throws Exception {
        MessageProtocol<RpcRequest> protocol = metadata.getProtocol();
        RpcFuture<MessageProtocol<RpcResponse>> future = new RpcFuture<>();
        // 将请求的唯一标识和对应的RpcFuture对象添加到本地缓存中
        LocalRpcResponseCache.add(protocol.getMessageHeader().getRequestId(),future);
        // 建立TCP连接
        ChannelFuture channelFuture = bootstrap.connect(metadata.getAddress(), metadata.getPort()).sync();
        //注册一个监听器，监听器是一个匿名类实现了ChannelFutureListener接口。
        channelFuture.addListener((ChannelFutureListener) arg0 -> {
            if (channelFuture.isSuccess()){
                log.info("connect rpc server {} on port {} success.", metadata.getAddress(), metadata.getPort());
            }else {
                log.error("connect rpc server {} on port {} failed.", metadata.getAddress(), metadata.getPort());
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }

        });
        // 写入数据channelFuture.channel()返回与连接关联的通道channel。
        //writeAndFlush(protocol)将请求数据protocol写入通道并刷新。
        channelFuture.channel().writeAndFlush(protocol);
        //如果metadata.getTimeout()不为null，说明设置了超时时间，使用future.get(metadata.getTimeout(), TimeUnit.MILLISECONDS)等待并获取响应结果，等待时间为超时时间。
        //如果metadata.getTimeout()为null，即没有设置超时时间，使用future.get()等待并获取响应结果，此时会一直等待直到有响应返回或线程被中断。
        return metadata.getTimeout()!=null ? future.get(metadata.getTimeout(), TimeUnit.MILLISECONDS) : future.get();
    }


}
