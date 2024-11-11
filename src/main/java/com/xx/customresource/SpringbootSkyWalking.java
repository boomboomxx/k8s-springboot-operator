package com.xx.customresource;

import static com.xx.consts.CommonConst.SKY_WALKING_DEFAULT_NAMESPACE;

/**
 * @author xx
 * @date 2024-11-07
 */
public class SpringbootSkyWalking {
    private boolean enable = false;

    private String serviceAddress;

    private String namespace = SKY_WALKING_DEFAULT_NAMESPACE;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
