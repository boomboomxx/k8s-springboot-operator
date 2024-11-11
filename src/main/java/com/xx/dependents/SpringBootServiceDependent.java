package com.xx.dependents;

import com.xx.customresource.SpringBootOperator;
import com.xx.utils.CommonUtils;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.xx.consts.CommonConst.DEFAULT_APP_LABEL_KEY;
import static io.javaoperatorsdk.operator.ReconcilerUtils.loadYaml;

/**
 * @author xx
 * @date 2024-11-07
 */
@KubernetesDependent
public class SpringBootServiceDependent extends CRUDKubernetesDependentResource<Service, SpringBootOperator> {

    private static final Logger log = LoggerFactory.getLogger(SpringBootServiceDependent.class);

    public SpringBootServiceDependent() {
        super(Service.class);
    }

    @Override
    protected Service desired(SpringBootOperator primary, Context<SpringBootOperator> context) {

        var name = primary.getMetadata().getName();
        var ns = primary.getMetadata().getNamespace();
        log.info("Replace resource type [service]  [{}] from namespace [{}]", name, ns);

        Service service = loadYaml(Service.class, CommonUtils.class, "/com/xx/service.yml");
        List<ServicePort> servicePorts = primary.getSpec().getServicePorts();
        if (ObjectUtils.isEmpty(servicePorts)) {
            servicePorts = primary.getSpec().getPorts()
                    .stream()
                    .map(x -> new ServicePort(null, x.getName(), null, x.getContainerPort(), x.getProtocol(), new IntOrString(x.getContainerPort())))
                    .toList();
        }
        service.getMetadata().setName(name);
        service.getMetadata().setNamespace(ns);
        service.getMetadata().getAnnotations().putAll(primary.getSpec().getIngress().getAnnotations());
        service.getMetadata().getLabels().putAll(primary.getSpec().getIngress().getLabels());

        service.getSpec().setType("ClusterIP");
        service.getSpec().getSelector().put(DEFAULT_APP_LABEL_KEY, name);
        service.getSpec().setPorts(servicePorts);

        return service;
    }
}
