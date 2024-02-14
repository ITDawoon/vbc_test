package com.rapeech.vbc.rest.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RequestScaUnregist implements Serializable {
    private static final long serialVersionUID = 7663347458135162015L;

    private String trId;
    private Long channelId;
}
