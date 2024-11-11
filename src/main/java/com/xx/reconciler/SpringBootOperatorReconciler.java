package com.xx.reconciler;

import com.xx.conditions.IngressCondition;
import com.xx.customresource.SpringBootOperator;
import com.xx.customresource.SpringBootOperatorStatus;
import com.xx.dependents.SpringBootDeploymentDependent;
import com.xx.dependents.SpringBootIngressDependent;
import com.xx.dependents.SpringBootServiceDependent;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author xx
 */
@ControllerConfiguration(dependents = {
        @Dependent(type = SpringBootDeploymentDependent.class),
        @Dependent(type = SpringBootServiceDependent.class),
        @Dependent(type = SpringBootIngressDependent.class, reconcilePrecondition = IngressCondition.class)
})
public class SpringBootOperatorReconciler implements
        Reconciler<SpringBootOperator>, Cleaner<SpringBootOperator> {

    private static final Logger log = LoggerFactory.getLogger(SpringBootOperatorReconciler.class);


    public UpdateControl<SpringBootOperator> reconcile(SpringBootOperator primary,
                                                       Context<SpringBootOperator> context) {
        String name = primary.getMetadata().getName();
        String ns = primary.getMetadata().getNamespace();

        SpringBootOperatorStatus status = new SpringBootOperatorStatus();
        status.setLastModifyDate(new Date());
        primary.setStatus(status);
        return UpdateControl.patchStatus(primary);
    }

    @Override
    public DeleteControl cleanup(SpringBootOperator resource, Context<SpringBootOperator> context) {
        log.info("delete resource {} from namespace {}", resource.getMetadata().getName(), resource.getMetadata().getNamespace());
        return DeleteControl.defaultDelete();
    }
}
