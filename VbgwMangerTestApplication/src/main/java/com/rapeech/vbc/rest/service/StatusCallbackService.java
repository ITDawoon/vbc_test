package com.rapeech.vbc.rest.service;

import com.rapeech.vbc.rest.model.StatusCallbackRequest;
import com.rapeech.vbc.rest.model.StatusCallbackResponse;

public interface StatusCallbackService {
    public StatusCallbackResponse recvStatusCallback(StatusCallbackRequest request);
}
