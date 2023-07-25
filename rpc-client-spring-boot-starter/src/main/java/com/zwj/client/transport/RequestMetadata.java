package com.zwj.client.transport;

import com.zwj.core.common.RpcRequest;
import com.zwj.core.protocol.MessageProtocol;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zwj
 * @Description: 请求元数据
 * @DateTime: 2023/5/9 22:49
 **/
@Data
@Builder
public class RequestMetadata implements Serializable {
    //协议
    private MessageProtocol<RpcRequest> protocol;
    //地址
    private String address;
    //端口
    private Integer port;
    //服务调用超时
    private Integer timeout;
}
