package com.rapeech.vbc.rest.model.response;

import com.rapeech.vbc.rest.common.model.ResponseCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseScaRegist extends ResponseCommon implements Serializable {
    private static final long serialVersionUID = -4851362517799713010L;

    List<RegistResult> results;
}
