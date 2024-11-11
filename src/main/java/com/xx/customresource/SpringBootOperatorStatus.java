package com.xx.customresource;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;

import java.util.Date;

/**
 * @author xx
 */
public class SpringBootOperatorStatus extends ObservedGenerationAwareStatus {
    private Date lastModifyDate;


    public Date getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(Date lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }
}
