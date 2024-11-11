package com.xx.customresource;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ServicePort;

import java.util.ArrayList;
import java.util.List;

import static com.xx.consts.CommonConst.KUBERNETES_DEFAULT_IMAGE_REGISTRY;

/**
 * @author xx
 */
public class SpringBootOperatorSpec {

    /**
     * JVM OPTS, 以 JVM_OPTS 的环境变量存在 <br>
     * JAVA_TOOL_OPTIONS 参数会对所有 java 进程生效, 所以最好不要使用 JAVA_TOOL_OPTIONS 作为单独的参数使用
     * 需要在 docker容器中配置启动命令 java $JVM_OPTS -jar xxx.jar
     */
    private String jvmOptions;
    /**
     * 镜像名称&版本,  image 和 version 二选一 <br>
     * 优先级最高
     */
    private String image;
    /**
     * 镜像仓库 {registry}/{env}/{metadata.name}:{version} 组成完整镜像名称 <br>
     * 优先级低于 image
     */
    private String registry = KUBERNETES_DEFAULT_IMAGE_REGISTRY;
    /**
     * spring 环境, 一般是 dev/test/prod
     * 同时不填写 image 的情况下, 会作为镜像的一部分组成镜像名称
     */
    private String env;
    /**
     * 镜像版本
     */
    private String version;
    /**
     * 容器 env
     */
    private List<EnvVar> containerEnv = new ArrayList<>();

    /**
     * 分片数
     */
    private Integer replicas = 1;

    /**
     * 容器端口信息
     */
    private List<ContainerPort> ports = new ArrayList<>();

    /**
     * 服务端口信息, 不填写的情况, 默认使用 ports 映射
     */
    private List<ServicePort> servicePorts = new ArrayList<>();
    /**
     * sw 相关配置, 如果开启了 sw的
     */
    private SpringbootSkyWalking skywalking;
    private SpringBootIngress ingress;

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SpringbootSkyWalking getSkywalking() {
        return skywalking;
    }

    public void setSkywalking(SpringbootSkyWalking skywalking) {
        this.skywalking = skywalking;
    }


    public List<ContainerPort> getPorts() {
        return ports;
    }

    public void setPorts(List<ContainerPort> ports) {
        this.ports = ports;
    }

    public List<EnvVar> getContainerEnv() {
        return containerEnv;
    }

    public void setContainerEnv(List<EnvVar> containerEnv) {
        this.containerEnv = containerEnv;
    }

    public List<ServicePort> getServicePorts() {
        return servicePorts;
    }

    public void setServicePorts(List<ServicePort> servicePorts) {
        this.servicePorts = servicePorts;
    }

    public SpringBootIngress getIngress() {
        return ingress;
    }

    public void setIngress(SpringBootIngress ingress) {
        this.ingress = ingress;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }
}
