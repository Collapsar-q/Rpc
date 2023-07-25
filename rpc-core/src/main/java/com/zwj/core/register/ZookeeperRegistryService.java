package com.zwj.core.register;

import com.zwj.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 16:57
 **/
@Slf4j
public class ZookeeperRegistryService implements RegistryService{
    /**
     * 连接Zookeeper的基础睡眠时间。
     */
    public static final int BASE_SLEEP_TIME_MS = 1000;
    /**
     * 连接Zookeeper失败后最大的重试次数。
     */
    public static final int MAX_RETRIES = 3;
    /**
     * 注册中心在Zookeeper中的根节点路径。
     */
    public static final String ZK_BASE_PATH = "/demo_rpc";
    /**
     * 使用Curator实现的服务发现对象。
     */
    private ServiceDiscovery<ServiceInfo> serviceDiscovery;



    /**
     * 构造函数，初始化连接Zookeeper并创建服务发现对象。
     *
     * @param registryAddr Zookeeper的地址，格式为：ip:port。
     */
    public ZookeeperRegistryService(String registryAddr) {
    try {
        // 创建CuratorFramework对象，并设置连接Zookeeper的参数。(连接Zookeeper的睡眠时间)
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        // 创建JsonInstanceSerializer对象，用于将ServiceInfo对象转换为JSON字符串。
        JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
        // 使用ServiceDiscoveryBuilder创建ServiceDiscovery对象，并设置相关参数。
        this.serviceDiscovery=ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }catch (Exception e){
        log.error("服务注册启动失败：{}",e);
    }
    }
    /**
     * 注册服务到Zookeeper中。
     *
     * @param serviceInfo 服务信息对象。
     * @throws Exception 如果注册失败，抛出异常。
     */
    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        // 创建ServiceInstance对象，包含服务信息和服务实例的相关信息。
        ServiceInstance<ServiceInfo> serviceInstance = ServiceInstance.<ServiceInfo>builder()
                .name(serviceInfo.getServiceName())
                .address(serviceInfo.getAddress())
                .port(serviceInfo.getPort())
                .payload(serviceInfo)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }
    /**
     * 将服务从Zookeeper中注销。
     *
     * @param serviceInfo 服务信息对象。
     * @throws Exception 如果注销失败，抛出异常。
     */
    @Override
    public void unRegister(ServiceInfo serviceInfo) throws Exception {
        ServiceInstance<ServiceInfo> serviceInstance = ServiceInstance.<ServiceInfo>builder()
                .name(serviceInfo.getServiceName())
                .port(serviceInfo.getPort())
                .address(serviceInfo.getAddress())
                .payload(serviceInfo)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }
    /**
     * 关闭服务发现。
     *
     * @throws IOException 如果关闭服务发现失败，抛出IOException异常。
     */
    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
