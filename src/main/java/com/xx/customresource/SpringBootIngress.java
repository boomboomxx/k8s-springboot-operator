package com.xx.customresource;



import io.fabric8.kubernetes.api.model.networking.v1.IngressRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xx
 * @date 2024-11-07
 */
public class SpringBootIngress {
    private List<IngressRule> rules;
    private String ingressClassName = "nginx";
    private Map<String, String> annotations = new LinkedHashMap<>();
    private Map<String, String> labels = new LinkedHashMap<>();

    public List<IngressRule> getRules() {
        return rules;
    }

    public void setRules(List<IngressRule> rules) {
        this.rules = rules;
    }

    public String getIngressClassName() {
        return ingressClassName;
    }

    public void setIngressClassName(String ingressClassName) {
        this.ingressClassName = ingressClassName;
    }

    public Map<String, String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }
}
