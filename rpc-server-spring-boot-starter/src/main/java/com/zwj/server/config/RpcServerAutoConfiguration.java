package com.zwj.server.config;

import com.zwj.core.register.RegistryService;
import com.zwj.core.register.ZookeeperRegistryService;
import com.zwj.server.RpcServerProvider;
import com.zwj.server.transport.NettyRpcServer;
import com.zwj.server.transport.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/5 20:43
 **/
//启用对RpcServerProperties类的配置属性支持，将其加载到Spring容器中。
@EnableConfigurationProperties(RpcServerProperties.class)
@Configuration
public class RpcServerAutoConfiguration {
    @Autowired
    private RpcServerProperties properties;

    @Bean
    @ConditionalOnMissingBean//当容器中不存在某个特定类型的 Bean 时才创建该 Bean。
    public RegistryService registryService() {
        return new ZookeeperRegistryService(properties.getRegistryAddr());
    }

    @Bean
    @ConditionalOnMissingBean(RpcServer.class)//如果容器中已经存在RpcServer类型的Bean，则不会创建
    public RpcServer RpcServer() {
        return new NettyRpcServer();
    }

    @Bean
    @ConditionalOnMissingBean(RpcServerProvider.class)
    public RpcServerProvider rpcServerProvider(@Autowired RegistryService registryService,
                                               @Autowired RpcServer rpcServer,
                                               @Autowired RpcServerProperties rpcServerProperties){
        return new RpcServerProvider(registryService, rpcServerProperties, rpcServer);
    }
}
