package com.zwj.core.discovery;

import com.zwj.core.common.ServiceInfo;

public interface DiscoveryService {

    /**
     *  发现
     * @param serviceName
     * @return
     * @throws Exception
     */
    ServiceInfo discovery(String serviceName) throws Exception;

}
