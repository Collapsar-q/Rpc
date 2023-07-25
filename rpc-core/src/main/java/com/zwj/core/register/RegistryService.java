package com.zwj.core.register;

import com.zwj.core.common.ServiceInfo;

import java.io.IOException;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 16:16
 **/
public interface RegistryService {
    //注册
    void register(ServiceInfo serviceInfo)throws Exception;
    void unRegister(ServiceInfo serviceInfo)throws Exception;
    //销毁
    void destroy()throws IOException;
}
