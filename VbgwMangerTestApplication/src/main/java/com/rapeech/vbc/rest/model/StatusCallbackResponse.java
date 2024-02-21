package com.rapeech.vbc.rest.model;

public class StatusCallbackResponse {
    String requestResult;
    String requestMessage;

    public String getRequestResult() {
        return this.requestResult;
    }

    public void setRequestResult(String requestResult) {
        this.requestResult = requestResult;
    }

    public String getRequestMessage() {
        return this.requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }
}