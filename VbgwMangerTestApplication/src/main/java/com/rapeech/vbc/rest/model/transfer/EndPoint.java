package com.rapeech.vbc.rest.model.transfer;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class EndPoint implements Serializable {
    private static final long serialVersionUID = -6737226004757228571L;

    @SerializedName("PBX")
    private String pbx;

    @SerializedName("SBC")
    private String sbc;
}
