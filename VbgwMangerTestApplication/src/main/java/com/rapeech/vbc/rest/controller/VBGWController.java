package com.rapeech.vbc.rest.controller;

import com.rapeech.vbc.ha.config.HaConfig;
import com.rapeech.vbc.ha.config.PropertiesConfig;
import com.rapeech.vbc.ha.model.Channel;
//import com.rapeech.vbc.kafka.KafkaProducerService; //20240130
import com.rapeech.vbc.logback.ErrorCode;
import com.rapeech.vbc.logback.VBCException;
import com.rapeech.vbc.rest.model.request.RequestCallChannel;
import com.rapeech.vbc.rest.packet.AlarmPacket;
import com.rapeech.vbc.rest.packet.CallAndConversationPacket;
import com.rapeech.vbc.rest.service.RestService;
import com.rapeech.vbc.utils.CommonConverter;
import com.rapeech.vbc.utils.FormatUtil;
import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VBGWController {
    private final RestService restService;
    // private final KafkaProducerService kafkaProducerService; //20240130

    FormatUtil formatUtil = new FormatUtil();


    //230523 : db 접속 기능 제거로 인한 hazelcast 추가
    private final HaConfig haConfig;
    private final PropertiesConfig propertiesConfig;


    /*
    채널 상태
            가용 가능/불가능
            이상 징후 여부
            지연 여부
            장애 여부
    */

    //230503 : (VBGW에서 VBC에게 전달 해줌) : 콜 시작 알림 수신 - 특정 콜 시작 패킷 퍼블리시(publish) 요청
    @PostMapping(value = "/vbcm/call/start/{calluuid}")
    public void callStart(@PathVariable String calluuid,
                          @RequestBody CallAndConversationPacket packet) {
        //230512 : 기존 전달 받던 형태와 동일하게 받는다고 가정하고, md 설계한 내역대로 진행
        //230512 : 통화 정보도 Packet에서 뽑아서 쓰도록 변경

        //230612 : 전달 방식에 따라 data로 들어오면 packet 처리
        if (packet.getData() != null) {
            packet = CommonConverter.convertObjectFromTarget(packet.getData(), CallAndConversationPacket.class);
            packet.setData(null); //없앰 처리
        }

        packet.setCalluuid(calluuid); //calluuid는 비어서 오기 때문에 채워서 사용

        log.debug("Receive call start packet : [{}]", packet);
        if (StringUtils.isNotBlank(packet.getBotcode())) {
            String scenName = restService.getScenarioName(packet.getBotcode());
            packet.setScenarioName(scenName);
        }

        //230524 : hazelcast에 현재 상태 추가

        //230524 : TODO : hazelcast 채널 상태 확인
        //status만 1로 바꿔줌 (사용 불가 (통화 중) 상태로 변경)
        CallAndConversationPacket finalPacket = packet;
        Channel channel = haConfig.getHazelCastMap(packet.getTenantId(), packet.getChannel());
        channel.setStatus(1);
        haConfig.setHazelCastMap(packet.getTenantId(), packet.getChannel(), channel);
        restService.callStatus(packet, 1);

        //230612 : VCode 추가
        packet.setInterfaceid("V110");

        //230614 : tr_id 추가
        packet.setTr_id(formatUtil.generateLogSequenceId());

        // //230512 : kafka로 request 로 온 정보를 넣어서 보내준다 //20240130
        // String kafkaMessage = formatUtil.requestBodyToJsonString(packet);
        // restService.conversationUpdate(packet);

        // kafkaProducerService.sendMessageTalk(kafkaMessage);
        // log.info("[callStart] REQUEST sends the information to KAFKA. [{}]", kafkaMessage);
    }


    //230503 : (VBGW에서 VBC에게 전달 해줌) : 대화 수행 알림 수신 - 콜 중 기능과 매핑 (210 / 290 으로 input/output 자름)
    @PostMapping(value = "/vbcm/rec/start/{calluuid}")
    public void conversationStart(@PathVariable String calluuid,
                                  @RequestBody CallAndConversationPacket packet) {
        //230511 : conversation 은 대화 start - talk - end인데
        //partial이 talk 라서 루프를 도는 느낌이다.
        //dialog를 talk - end에서 append하여 업데이트 한다.


        //230612 : 전달 방식에 따라 data로 들어오면 packet 처리
        if (packet.getData() != null) {
            packet = CommonConverter.convertObjectFromTarget(packet.getData(), CallAndConversationPacket.class);
            packet.setData(null); //없앰 처리
        }


        packet.setCalluuid(calluuid); //calluuid는 비어서 오기 때문에 채워서 사용

        //230612 : VCode 추가
        packet.setInterfaceid("V410");

        if (StringUtils.isBlank(packet.getCenterCode())) {
            String centerCode = restService.getCenterCode(packet.getChannel());
            if (StringUtils.isNotBlank(centerCode)) {
                packet.setCenterCode(centerCode);
            }
        }

        //230614 : tr_id 추가
        packet.setTr_id(formatUtil.generateLogSequenceId());
        if (StringUtils.isNotBlank(packet.getBotcode())) {
            String scenName = restService.getScenarioName(packet.getBotcode());
            packet.setScenarioName(scenName);
        }
        log.debug("Receive rec start packet : [{}]", packet);

        restService.conversationUpdate(packet);

        // //230512 : kafka로 request 로 온 정보를 넣어서 보내준다. //20240130
        // String kafkaMessage = formatUtil.requestBodyToJsonString(packet); //20240130


        // kafkaProducerService.sendMessageTalk(kafkaMessage); //20240130

        //log.info("[conversationStart] REQUEST sends the information to KAFKA. " + kafkaMessage); //20240130
    }

    //#230426 : (VBGW에서 VBC에게 전달 해줌) : 전화 중 부분 인식 결과 - (V240)
    @PostMapping(value = "/vbcm/rec/particial/{calluuid}")
    public void partialConversation(@PathVariable String calluuid,
                                    @RequestBody CallAndConversationPacket packet) {
        //230511 : NLP 가 Skyrunner를 통해서 SCA로 전달을 해 줌
        //Channel_NO 도 같이 전달해 주므로 그것으로 조회함
        //calluuid는 유니크한 값임 -> 통화 1개당 1개가 배정됨
        //OMS_AI_ALARMS -> Tenant id / center code / call uuid를 아니까, 그것만 추가하면 됨
        //ai_alarms에 욕설이 n번 나더라도 -> 횟수 체크는 없이 1만 체크함

        //230612 : 전달 방식에 따라 data로 들어오면 packet 처리
        if (packet.getData() != null) {
            packet = CommonConverter.convertObjectFromTarget(packet.getData(), CallAndConversationPacket.class);
            packet.setData(null); //없앰 처리
        }


        packet.setCalluuid(calluuid); //calluuid는 비어서 오기 때문에 채워서 사용
        //230612 : VCode 추가
        packet.setInterfaceid("V420");

        if (StringUtils.isBlank(packet.getCenterCode())) {
            String centerCode = restService.getCenterCode(packet.getChannel());
            if (StringUtils.isNotBlank(centerCode)) {
                packet.setCenterCode(centerCode);
            }
        }

        //230614 : tr_id 추가
        packet.setTr_id(formatUtil.generateLogSequenceId());

        if (StringUtils.isNotBlank(packet.getBotcode())) {
            String scenName = restService.getScenarioName(packet.getBotcode());
            packet.setScenarioName(scenName);
        }
        // 230512 : DB에 정보 update 하는 것은 End 때만 전달 해 주는 것으로 확정
        // 230719 : 각 api 호출 시 마다 전달 하는 것으로 재 수정(사유: 콜 시작 및 정보 갱신에 대한 부분이 DB 저장이 안되어 call종료 외 모든 API 내 메시지 병합
        restService.conversationUpdate(packet);

        //230512 : kafka로 request 로 온 정보를 넣어서 보내준다. //20240130
        // String kafkaMessage = formatUtil.requestBodyToJsonString(packet);
        // kafkaProducerService.sendMessageTalk(kafkaMessage);


        // log.info("[partialConversation] REQUEST sends the information to KAFKA. " + kafkaMessage);

    }

    //#230426 : (VBGW에서 VBC에게 전달 해줌) : 전화 중 인식 결과 - (V290) 현재는 490으로 매핑되있음 : 알람/이상징후 여기에 추가
    @PostMapping(value = "/vbcm/rec/end/{calluuid}")
    public void conversationEnd(@PathVariable String calluuid,
                                @RequestBody CallAndConversationPacket packet) {

        //230511 : NLP 가 Skyrunner를 통해서 SCA로 전달을 해 줌
        //Channel_NO 도 같이 전달해 주므로 그것으로 조회함
        //calluuid는 유니크한 값임 -> 통화 1개당 1개가 배정됨
        //OMS_AI_ALARMS -> Tenant id / center code / call uuid를 아니까, 그것만 추가하면 됨

        //ai_alarms에 욕설이 n번 나더라도 -> 횟수 체크는 없이 1만 체크함

        //230612 : 전달 방식에 따라 data로 들어오면 packet 처리
        if (packet.getData() != null) {
            packet = CommonConverter.convertObjectFromTarget(packet.getData(), CallAndConversationPacket.class);
            packet.setData(null); //없앰 처리
        }


        packet.setCalluuid(calluuid); //calluuid는 비어서 오기 때문에 채워서 사용

        //230612 : VCode 추가
        packet.setInterfaceid("V490");

        if (StringUtils.isBlank(packet.getCenterCode())) {
            String centerCode = restService.getCenterCode(packet.getChannel());
            if (StringUtils.isNotBlank(centerCode)) {
                packet.setCenterCode(centerCode);
            }
        }

        //230614 : tr_id 추가
        packet.setTr_id(formatUtil.generateLogSequenceId());
        if (StringUtils.isNotBlank(packet.getBotcode())) {
            String scenName = restService.getScenarioName(packet.getBotcode());
            packet.setScenarioName(scenName);
        }
        //230515 : NLP가 conversationEnd 때 주는 것으로 생각하고, 이상징후는 conversationEnd 때만 진행
        log.debug("Receive rec end packet : [{}]", packet);
        //바뀔 수 있으나 일단은 대화 종료 (전화 중 인식 결과) 시에만 진행한다.
        restService.conversationUpdate(packet);

        //230512 : kafka로 request 로 온 정보를 넣어서 보내준다. //20240130
        // String kafkaMessage = formatUtil.requestBodyToJsonString(packet);


        // kafkaProducerService.sendMessageTalk(kafkaMessage);
        // log.info("[conversationEnd] REQUEST sends the information to KAFKA. " + kafkaMessage);

    }

    //#230426 : (VBGW에서 VBC에게 전달 해줌) : 콜 종료 알림 수신 - 특정 콜 종료 패킷 Publish 요청 (알람/이상징후 여기에 추가)
    @PostMapping(value = "/vbcm/call/end/{calluuid}")
    public void callEnd(@PathVariable String calluuid,
                        @RequestBody CallAndConversationPacket packet) {

        //230612 : 전달 방식에 따라 data로 들어오면 packet 처리
        if (packet.getData() != null) {
            packet = CommonConverter.convertObjectFromTarget(packet.getData(), CallAndConversationPacket.class);
            packet.setData(null); //없앰 처리
        }

        packet.setCalluuid(calluuid); //calluuid는 비어서 오기 때문에 채워서 사용
        log.debug("Receive call end packet : [{}]", packet);
        //230524 : hazelcast에 현재 상태 추가
        Channel channel = haConfig.getHazelCastMap(packet.getTenantId(), packet.getChannel());
        channel.setStatus(0);

        haConfig.setHazelCastMap(packet.getTenantId(), packet.getChannel(), channel);

        restService.callStatus(packet, 0); //통화종료 (대기) : 0
        //230612 : VCode 추가
        packet.setInterfaceid("V190");


        //230614 : tr_id 추가
        packet.setTr_id(formatUtil.generateLogSequenceId());
        if (StringUtils.isNotBlank(packet.getBotcode())) {
            String scenName = restService.getScenarioName(packet.getBotcode());
            packet.setScenarioName(scenName);
        }
        //230512 : kafka로 request 로 온 정보를 넣어서 보내준다. //20240130
        // String kafkaMessage = formatUtil.requestBodyToJsonString(packet);


        // kafkaProducerService.sendMessageTalk(kafkaMessage);
        // log.info("[callEnd] REQUEST sends the information to KAFKA. " + kafkaMessage);
    }

    //#230507 : (VBGW에서 VBC에게 전달 해줌) : 지연,오류(장애)
    @PostMapping(value = "/calldelaylatency/{centerCode}")
    public void callDelayLatency(@PathVariable String centerCode,
                                 @RequestBody AlarmPacket packet) {

        //oms_ai_alarms 에다가 insert 해줌
        //center_code 입력되야함 으로 기재되어 있음

        packet.setCenter_code(centerCode);

        //230614 : tr_id 추가
        packet.setTr_id(formatUtil.generateLogSequenceId());


        restService.delayAndErrorInsert(packet);

        //지연/오류 상태 oms_channel_online 테이블에 추가


        restService.callAnomarly(packet);

        //230512 : kafka로 request 로 온 정보를 넣어서 보내준다. //20240130
        // String kafkaMessage = formatUtil.requestBodyToJsonString(packet);
        // kafkaProducerService.sendMessageTalk(kafkaMessage);
        // log.info("[callDelayLatency] REQUEST sends the information to KAFKA. " + kafkaMessage);


    }


    //230503 : (VBGW에서 VBC에게 전달 해줌) : 채널 상태 확인 전문 - 채널 아이디를 input으로 주면, vbc에서 가용 가능 여부 회신해 주면 됨
    //230515 : TODO : 채널 id 대신 channel_no 로 받거나, request 내에 포함 할 수 있는 지 여쭤볼 것

    //230523 : 현재 조주형부장님 요청으로 개발해야 하는 사항(callChannelStatus)과 중복되면, node 방식으로 변경
    //230605 : outboundcallChannelStatus -> callChannelStatus
    @PostMapping(value = "/channelstatus/{channelNo}")
    public int channelStatus(@PathVariable long channelNo) {
        log.info("[channelStatus] Channel status can be checked using channel ID : " + channelNo);

        return restService.findStatusByChannelNo(channelNo);
    }


    //230523: 현재 조주형 부장님 요청으로 개발해야 하는 사항
    //230601 : 공용으로 뺄 생각 해볼 것 inbound/outbound -> value를 list로 묶어서 분기해볼 것
    //230605 : 일부 기능 fix 된 것으로 추정되어 분기 추가
    //230605 : outboundcallChannelStatus -> callChannelStatus
    //230607 : return type string -> json 으로 변경 요청 하심 (홍석원 부장님)
    @PostMapping(value = "/vapi/channelstatus")
    public JsonObject inboundCallChannelStatus(@RequestBody RequestCallChannel requestBody) {
        if(StringUtils.isEmpty(requestBody.getTenantId())) {
            throw new VBCException(ErrorCode.INVALID_ARGUMENT);
        }
        requestBody.setInoutFlag(1);
        return restService.getCallChannelStatus(requestBody);
    }

    @PostMapping(value = "/outboundcall/channelstatus")
    public JsonObject outboundCallChannelStatus(@RequestBody RequestCallChannel requestBody) {
        if(StringUtils.isEmpty(requestBody.getTenantId())) {
            throw new VBCException(ErrorCode.INVALID_ARGUMENT);
        }
        requestBody.setInoutFlag(0);
        return restService.getCallChannelStatus(requestBody);
    }

    //230605 : https://rapeech.atlassian.net/browse/LGUIBK-299
    @GetMapping(value = "/vapi/calltransfer/{channelno}")
    public JsonObject vapiCallTransfer(@PathVariable String channelno) {
        //요약 :  VAPI에서 VBC에 채널no를 주시면, end point를 리턴합니다.
        //hazelcast에서 채널no를 찾아서, 그 채널no에 해당하는 end point를 리턴합니다
        String tenantId = restService.findByChannelNo(channelno);
        // JSON 객체 생성
        JsonObject returnJsonObject = new JsonObject();

        // result 배열 생성
        JsonArray resultArray = new JsonArray();

        if(StringUtils.isNotBlank(tenantId)) {
            //230524 : hazelcast에 현재 상태 추가
            Channel channel = haConfig.getHazelCastMap(tenantId, channelno);

            //230608 : 해당하는 채널번호에 대한 regi 상태를 조회해서, 그에 매칭하는 ep를 리턴한다.
            //230608 : regi 상태가 2인 경우에는, 해당 채널번호에 대한 ep를 리턴하지 않는다.
            int registatus = channel.getRegiStatus();

            switch (registatus) {
                case 0 -> {
                    resultArray.add(buildEndpointProperty(channelno, channel.getPrimary()));
                }
                case 1 -> {
                    resultArray.add(buildEndpointProperty(channelno, channel.getPrimary()));
                }
                default -> {
                }
            }
        }


        // JSON 객체에 result 배열 추가
        returnJsonObject.add("result", resultArray);

        //230614 : tr_id 추가
        returnJsonObject.addProperty("tr_id", formatUtil.generateLogSequenceId());

        return returnJsonObject;
    }

    private JsonObject buildEndpointProperty(String channelNo, String endPoint) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("no", channelNo);
        jsonObj.addProperty("ep", endPoint);
        return jsonObj;
    }

    @GetMapping(value = "/didinbound/{did}")
    public JsonObject vbgwUrgentDIDCall(@PathVariable int did) {
        //https://rapeech.atlassian.net/browse/LGUIBK-223
        /*
               [6/2 말씀해 주신 사항] 보충
                [시나리오 id 반환 및 긴급운용모드 상태 반환] 기능
                →  회의 참여자 (예정) : @이주열 , @정광석

                특이사항 : 긴급운용모드 관련 추가 설명 필요
                @이주열
                6/2 말씀해 주신 사항
                → 기존 VAPI가 해야 할 사항을 VBC가 하는 것이 어떤지 협의 필요 (차주 수요일)
                → 기능  : VDN 정보 받아와서 (join 등을 통해서 조회한 뒤) → workspace id 리턴

         */
        JsonObject jsonObject = restService.getUrgentInfo(did);

        //230614 : tr_id 추가
        jsonObject.addProperty("tr_id", formatUtil.generateLogSequenceId());
        return jsonObject;
    }
}