package com.xx.conditions;

import com.xx.customresource.SpringBootOperator;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition;

/**
 * @author xx
 * @date 2024-11-07
 */
public class IngressCondition implements Condition<Ingress, SpringBootOperator> {
    @Override
    public boolean isMet(DependentResource<Ingress, SpringBootOperator> dependentResource, SpringBootOperator primary, Context<SpringBootOperator> context) {
        return null != primary.getSpec().getIngress();
    }
}
