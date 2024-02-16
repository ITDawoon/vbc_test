package com.rapeech.vbc.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.spring.web.json.Json;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rapeech.vbc.rest.model.StatusCallbackRequest;
import com.rapeech.vbc.rest.model.StatusCallbackResponse;
import com.rapeech.vbc.rest.service.StatusCallbackService;


@RestController
@Api(tags = "Status Callback")
public class StatusCallbackController {

    //@Autowired
    //private StatusCallbackService service;

    @ApiOperation(value = "Send Status Callback", notes = "Send status callback for POST requests")
    @RequestMapping(value = "/status/callback", method = RequestMethod.POST)
    // public StatusCallbackResponse sendStatusCallback(@RequestBody(required = true) StatusCallbackRequest request) {

    //     StatusCallbackResponse result = new StatusCallbackResponse();
    //     //result = service.recvStatusCallback(request);

    //     return result;
    // }
    public StatusCallbackRequest sendStatusCallback(@RequestBody StatusCallbackRequest request) {

        return request;
    }

}
