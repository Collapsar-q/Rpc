package com.zwj.core.balancer;

import com.zwj.core.common.ServiceInfo;

import java.util.List;

/**
 * @Author: zwj
 * @Description: 负载均衡的算法
 * @DateTime: 2023/5/8 20:27
 **/
public interface LoadBalance {
    ServiceInfo chooseOne(List<ServiceInfo> serviceList);
}
