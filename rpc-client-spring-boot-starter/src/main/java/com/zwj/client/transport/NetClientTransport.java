package com.zwj.client.transport;

import com.zwj.core.common.RpcResponse;
import com.zwj.core.protocol.MessageProtocol;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/9 22:44
 **/
public interface NetClientTransport {
    //发送数据
    MessageProtocol<RpcResponse> sendRequest(RequestMetadata metadata)throws Exception;
}
