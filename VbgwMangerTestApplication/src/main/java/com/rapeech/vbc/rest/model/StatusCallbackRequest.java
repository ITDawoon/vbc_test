package com.rapeech.vbc.rest.model;

public class StatusCallbackRequest {
    String requestResult;
    String requestMessage;
    String status;

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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}