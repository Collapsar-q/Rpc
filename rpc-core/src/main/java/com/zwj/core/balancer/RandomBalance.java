package com.zwj.core.balancer;

import com.zwj.core.common.ServiceInfo;

import java.util.List;
import java.util.Random;

/**
 * @Author: zwj
 * @Description: 随机访问
 * @DateTime: 2023/5/8 20:43
 **/
public class RandomBalance implements LoadBalance{
    private static Random random=new Random();
    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> serviceList) {
        return serviceList.get(random.nextInt(serviceList.size()));
    }
}
