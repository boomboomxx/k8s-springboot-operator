package com.xx.consts;

import java.util.List;

/**
 * @author xx
 * @date 2024-11-08
 */
public interface CommonConst {

    /**
     * 默认镜像仓库地址
     */
    String KUBERNETES_DEFAULT_IMAGE_REGISTRY = "harbor.szistech.com";
    /**
     * 默认 jvm 参数 的key
     */
    String DEFAULT_JVM_OPTIONS_KEY = "JVM_OPTS";
    /**
     * 默认 jvm 参数 的value
     * <br/>
     * JDK 17+ 需要 add-opens 参数打开反射相关
     */
    String DEFAULT_JVM_OPTIONS_VALUE = """
            -XX:+UnlockDiagnosticVMOptions
            -XX:+UnlockExperimentalVMOptions
            -XX:-OmitStackTraceInFastThrow
            -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom
            -Dnetworkaddress.cache.ttl=10
            -XX:MaxRAMPercentage=60
            -XX:InitialRAMPercentage=60
            -XX:+AlwaysPreTouch
            -Xss1024k
            -XX:+UseZGC
            -XX:+HeapDumpOnOutOfMemoryError
            -XX:HeapDumpPath=/tmp
            --add-opens=java.base/java.lang=ALL-UNNAMED
            --add-opens=java.base/java.io=ALL-UNNAMED
            --add-opens=java.base/java.math=ALL-UNNAMED
            --add-opens=java.base/java.net=ALL-UNNAMED
            --add-opens=java.base/java.nio=ALL-UNNAMED
            --add-opens=java.base/java.security=ALL-UNNAMED
            --add-opens=java.base/java.text=ALL-UNNAMED
            --add-opens=java.base/java.time=ALL-UNNAMED
            --add-opens=java.base/java.util=ALL-UNNAMED
            --add-opens=java.base/jdk.internal.access=ALL-UNNAMED
            --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED
            """;
    String DEFAULT_APP_LABEL_KEY = "app";

    /**
     * 默认 deployment 需要移除的 label键
     */
    List<String> DEPLOYMENT_LABELS_IGNORE_KEYS = List.of("kubectl.kubernetes.io/last-applied-configuration");

    /**
     * sky walking 默认命名空间
     */
    String SKY_WALKING_DEFAULT_NAMESPACE = "default";

}
