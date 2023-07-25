package com.zwj.client.proxy;

import com.zwj.client.config.RpcClientProperties;
import com.zwj.client.transport.NetClientTransport;
import com.zwj.client.transport.NetClientTransportFactory;
import com.zwj.client.transport.RequestMetadata;
import com.zwj.core.common.RpcRequest;
import com.zwj.core.common.RpcResponse;
import com.zwj.core.common.ServiceInfo;
import com.zwj.core.common.ServiceUtil;
import com.zwj.core.discovery.DiscoveryService;
import com.zwj.core.exception.ResourceNotFoundException;
import com.zwj.core.exception.RpcException;
import com.zwj.core.protocol.MessageHeader;
import com.zwj.core.protocol.MessageProtocol;
import com.zwj.core.protocol.MsgStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/9 20:48
 **/
@Slf4j
public class ClientStubInvocationHandler implements InvocationHandler {
    //服务发现
    private DiscoveryService discoveryService;
    // RPC客户端配置属性
    private RpcClientProperties properties;
    //被代理接口的Class对象
    private Class<?> calzz;
    //服务版本号
    private String version;

    public ClientStubInvocationHandler(DiscoveryService discoveryService, RpcClientProperties properties, Class<?> calzz, String version) {
        super();
        this.discoveryService = discoveryService;
        this.properties = properties;
        this.calzz = calzz;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1、获取服务信息
        ServiceInfo serviceInfo = discoveryService.discovery(ServiceUtil.serviceKey(this.calzz.getName(), this.version));
        if (serviceInfo==null){
            throw new ResourceNotFoundException("404");
        }
        MessageProtocol<RpcRequest> messageProtocol = new MessageProtocol<>();
        // 设置请求头
        messageProtocol.setMessageHeader(MessageHeader.build(properties.getSerialization()));
        //设置请求体
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(ServiceUtil.serviceKey(this.calzz.getName(), this.version));
        rpcRequest.setMethod(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);
        messageProtocol.setBody(rpcRequest);
        //发送网络请求
        MessageProtocol<RpcResponse> responseMessageProtocol = NetClientTransportFactory.getNetClientTransport()
                .sendRequest(RequestMetadata.builder().protocol(messageProtocol).address(serviceInfo.getAddress())
                .port(serviceInfo.getPort()).timeout(properties.getTimeout()).build());
        if (responseMessageProtocol==null){
            log.error("请求超时");
            throw new RpcException("RPC结果调用失败，请求超时"+properties.getTimeout());
        }
        if (!MsgStatus.isSuccess(responseMessageProtocol.getMessageHeader().getStatus())) {
            log.error("rpc调用结果失败， message：{}", responseMessageProtocol.getBody().getMessage());
            throw new RpcException(responseMessageProtocol.getBody().getMessage());
        }
        return responseMessageProtocol.getBody().getData();
    }
}
