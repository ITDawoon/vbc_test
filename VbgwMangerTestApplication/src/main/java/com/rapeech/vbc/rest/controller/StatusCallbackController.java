package com.rapeech.vbc.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.spring.web.json.Json;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.rapeech.vbc.ha.daemon.VBCSchedular;
import com.rapeech.vbc.rest.model.StatusCallbackRequest;
import com.rapeech.vbc.rest.model.StatusCallbackResponse;
import com.rapeech.vbc.rest.service.StatusCallbackService;

@RestController
@Api(tags = "Status Callback")
public class StatusCallbackController {

    private static final Logger logger = LoggerFactory.getLogger(StatusCallbackController.class);

    

    @ApiOperation(value = "Send Status Callback", notes = "Send status callback for POST requests")
    @RequestMapping(value = "/status/callback", method = RequestMethod.POST)
    public StatusCallbackRequest sendStatusCallback(@RequestBody StatusCallbackRequest request) {
        // Log request
        logger.info("Received status callback request: {}", request);

        // Process request and return
        return request;
    }

}
