package com.xx.customresource;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("com.xx")
@Version("v1")
@Kind("SpringBoot")
@Plural("SpringBoots")
public class SpringBootOperator extends CustomResource<SpringBootOperatorSpec, SpringBootOperatorStatus> implements Namespaced {
}
