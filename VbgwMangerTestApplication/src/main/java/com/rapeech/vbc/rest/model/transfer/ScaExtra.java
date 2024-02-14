package com.rapeech.vbc.rest.model.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class ScaExtra implements Serializable {
    private static final long serialVersionUID = -671261928671756627L;

    @SerializedName("from_user")
    private String fromUser;

    @SerializedName("from_domain")
    private String fromDomain;

    @SerializedName("register_proxy")
    private String registerProxy;

    @SerializedName("register_transport")
    private String registerTransport;

    @SerializedName("register")
    private boolean register;

    @SerializedName("retry_seconds")
    private int retrySeconds;

    @JsonProperty("expire_seconds")
    private int expireSeconds;
}
