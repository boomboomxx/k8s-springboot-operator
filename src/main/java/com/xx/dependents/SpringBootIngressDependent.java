package com.xx.dependents;

import com.xx.customresource.SpringBootOperator;
import com.xx.utils.CommonUtils;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.javaoperatorsdk.operator.ReconcilerUtils.loadYaml;

/**
 * ingress 资源更新
 *
 * @author xx
 * @date 2024-11-07
 */
@KubernetesDependent
public class SpringBootIngressDependent extends CRUDKubernetesDependentResource<Ingress, SpringBootOperator> {
    private static final Logger log = LoggerFactory.getLogger(SpringBootIngressDependent.class);

    public SpringBootIngressDependent() {
        super(Ingress.class);
    }

    @Override
    protected Ingress desired(SpringBootOperator primary, Context<SpringBootOperator> context) {
        Ingress ingress = loadYaml(Ingress.class, CommonUtils.class, "/com/xx/ingress.yml");
        var name = primary.getMetadata().getName();
        var ns = primary.getMetadata().getNamespace();
        log.info("Replace resource type [ingress] [{}] from namespace [{}]", name, ns);

        ingress.getMetadata().setName(name);
        ingress.getMetadata().setNamespace(ns);
        ingress.getMetadata().getLabels().putAll(primary.getSpec().getIngress().getLabels());
        ingress.getMetadata().getAnnotations().putAll(primary.getSpec().getIngress().getAnnotations());
        ingress.getSpec().setIngressClassName(primary.getSpec().getIngress().getIngressClassName());
        ingress.getSpec().setRules(primary.getSpec().getIngress().getRules());
        return ingress;
    }
}
