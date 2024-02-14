package com.rapeech.vbc.rest.packet;

public class Data {

    //230612 : "data" 로 들어오는 경우 파싱 목적으로 사용

    //230512 : 기존 전달 받던 사항 중 필요 내역추가

    private String calluuid ;
    private String tenantId;
    private String recStartTime ;
    private String recEndTime ;
    private String callStartTime ;
    private String callEndTime ;
    private String dnis ;
    private String currentTime ;
    private String ani ;
    private String botcode ;
    private String direction ;
    private String index ;
    private String transcript ;
    private String filteredTranscript ;
    private String checkedTranscript ;
    private String intentName ;
    private String parameters ;
    private String controlType ;
    private String value ;
    private String callType;
    private String channel ;
    private String status ;
    private String timestamp ;
    private String scenario ;
    private String dnum ;
    private String sessionId ;
    private String scaIp ;

    //230512 : centerCode는 형태 확립 시 변수명 변경 가능

    private String centerCode;


    //230512 : 이상징후가 하나로 합쳐지는 관계로 필요 사항 추가

    private String sentence ;
    private String date_ymd ;
    private String date_hh ;
    private String createdAt ;

    private String transfer_user ;		// 전환요청자ID
    private String transfer_type ;		// 전환요청구분
    private String transfer_target ;	// 전환대상고유키
    private String transfer_at ;		// 전환요청시각

    //230612 - V코드 관련해서 추가
    private String interfaceid;  //인터페이스 아이디

    private boolean isWarning;
    private String isWarningChar;

    public String getCalluuid() {
        return calluuid;
    }

    public void setCalluuid(String calluuid) {
        this.calluuid = calluuid;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRecStartTime() {
        return recStartTime;
    }

    public void setRecStartTime(String recStartTime) {
        this.recStartTime = recStartTime;
    }

    public String getRecEndTime() {
        return recEndTime;
    }

    public void setRecEndTime(String recEndTime) {
        this.recEndTime = recEndTime;
    }

    public String getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(String callStartTime) {
        this.callStartTime = callStartTime;
    }

    public String getCallEndTime() {
        return callEndTime;
    }

    public void setCallEndTime(String callEndTime) {
        this.callEndTime = callEndTime;
    }

    public String getDnis() {
        return dnis;
    }

    public void setDnis(String dnis) {
        this.dnis = dnis;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getAni() {
        return ani;
    }

    public void setAni(String ani) {
        this.ani = ani;
    }

    public String getBotcode() {
        return botcode;
    }

    public void setBotcode(String botcode) {
        this.botcode = botcode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getFilteredTranscript() {
        return filteredTranscript;
    }

    public void setFilteredTranscript(String filteredTranscript) {
        this.filteredTranscript = filteredTranscript;
    }

    public String getCheckedTranscript() {
        return checkedTranscript;
    }

    public void setCheckedTranscript(String checkedTranscript) {
        this.checkedTranscript = checkedTranscript;
    }

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getDnum() {
        return dnum;
    }

    public void setDnum(String dnum) {
        this.dnum = dnum;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getScaIp() {
        return scaIp;
    }

    public void setScaIp(String scaIp) {
        this.scaIp = scaIp;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getDate_ymd() {
        return date_ymd;
    }

    public void setDate_ymd(String date_ymd) {
        this.date_ymd = date_ymd;
    }

    public String getDate_hh() {
        return date_hh;
    }

    public void setDate_hh(String date_hh) {
        this.date_hh = date_hh;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTransfer_user() {
        return transfer_user;
    }

    public void setTransfer_user(String transfer_user) {
        this.transfer_user = transfer_user;
    }

    public String getTransfer_type() {
        return transfer_type;
    }

    public void setTransfer_type(String transfer_type) {
        this.transfer_type = transfer_type;
    }

    public String getTransfer_target() {
        return transfer_target;
    }

    public void setTransfer_target(String transfer_target) {
        this.transfer_target = transfer_target;
    }

    public String getTransfer_at() {
        return transfer_at;
    }

    public void setTransfer_at(String transfer_at) {
        this.transfer_at = transfer_at;
    }

    public String getInterfaceid() {
        return interfaceid;
    }

    public void setInterfaceid(String interfaceid) {
        this.interfaceid = interfaceid;
    }

    public boolean getIsWarning() {
        return isWarning;
    }

    public void setIsWarning(boolean isWarning) {
        this.isWarning = isWarning;
    }
}
