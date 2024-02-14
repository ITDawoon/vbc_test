package com.rapeech.vbc.rest.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RequestCallChannel implements Serializable {
    private static final long serialVersionUID = 8192660551811005605L;

    private String tenantId;
    private String ani;

    private String trId;
    private int inoutFlag;
}
