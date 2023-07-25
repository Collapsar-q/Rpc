package com.zwj.server.transport;

import com.zwj.core.codec.RpcDecoder;
import com.zwj.core.codec.RpcEncoder;
import com.zwj.core.common.RpcRequest;
import com.zwj.server.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 20:49
 **/
@Slf4j
public class NettyRpcServer implements RpcServer{
    @Override
    public void start(int port) {
        // 创建用于接收连接的主线程池
        EventLoopGroup boss = new NioEventLoopGroup();
        // 创建用于处理连接的工作线程池
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            // 获取本地主机的IP地址
            String serverAddress = InetAddress.getLocalHost().getHostAddress();
            // 创建服务器启动引导类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //添加配置
            // 设置服务器的事件循环组，boss用于接收连接，worker用于处理连接
            bootstrap.group(boss,worker)
            // 指定通道类型为NIO服务器通道
             .channel(NioServerSocketChannel.class)
           //设置子处理器，用于处理接收到的连接的网络事件
            .childHandler(new ChannelInitializer<SocketChannel>(){

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 初始化SocketChannel的处理管道
                    socketChannel.pipeline()
                            //编码器
                            .addLast(new RpcEncoder())
                            //解码器
                            .addLast(new RpcDecoder())
                            // 添加请求处理器
                            .addLast(new RpcRequestHandler());
                    //设置TCP连接保活机制。ChannelOption.SO_KEEPALIVE表示开启TCP的Keep-Alive机制，保持长连接状态。
                }
            }).childOption(ChannelOption.SO_KEEPALIVE,true);
            // 绑定服务器地址和端口，并启动服务器
            ChannelFuture channelFuture = bootstrap.bind(serverAddress, port).sync();
            // 记录服务器启动的地址和端口信息
            log.info("server addr {} started on port {}", serverAddress, port);
            // 等待服务器关闭
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            // 发生异常时的处理
        }finally {
            // 优雅地关闭主线程池和工作线程池
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
