package com.zwj.core.balancer;

import com.zwj.core.common.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/8 20:45
 **/
public class FullRoundBalance implements LoadBalance{
    //创建了一个名为 logger 的静态日志记录器，用于在代码中输出日志。
    private static Logger logger= LoggerFactory.getLogger(FullRoundBalance.class);
    //记录当前选择的服务的索引。
    private int index;
    @Override
    public synchronized ServiceInfo chooseOne(List<ServiceInfo> serviceList) {
        // 加锁防止多线程情况下，index超出services.size()
        if (index>=serviceList.size()){
            index=0;
        }
        return serviceList.get(index++);
    }
}
