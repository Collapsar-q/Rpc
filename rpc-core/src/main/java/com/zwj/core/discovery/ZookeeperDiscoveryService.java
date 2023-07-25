package com.zwj.core.discovery;

import com.zwj.core.balancer.LoadBalance;
import com.zwj.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/9 21:00
 **/
@Slf4j
public class ZookeeperDiscoveryService implements DiscoveryService{
    //基本睡眠时间
    public static final int BASE_SLEEP_TIME_MS = 1000;
    //最大重连次数
    public static final int  MAX_RETRIES = 3;
    //服务器注册的目录
    public static final String ZK_BASE_PATH = "/demo_rpc";
    /**
     * 使用Curator实现的服务发现对象。
     */
    private ServiceDiscovery<ServiceInfo> serviceDiscovery;
    //负载均衡算法
    private LoadBalance loadBalance;

    public ZookeeperDiscoveryService(String registryAddress, LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
        try {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddress, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
        this.serviceDiscovery= ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                .serializer(serializer)
                .client(client)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
        }catch (Exception e){
            log.error("服务发现启动失败{}",e);
        }
    }
    /**
     *  服务发现
     * @param serviceName
     * @return
     * @throws Exception
     */
    @Override
    public ServiceInfo discovery(String serviceName) throws Exception {
        //serviceDiscovery对象的queryForInstances方法查询给定服务名称的所有服务实例，并将结果存储在serviceInstances变量中
        Collection<ServiceInstance<ServiceInfo>> serviceInfoServiceInstance = serviceDiscovery.queryForInstances(serviceName);
        //查询实例是否为空，不是为空采用负载均衡算法选出一个
        return CollectionUtils.isEmpty(serviceInfoServiceInstance)?null:
                loadBalance.chooseOne(serviceInfoServiceInstance.stream().map(ServiceInstance::getPayload).collect(Collectors.toList()));
    }
}
