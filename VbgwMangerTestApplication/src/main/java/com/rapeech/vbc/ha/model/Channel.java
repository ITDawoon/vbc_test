package com.rapeech.vbc.ha.model;

import com.rapeech.vbc.rest.model.transfer.ScaRegiInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class Channel implements Serializable {
    private static final long serialVersionUID = 8359448269995835723L;

    private String primary;
    private String secondary;
    private String tenantId;
    private int direction;
    private int status;
    private long channelNo;
    private int regiStatus;
    private String userName;
    private ScaRegiInfo scaRegiInfo;
}
