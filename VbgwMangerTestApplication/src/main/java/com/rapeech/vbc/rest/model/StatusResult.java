package com.rapeech.vbc.rest.model;

public class StatusResult {
    String transcript;
    String confidence;
    String[] words;
    int startTime;
    int endTime;

    public String getTranscript() {
        return this.transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getConfidence() {
        return this.confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String[] getWords() {
        return this.words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
