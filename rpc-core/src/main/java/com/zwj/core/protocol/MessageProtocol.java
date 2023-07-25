package com.zwj.core.protocol;



import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/7 21:20
 **/
@Data
public class MessageProtocol<T> implements Serializable {
    //请求头
    private MessageHeader messageHeader;
    //消息体
    private T body;
}
