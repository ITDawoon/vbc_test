package com.rapeech.vbc.rest.service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.rapeech.vbc.data.entity.*;
import com.rapeech.vbc.data.repository.*;
import com.rapeech.vbc.ha.config.HaConfig;
import com.rapeech.vbc.ha.config.PropertiesConfig;
import com.rapeech.vbc.ha.model.Channel;
//import com.rapeech.vbc.kafka.KafkaProducerService; //20240130
import com.rapeech.vbc.rest.model.request.RequestCallChannel;
import com.rapeech.vbc.rest.model.transfer.OmsChannel;
import com.rapeech.vbc.rest.packet.AlarmPacket;
import com.rapeech.vbc.rest.packet.CallAndConversationPacket;
import com.rapeech.vbc.utils.FormatUtil;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestServiceImpl implements RestService {

    private final OMSChannelsRepository omsChannelsRepository;
    private final OMSAiAlarmsRepository omsAiAlarmsRepository;
    private final OMSAlarmsRepository omsAlarmsRepository;
    private final OMSChannelOnlineRepository omsChannelOnlineRepository;

    //230607 : 긴급운용모드 관련 개발 사항 추가 개발 진행
    private final OMSScenarioMappingsRepository omsScenarioMappingsRepository;

    private final SOWorkspaceInformationsRepository soWorkspaceInformationsRepository;

    //230614 : kafka producer 를 이용해서 채널 실시간 상태 전달을 위한 추가
    // private final KafkaProducerService kafkaProducerService; //20240130
    private final PropertiesConfig propertiesConfig;
    private final HaConfig haConfig;
    FormatUtil formatUtil = new FormatUtil();

    //230508 : regist 요청
    @Override
    public List<OMSChannels> getChannelsByIdList(List<Long> idList) {
        List<OMSChannels> channelsList = omsChannelsRepository.findByIds(idList);

        if(CollectionUtils.isEmpty(channelsList)) {
            channelsList = new ArrayList<>();
        }
        return channelsList;
    }

    @Override
    public OMSChannels getChannelsById(Long channalId) {
        log.info("[getChannelsById] oms channel search by using receive channel primary id : {}", channalId);
        Optional<OMSChannels> omsChannelsOptional = omsChannelsRepository.findById(channalId);
        OMSChannels omsChannels = null;
        if(omsChannelsOptional.isPresent()) {
            omsChannels = omsChannelsOptional.get();
        }
        return omsChannels;
    }

    //230507 : 지연,오류 insert
    @Override
    public void delayAndErrorInsert(AlarmPacket packet) {

        OMSAlarms omsAlarms = new OMSAlarms();
        omsAlarms.setTenantId(packet.getTenantId());
        omsAlarms.setCenterCode(packet.getCenter_code());
        omsAlarms.setSystem(Integer.valueOf(packet.getSystem()));
        omsAlarms.setType(Integer.valueOf(packet.getType()));
        omsAlarms.setStatus(Integer.valueOf(packet.getStatus()));

        //alarm_code, message, repaired_at는 사용하지 않는다.
        //omsAlarms.setAlarmCode(packet.getAlarmCode());
        //omsAlarms.setMessage(packet.getMessage());
        omsAlarms.setDailyYmd(packet.getDaily_ymd());
        omsAlarms.setDailyHh(packet.getDaily_hh());
        omsAlarms.setCallUuid(packet.getCalluuid());
        omsAlarms.setCreatedAt(new Date());
        //omsAlarms.setRepairedAt(packet.getRepairedAt());

        omsAlarmsRepository.save(omsAlarms);


    }


    //230512 : 채널 번호를 받아와서, status (채널 상태) 를 반환
    @Override
    public int findStatusByChannelNo(long channelNo) {
        return omsChannelOnlineRepository
                .findById(channelNo)
                .get()
                .getStatus();
    }

    @Override
    public String findByChannelNo(String channelNo) {
        long channel = Long.valueOf(channelNo);
        String tenantId = new String();
        Optional<OMSChannelOnline> omsChannelOnlineOptional = omsChannelOnlineRepository.findById(channel);

        if(omsChannelOnlineOptional.isPresent()) {
            tenantId= omsChannelOnlineOptional.get().getTenantId();
        }

        return tenantId;
    }


    //230508 : callStart 시 채널 상태 '통화중'으로 변경 = 1
    //230508 : callEnd 시 채널 상태 '대기' 로 변경 = 0

    //230512 : CallAndConversationPacket에서 정보 뽑아서 사용하도록 변경
    @Override
    public void callStatus(CallAndConversationPacket packet, int status) {
        Long channel_no = Long.valueOf(packet.getChannel());

        //230512 : Null 체크
        Optional<OMSChannelOnline> omsChannelOnlineOptional = omsChannelOnlineRepository.findById(channel_no);
        OMSChannelOnline omsChannelOnline = null;
        if (omsChannelOnlineOptional.isPresent()) {
            omsChannelOnline = omsChannelOnlineOptional.get();
            if (StringUtils.isBlank(packet.getCenterCode())) {
                Optional<OMSChannels> omsChannelsOptional = omsChannelsRepository.findByTenantIdAndChannelNo(omsChannelOnline.getTenantId(), channel_no);
                if (omsChannelsOptional.isPresent()) {
                    omsChannelOnline.setCenterCode(omsChannelsOptional.get().getCenterCode());
                }
            }
            switch (status) {
                case 1:
                    //통화중 : 1
                    omsChannelOnline.setStatus(status);

                    //콜시작시 해당 콜 정보를 기반으로 call_uuid~dialogs 필드 update 진행
                    omsChannelOnline.setCallUuid(packet.getCalluuid());
                    break;
                case 0:
                    //통화완료 (대기) : 0
                    omsChannelOnline.setStatus(status);

                    //콜종료시 초기화 (center_code, call_uuid, dialogs는 null로, status~ai_alarms는 0으로)
                    omsChannelOnline.setCallUuid(null);
                    omsChannelOnline.setDialogs(null);
                    omsChannelOnline.setDelays(0);
                    omsChannelOnline.setAiAlarms(0);
                    omsChannelOnline.setErrors(0);

                    break;
                default:
                    break;
            }
            //230614 : kafka producer 를 이용해서 채널 실시간 상태 전달을 위한 추가
            log.info("send status kafka message : {} {}",packet.getScenarioName(), omsChannelOnline);
            sendKafkaStatus(packet.getScenarioName(), omsChannelOnline);

            // 통화 종료 시 center_code 필드 초기화는 카프카 메시지 전달 이후로 한다.
            if (status == 0) {
                omsChannelOnline.setCenterCode(null);
            }
        }


        //TODO 230511 : 채널넘버는 테넌트 아이디로 박혀있는데,
        //센터코드랑 calluuid, status가 바뀌어야 함

        //channelnumber, calluuid, centercode 를 받아야 함!!! TODO 받아서 오는 것

        //calluuid가 바뀌어서 옴
        //where절

        if(ObjectUtils.isNotEmpty(omsChannelOnline)) {
            omsChannelOnlineRepository.save(omsChannelOnline);
        }
    }


    //230512 : 전화 중 부분 인식 결과 - (V240), 전화 중 인식 결과 - (V290) 처리 부분
    @Override
    public void conversationUpdate(CallAndConversationPacket packet) {
        log.info("[conversationUpdate] receive call packet: [{}]", packet);
        Long channel_no = Long.valueOf(packet.getChannel());
        Gson gson = new Gson();
        //230512 : Null 체크
        Optional<OMSChannelOnline> omsChannelOnlineOptional = omsChannelOnlineRepository.findById(channel_no);
        OMSChannelOnline omsChannelOnline = null;

        if (omsChannelOnlineOptional.isPresent()) {
            omsChannelOnline = omsChannelOnlineOptional.get();

            //230512 : dialog(=transcript)는 json을 읽어와서 append 하는 방향으로 진행한다.
            List<CallAndConversationPacket> dialogList = new ArrayList<>();

            if (StringUtils.isNotBlank(omsChannelOnline.getDialogs())) {
                String prevDialogs = omsChannelOnline.getDialogs();
                dialogList = gson.fromJson(prevDialogs, new TypeToken<List<CallAndConversationPacket>>() {
                }.getType());
            }

            dialogList.add(packet);
            omsChannelOnline.setDialogs(gson.toJson(dialogList));

            //230515 : SCA 쪽에 문의 후 김다은 과장님께 문의
            // -> 와이즈넛에서 어떠한 형태로 올 지 모르나 만약 올 경우에는 boolean 으로 올 것으로 예상된다.
            //TODO : NLP(와이즈넛) 에서 욕설 정보 를 전달 해 줄 시 request 에서 뽑아서 쓸 것.
            //아마도 boolean 형태로 전달 해 줄 것으로 예상되므로 일단 초안으로 작성한다.
            log.info("[conversationUpdate] receive warning: [{}]", packet.isWarning());
            boolean nlpOption = packet.isWarning();

            if (nlpOption) {
                log.info("[conversationUpdate] occured ai alarm. conversation warning!");
                omsChannelOnline.setAiAlarms(1);

                //TODO : NLP가 이상징후 에 대한 값을 전달해 주면 oms_ai_alarms 테이블에 insert
                LocalDateTime today = LocalDateTime.now();
                String dailyYmd = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String dailyHH = today.format(DateTimeFormatter.ofPattern("HH"));

                OMSAiAlarms omsAiAlarms = new OMSAiAlarms();
                omsAiAlarms.setTenantId(omsChannelOnline.getTenantId());
                omsAiAlarms.setCenterCode(omsChannelOnline.getCenterCode());

                omsAiAlarms.setDailyYmd(dailyYmd);
                omsAiAlarms.setDailyHh(dailyHH);
                omsAiAlarms.setCallUuid(omsChannelOnline.getCallUuid());
                omsAiAlarms.setCreatedAt(new Date());
                //TODO : 230515 : messages 이후로는 옵셔널인 것으로 추정 (아닐 경우 EMBUS에서 받아온 값으로 수정)
                if(StringUtils.isNotBlank(packet.getTransfer_at())) {
                    omsAiAlarms.setTransferedAt(new Date()); //호전환 시각
                }
                if(StringUtils.isNotBlank(packet.getTransfer_user())) {
                    omsAiAlarms.setTransferedBy(packet.getTransfer_user()); //호전환 요청자
                }
                if(StringUtils.isNotBlank(packet.getTransfer_target())) {
                    omsAiAlarms.setTransferedTo(Integer.parseInt(packet.getTransfer_target())); //호전환 상담그룹 ID
                }

                //230614 : kafka producer 를 이용해서 채널 실시간 상태 전달을 위한 추가
                log.info("[conversationUpdate] kafka warning message send.");
                sendKafkaStatus(packet.getScenarioName(), omsChannelOnline);

                //230515 : 필요 사항 삽입 후 insert 한다.
                omsAiAlarmsRepository.save(omsAiAlarms);
            }
        }

        if(ObjectUtils.isNotEmpty(omsChannelOnline)) {
            omsChannelOnlineRepository.save(omsChannelOnline);
        }
    }

    @Override
    public void callAnomarly(AlarmPacket packet) {
        Long channel_no = Long.valueOf(packet.getChannel());
        Optional<OMSChannelOnline> omsChannelOnlineOptional = omsChannelOnlineRepository.findById(channel_no);

        if (omsChannelOnlineOptional.isPresent()) {
            OMSChannelOnline omsChannelOnline = omsChannelOnlineOptional.get();

            int type = Integer.valueOf(packet.getType()); //지연 : 0 | 장애 : 1
            switch (type) {
                case 0:
                    omsChannelOnline.setDelays(1);
                    break;
                case 1:
                    omsChannelOnline.setErrors(1);
                    break;
                default:
                    break;
            }
            //230614 : kafka producer 를 이용해서 채널 실시간 상태 전달을 위한 추가
            String scenarioName = "";
            sendKafkaStatus(scenarioName, omsChannelOnline);

            omsChannelOnlineRepository.save(omsChannelOnline);

        }
    }

    //230607 : 긴급운용모드 추가
    @Override
    public JsonObject getUrgentInfo(int vdn) {
        //TODO 여기 직렬화를 수정해야 합니다
        JsonObject jsonObject = new JsonObject();
        List<Object[]> urgentInfo = omsScenarioMappingsRepository.getDataByVdn(vdn);

        if (!urgentInfo.isEmpty()) {
            Object[] data = urgentInfo.get(0);
            jsonObject.addProperty("tenantId", data[0] != null ? (String) data[0] : "");
            jsonObject.addProperty("centerCode", data[1] != null ? (String) data[1] : "");
            jsonObject.addProperty("workspaceUuid", data[2] != null ? (String) data[2] : "");
            if((boolean) data[3]) {
                jsonObject.addProperty("urgentStatus", 1);
            } else {
                jsonObject.addProperty("urgentStatus", 0);
            }
            jsonObject.addProperty("urgentVdnNo", data[4] != null ? (String) data[4] : "");
        } else {
            // 모든 필드에 대해 null 처리
            jsonObject.addProperty("tenantId", "");
            jsonObject.addProperty("centerCode", "");
            jsonObject.addProperty("workspaceUuid", "");
            jsonObject.addProperty("urgentStatus", 0); //이거 확인해보고 바꿔줌 TODO
            jsonObject.addProperty("urgentVdnNo", "");
        }

        return jsonObject;
    }


    public void sendKafkaStatus(String scenarioName, OMSChannelOnline omsChannelOnline) {
        //230614 : kafka producer 를 이용해서 채널 실시간 상태 전달을 위한 추가
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("channelNo", omsChannelOnline.getChannelNo());
        jsonObject.addProperty("centerCode", omsChannelOnline.getCenterCode());
        jsonObject.addProperty("calluuid", omsChannelOnline.getCallUuid());
        jsonObject.addProperty("status", omsChannelOnline.getStatus());
        jsonObject.addProperty("delays", omsChannelOnline.getDelays());
        jsonObject.addProperty("errors", omsChannelOnline.getErrors());
        jsonObject.addProperty("aiAlarms", omsChannelOnline.getAiAlarms());
        jsonObject.addProperty("scenarioName", scenarioName);

        // kafkaProducerService.sendMessageStatus(jsonObject.toString()); //20240130
    }

    @Override
    public String getCenterCode(String channel) {
        AtomicReference<String> centerCode = new AtomicReference<>();
        Long channel_no = Long.valueOf(channel);

        //230512 : Null 체크
        Optional<OMSChannelOnline> omsChannelOnlineOptional = omsChannelOnlineRepository.findById(channel_no);
        if (omsChannelOnlineOptional.isPresent()) {
            OMSChannelOnline omsChannelOnline = omsChannelOnlineOptional.get();
            Optional<OMSChannels> omsChannelsOptional = omsChannelsRepository.findByTenantIdAndChannelNo(omsChannelOnline.getTenantId(), channel_no);
            omsChannelsOptional.ifPresent(omsChannels -> centerCode.set(omsChannels.getCenterCode()));
        }
        return centerCode.get();
    }



    @Override
    public String getScenarioName(String workspaceId) {
        String scenarioNm = null;
        Optional<SOWorkspaceInformations> soWorkspaceInformationsOptional = soWorkspaceInformationsRepository.findById(workspaceId);
        if(soWorkspaceInformationsOptional.isPresent()) {
            SOWorkspaceInformations soWorkspaceInformations = soWorkspaceInformationsOptional.get();
            if(StringUtils.equals(soWorkspaceInformations.getType(), "main")) {
                scenarioNm = soWorkspaceInformations.getName();
            }
        }

        return scenarioNm;
    }

    @Override
    public void removeOmsChannelOnlineById(Long channelId) {
        Optional<OMSChannels> omsChannelsOptional = omsChannelsRepository.findById(channelId);
        omsChannelsOptional.ifPresent(omsChannels -> omsChannelOnlineRepository.deleteById(omsChannels.getChannelNo()));
    }

    @Override
    public void removeOmsChannelsById(Long channelId) {
        omsChannelsRepository.deleteById(channelId);
    }

    @Override
    public JsonObject getCallChannelStatus(RequestCallChannel requestCallChannel) {
        // 조주형 부장님 요청사항
        // Tenant ID를 받아와서, 사용 가능한 Channel NO, 현재 Regist된 End Point를 반환한다.
        log.info("[getCallChannelStatus] Receives Tenant ID, returns usable Channel NO, and currently registered End Point. current tenant id: " + requestCallChannel.getTenantId());

        //hazelObject 를 루프를 돌려서, key가 바라보는 tenantId가 "tenantId"가 일치하는 것을 찾아서,
        // 그거에 대한 ep를 출력하는 걸 대충 짠다
        Map<String, Channel> channelMap = haConfig.getDataMap(requestCallChannel.getTenantId());
        Gson gson = new Gson();
        log.info("[getCallChannelStatus] call bot channel map : {}", gson.toJson(channelMap));
        Collection<Channel> valueList = channelMap.values();
        Predicate<Channel> statusIdle = c -> c.getStatus() == 0;
        Predicate<Channel> direction = c -> c.getDirection() != requestCallChannel.getInoutFlag();
        List<Channel> channelList = valueList.stream()
                .filter(statusIdle.and(direction)).toList();

        // JSON 객체 생성
        JsonObject returnJsonObject = new JsonObject();

        // result 배열 생성
        JsonArray resultArray = new JsonArray();

        //230524 : 현재 사용 가능 여부 확인해서 회신 (status)
        for(Channel channel : channelList) {
            JsonObject resultObject = new JsonObject();
            if (channel.getDirection() == 2 && requestCallChannel.getInoutFlag() == 1) { // mix 채널 일 경우 obcoller 점유여부 확인
                if (!checkObWeight(requestCallChannel.getTenantId(), Long.toString(channel.getChannelNo()))) {
                    continue;
                }
            }
            resultObject.addProperty("no", channel.getChannelNo());

            //230524 : registatus 추가 (regist 되야 사용 가능함 - 둘 다 죽었으면 2)
            switch (channel.getRegiStatus()) {
                case 0 -> {
                    resultObject.addProperty("ep", channel.getPrimary());
                    resultArray.add(resultObject);
                }
                case 1 -> {
                    resultObject.addProperty("ep", channel.getSecondary());
                    resultArray.add(resultObject);
                }
                default -> {
                }
            }
        }

        log.info("[getCallChannelStatus] TenantId : {} -> avaliable channel list : {}", requestCallChannel.getTenantId(), resultArray);
        if(requestCallChannel.getInoutFlag() == 1 && resultArray.size() > 0) {
            int len = resultArray.size();
            if(len > 1) {
                int rand = ThreadLocalRandom.current().nextInt(0,len);
                JsonObject randObj = (JsonObject) resultArray.get(rand);
                resultArray = new JsonArray();
                resultArray.add(randObj);
            }
        }
        // JSON 객체에 result 배열 추가
        returnJsonObject.add("result", resultArray);
        if(resultArray.size() == 0) {
            log.info("[getCallChannelStatus] TenantID [{}] no avaliable channels", requestCallChannel.getTenantId());
        }
        //230614 : tr_id 추가
        if(StringUtils.isNotBlank(requestCallChannel.getTrId())) {
            returnJsonObject.addProperty("tr_id",requestCallChannel.getTrId());
        } else {
            returnJsonObject.addProperty("tr_id", formatUtil.generateLogSequenceId());
        }

        log.info("[getCallChannelStatus] return avaliable channel list : {}", resultArray);
        return returnJsonObject;
    }

    private boolean checkOnline(String tenantId, Long channelId) {
        boolean avaliableFlg = false;
        OMSChannelOnline omsChannelOnline = omsChannelOnlineRepository.findOneByChannelNoAndTenantId(channelId, tenantId);
        if(ObjectUtils.isNotEmpty(omsChannelOnline)) {
            if(omsChannelOnline.getStatus() == 0) {
                avaliableFlg = true;
            }
        }
        return avaliableFlg;
    }

    private boolean checkObWeight(String tenantId, String channel) {
        boolean obWeightFlg = false;
        Long channel_no = Long.valueOf(channel);
        Optional<OMSChannels> omsChannelsOptional = omsChannelsRepository.findByTenantIdAndChannelNo(tenantId, channel_no);
        if (omsChannelsOptional.isPresent()) {
            OMSChannels omsChannels = omsChannelsOptional.get();
            if (StringUtils.equals(omsChannels.getObweight(), "0")) {
                obWeightFlg = true;
            }
        }
        return obWeightFlg;
    }
}
