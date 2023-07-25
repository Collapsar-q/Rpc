package com.zwj.server;

import com.zwj.core.common.ServiceInfo;
import com.zwj.core.common.ServiceUtil;
import com.zwj.core.register.RegistryService;
import com.zwj.server.annotation.RpcService;
import com.zwj.server.config.RpcServerProperties;
import com.zwj.server.store.LocalServerCache;
import com.zwj.server.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 15:52
 **/
@Slf4j
public class RpcServerProvider implements BeanPostProcessor, CommandLineRunner {
    //服务注册中心
    private RegistryService registryService;
    //RPC服务器的配置中心
    private RpcServerProperties properties;
    //RPC服务器的对象
    private RpcServer rpcServer;

    // 构造方法，用于注入依赖
    public RpcServerProvider(RegistryService registryService, RpcServerProperties properties, RpcServer rpcServer) {
        this.registryService = registryService;
        this.properties = properties;
        this.rpcServer = rpcServer;
    }
    /**
     * 启动RPC服务并处理请求。
     *
     * @param args 启动参数
     */
    @Override
    public void run(String... args) throws Exception {
        // 启动一个新线程来启动RPC服务器
    new Thread(()-> rpcServer.start(properties.getPort())).start();
        // 打印启动日志
        log.info(" rpc server :{} start, appName :{} , port :{}", rpcServer, properties.getAppName(), properties.getPort());
        // 注册JVM关闭钩子，用于在JVM关闭时从注册中心注销服务
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                registryService.destroy();
            }catch (Exception e){
                log.error("{}",e);
            }
        }));
    }

    /**
     * 所有bean 实例化之后处理
     * <p>
     * 暴露服务注册到注册中心
     * <p>
     * 容器启动后开启netty服务处理请求
     *
     * @param bean
     * @param beanName
     * @return 处理后的bean对象
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 判断bean对象是否被@RpcService注解标记
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            try {
                // 获取服务名和版本号
                String name = rpcService.interfaceType().getName();
                String version = rpcService.version();
                // 将服务存入本地缓存中
                LocalServerCache.store(ServiceUtil.serviceKey(name, version), bean);
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceName(ServiceUtil.serviceKey(name, version));
                serviceInfo.setPort(properties.getPort());
                serviceInfo.setAddress(InetAddress.getLocalHost().getHostAddress());
                serviceInfo.setAppName(properties.getAppName());
                registryService.register(serviceInfo);
            } catch (Exception e) {
               log.error("服务注册出错{}",e);
            }
        }
        return bean;
    }
}
