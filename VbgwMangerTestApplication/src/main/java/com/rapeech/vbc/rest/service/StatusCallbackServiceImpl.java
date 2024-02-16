package com.rapeech.vbc.rest.service;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rapeech.vbc.rest.model.StatusCallbackRequest;
import com.rapeech.vbc.rest.model.StatusCallbackResponse;

@Service
public class StatusCallbackServiceImpl extends BaseStructure implements StatusCallbackService{

    @Override
    public StatusCallbackResponse recvStatusCallback(StatusCallbackRequest request) {
        //StatusCallbackResponse result = new StatusCallbackResponse();
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'StatusCallbackResponse'");
    }

}
