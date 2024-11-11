package com.xx.dependents;

import com.xx.customresource.SpringBootOperator;
import com.xx.utils.CommonUtils;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.xx.consts.CommonConst.*;
import static io.javaoperatorsdk.operator.ReconcilerUtils.loadYaml;

/**
 * deployment 资源
 * @author xx
 * @date 2024-11-07
 */
@KubernetesDependent
public class SpringBootDeploymentDependent extends CRUDKubernetesDependentResource<Deployment, SpringBootOperator> {

    private static final Logger log = LoggerFactory.getLogger(SpringBootDeploymentDependent.class);

    public SpringBootDeploymentDependent() {
        super(Deployment.class);
    }

    @Override
    protected Deployment desired(SpringBootOperator primary, Context<SpringBootOperator> context) {
        var name = primary.getMetadata().getName();
        var ns = primary.getMetadata().getNamespace();
        log.info("Replace resource type [deployment] [{}] from namespace [{}]", name, ns);
        Deployment deployment = loadYaml(Deployment.class, CommonUtils.class, "/com/xx/deployment.yml");
        deployment.getMetadata().setName(name);
        deployment.getMetadata().setNamespace(ns);
        deployment.getMetadata().getLabels().put(DEFAULT_APP_LABEL_KEY, name);
        deployment.getSpec().getTemplate().getMetadata().getLabels().put(DEFAULT_APP_LABEL_KEY, name);
        deployment.getSpec().getSelector().getMatchLabels().put(DEFAULT_APP_LABEL_KEY, name);
        // 替换镜像
        var image = primary.getSpec().getImage();
        if (ObjectUtils.isEmpty(image)) {
            image = Optional.ofNullable(primary.getSpec().getRegistry()).map(x -> x + "/").orElse("") + primary.getSpec().getEnv() + "/" + name + ":" + primary.getSpec().getVersion();
        }
        deployment.getSpec().setReplicas(primary.getSpec().getReplicas());
        deployment.getMetadata().getLabels().putAll(primary.getMetadata().getLabels());
        Map<String, String> annotations = primary.getMetadata().getAnnotations();
        DEPLOYMENT_LABELS_IGNORE_KEYS.forEach(annotations::remove);
        deployment.getMetadata().getAnnotations().putAll(annotations);
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setImage(image);
        // env 添加
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getEnv().addAll(primary.getSpec().getContainerEnv());
        // 端口开放
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setPorts(primary.getSpec().getPorts());
        // 处理 skywalking
        pickSkywalking(primary, deployment);
        // 处理 jvm 参数
        String jvmOpts = DEFAULT_JVM_OPTIONS_VALUE +
                Optional.ofNullable(primary.getSpec().getEnv()).map(" -Dspring.profiles.active="::concat).orElse("") +
                (null != primary.getSpec().getSkywalking() ? " -javaagent:/opt/agent/skywalking-agent.jar " : " ")
                + Optional.ofNullable(primary.getSpec().getJvmOptions()).orElse("");
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getEnv().add(new EnvVar(DEFAULT_JVM_OPTIONS_KEY, jvmOpts, null));

        return deployment;
    }

    private void pickSkywalking(SpringBootOperator primary, Deployment deployment) {
        if (null == primary.getSpec().getSkywalking()) {
            List<Container> initContainers = deployment.getSpec().getTemplate().getSpec().getInitContainers();
            if (ObjectUtils.isNotEmpty(initContainers)) {
                List<Container> cts = initContainers.stream().filter(x -> !"sw-agent".equals(x.getName())).collect(Collectors.toList());
                deployment.getSpec().getTemplate().getSpec().setInitContainers(cts);
            }
            return;
        }
        // 添加 sw 相关环境变量, 具体参数可以参考官方文档, 这里只配置应用名和命名空间
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getEnv().add(new EnvVar("SW_AGENT_COLLECTOR_BACKEND_SERVICES", primary.getSpec().getSkywalking().getServiceAddress(), null));
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getEnv().add(new EnvVar("SW_AGENT_NAME", primary.getMetadata().getName(), null));
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getEnv().add(new EnvVar("SW_AGENT_NAMESPACE", primary.getSpec().getSkywalking().getNamespace(), null));

    }
}
