package com.rapeech.vbc.rest.model.transfer;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ScaRegiInfo implements Serializable {
    private static final long serialVersionUID = 8948444487047471825L;

    @SerializedName("profile_name")
    private String profileName;

    private String username;

    private String realm;

    private String password;

    private String proxy;

    private ScaExtra extras;

    @SerializedName("endpoints")
    private List<EndPoint> endPoints;

    @SerializedName("tr_id")
    private String trId;
}
