package com.rapeech.vbc.rest.model.transfer;

import lombok.Data;

import java.io.Serializable;

@Data
public class OmsChannel implements Serializable {
    private static final long serialVersionUID = 5467988800950668173L;

    private String primary;
    private String secondary;
    private String tenantid;
    private int direction;
    private int status;
    private int channelno;
    private int registatus;
    private String username;
}
