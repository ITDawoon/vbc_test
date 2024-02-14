// package com.rapeech.vbc.rest.controller;

// import com.google.gson.reflect.TypeToken;
// import com.rapeech.vbc.data.entity.OMSChannels;
// import com.rapeech.vbc.ha.config.HaConfig;
// import com.rapeech.vbc.ha.config.PropertiesConfig;
// import com.rapeech.vbc.ha.model.Channel;
// import com.rapeech.vbc.logback.ErrorCode;
// import com.rapeech.vbc.logback.StandardIntegratedLog;
// import com.rapeech.vbc.logback.VBCException;
// import com.rapeech.vbc.rest.common.model.ResponseCommon;
// import com.rapeech.vbc.rest.dao.HaDao;
// import com.rapeech.vbc.rest.model.request.RequestScaRegist;
// import com.rapeech.vbc.rest.model.request.RequestScaUnregist;
// import com.rapeech.vbc.rest.model.transfer.EndPoint;
// import com.rapeech.vbc.rest.model.transfer.ScaExtra;
// import com.rapeech.vbc.rest.model.transfer.ScaRegiInfo;
// import com.rapeech.vbc.rest.service.RestService;
// import com.rapeech.vbc.utils.CommonConverter;
// import com.rapeech.vbc.utils.ConnectionUtil;
// import com.google.gson.*;
// import io.swagger.v3.oas.annotations.Operation;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.ObjectUtils;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.util.CollectionUtils;
// import org.springframework.web.bind.annotation.*;

// import java.util.*;
// import java.util.concurrent.CompletableFuture;
// import java.util.stream.Collectors;

// @RestController
// @RequiredArgsConstructor
// @Slf4j
// public class ScaController {


//     private final RestService restService;
//     ConnectionUtil connectionUtil = new ConnectionUtil();

//     private final PropertiesConfig propertiesConfig;

//     private final HaConfig haConfig;

//     private final HaDao haDao;

//     //230516 : 채널 Regist 하는 URL을 만들어주는 부분
//     private String registUrl(String ep){
//         return "http://"+ep+":"+propertiesConfig.getVbgwPort()+"/"+propertiesConfig.getVbgwRegistUrl();
//     }

//     //230516 : 채널 Unregist 하는 URL을 만들어주는 부분
//     private String unregistUrl(String ep){
//         return "http://"+ep+":"+propertiesConfig.getVbgwPort()+"/"+propertiesConfig.getVbgwUnregistUrl();
//     }



//     // VBGW(sca)의 인스턴스들에게 채널을 분배하며,
//     // 채널 외에 교환기 정보도 같이 주도록 한다.메인 인스턴스만 수행 (H/A 참조)

//     // 교환기 정보 (조사 후 기술)



//     //230530 : 채널 rescan 요청 URL을 만들어주는 부분
//     private String rescanUrl(String ep){
//         //230530 : ssl 적용 안 하도록 수정
//         //txtapi/sofia?profile%20

//         //230531 : 하드코딩된 profile name 추가
//         return "http://"+ep+":"+propertiesConfig.getVbgwPort()+"/"+propertiesConfig.getVbgwRescanUrl() + propertiesConfig.getAccessProfileName() +"%20rescan%20all";
//   }


//     /*
//         230516 : hazelcast node 관련 추가 사항
//         OMS(WEB)에서 regist 요청 시에는 hazelcast node에 접근하여 없는 channelid만 읽어와서 추가한다.

//         OMS(WEB)에서 unregist 요청 시에는 hazelcast node에 접근하여 channelid 자체를 날려버린다.
//         -> 이렇게 하면 db에 두번 접근 할 필요 없이 진행한다.
//         -> 따라서 regist / unregist랑 같지 않기 때문에 이름을 변경해서 진행한다. (channelidadd / channelidremove로 변경)
//         그래서 hazelcast에서 가지고 다니는 json 형태를 좀 수정해야 함
//         -> channelid가 필요함
//      */


//     //#230426 : (WEB에서 VBC에게 전달 해줌) : SCA 채널(VBGW) regist 요청 대리
//     // - 웹에서 채널 목록을 주면 -> 채널별 레지 성공 여부를 회신해줌
//     @Operation(summary = "SCA 채널 regist 요청")
//     @RequestMapping(method = RequestMethod.POST, value = "/sca/regist")
//     //230504 : VBGW에 POST 로 보내도 됨
//     //230523 : POST -> GET으로 바뀔 수도 있음 (협의 진행 중)
//     //230531 성공 여부 미전달
//     //public JsonObject addChannel_regist(@RequestBody List <Long> channelList){
//     //230602 : TODO 이것을 json으로 바꿔보아요 ^^... 기능추가됨 -> tr_id 추가됨
//     //230607 : 기능 추가 진행
//     //public void addChannel_regist(@RequestBody List <Long> channelList){
//     public ResponseCommon addChannelRegist(@RequestBody RequestScaRegist requestScaRegist) {
//         if(CollectionUtils.isEmpty(requestScaRegist.getChannelList())) {
//             throw new VBCException(ErrorCode.INVALID_ARGUMENT);
//         }
//         //TODO : 채널 number를 가져와서 -> channel 번호를 줌
//         //where 절에 channel no를 넣어서 가져옴
//         //channelNo : "70001"
//         //others <- extras 읽어와서 그대로 전달
//         //extras 유효성 체크는 화면단에서 할 것
//         //정광석 셀장님께 regist 요청 시에 이렇게 보낸다고 말씀 드릴 것
//         //null 체크하는 부분에 대해서 -> 빈 문자열로 할 건지 null로 드릴 건지 확인할 것

//         boolean flag = true; //레지 성공 여부 확인

//         //channelID에 대한 Tier 1/2/3 한번에 JSON으로 만들어서 보냄
//         //채널당 정보 json array로 발라서 보내면 됨

//         //JsonObject jsonObject = restService.getAllChannels();


//         //230530 : Connection 시 username, password 사용
//         String username = propertiesConfig.getVbgwConnectionUsername();
//         String password = propertiesConfig.getVbgwConnectionPassword();

//         // Base64로 사용자 이름과 비밀번호를 인코딩
//         String credentials = username + ":" + password;


//         //230531 성공 여부 미전달
//         //JsonObject returnObject = new JsonObject();

//         List<OMSChannels> channelList = restService.getChannelsByIdList(requestScaRegist.getChannelList());
//         Set<String> tenantSet = new HashSet<>();
//         channelList.stream().forEach(omsChannels -> {
//             tenantSet.add(omsChannels.getTenantId());
//         });

//         List<CompletableFuture<Boolean>> requestList = new ArrayList<>();


//         //TODO : 또 기능이 바뀌면 또 수정해야 함

//         //230531 : ep 정보를 json에 넣었다가, 하나씩 빼서 rescan 목적으로 사용한다. (Json 사용)
//         JsonObject rescanJson = new JsonObject();

//         //230531 : ep 정보를 set에 넣었다가, 하나씩 빼서 rescan 요청 보낸다. (Set 사용)
//         Set<String> rescanSet = new HashSet<>();

//         Gson gson = new Gson();
//         for (OMSChannels channels : channelList) {
//             ScaRegiInfo scaRegiInfo = CommonConverter.convertObjectFromTarget(channels, ScaRegiInfo.class);
//             if (ObjectUtils.isEmpty(scaRegiInfo.getExtras())) {
//                 scaRegiInfo.setExtras(gson.fromJson(channels.getExtras(), ScaExtra.class));
//             }

//             if (ObjectUtils.isEmpty(scaRegiInfo.getEndPoints())) {
//                 scaRegiInfo.setEndPoints(gson.fromJson(channels.getEndpoints(), new TypeToken<List<EndPoint>>() {
//                 }));
//             }
//             scaRegiInfo.setProfileName(propertiesConfig.getAccessProfileName());
//             scaRegiInfo.setTrId(requestScaRegist.getTrId());

//             //230531 : rescan 요청을 보낼 때 사용할 json
//             JsonObject rescan = new JsonObject();

//             log.info("send json : [{}]", scaRegiInfo);
//             //230531 : primary | primary -> secondary 기능 구현
//             CompletableFuture<Boolean> request = connectionUtil.RestVBGWPostConnector(registUrl(channels.getPrimaryEp()), scaRegiInfo, credentials)
//                     .thenCompose(primaryFlag -> {
//                         if (!primaryFlag) {
//                             //primaryFlag 가 false인 경우
//                             return connectionUtil.RestVBGWPostConnector(registUrl(channels.getSecondaryEp()), scaRegiInfo, credentials)
//                                     .thenApply(secondaryFlag -> {
//                                         if (!secondaryFlag) {
//                                             // 2 = regi 2 개 다 실패 상태(둘 다 서버가 동시에 죽었을 경우 추가)  -> primary에게만 regi  요청  |
//                                             //"registatus", 2
//                                             rescan.addProperty("registatus", 2);
//                                             rescan.addProperty("ep", channels.getPrimaryEp());
//                                             rescanJson.add(String.valueOf(channels.getChannelNo()), rescan);
//                                         } else {
//                                             // 1 = secondary regi 상태   -> 추후  primary regi -> secondary unregi  요청 |
//                                             //"registatus", 1
//                                             rescan.addProperty("registatus", 1);
//                                             rescan.addProperty("ep", channels.getSecondaryEp());

//                                             //230531 : ep 정보를 set에 넣었다가, 하나씩 빼서 rescan 요청 보낸다. (Set 사용)
//                                             rescanSet.add(channels.getSecondaryEp());

//                                             rescanJson.add(String.valueOf(channels.getChannelNo()), rescan);
//                                         }
//                                         return secondaryFlag;
//                                     });
//                         } else {
//                             //primaryFlag 가 true인 경우
//                             //"registatus", 0
//                             rescan.addProperty("registatus", 0);
//                             rescan.addProperty("ep", channels.getPrimaryEp());

//                             //230531 : ep 정보를 set에 넣었다가, 하나씩 빼서 rescan 요청 보낸다. (Set 사용)
//                             rescanSet.add(channels.getPrimaryEp());

//                             rescanJson.add(String.valueOf(channels.getChannelNo()), rescan);

//                             return CompletableFuture.completedFuture(true);
//                         }

//                     });

//             requestList.add(request);

//             log.info("[addChannelRegist] Register status by channel json. " + request);
//         }

//         CompletableFuture<Void> allRequests = CompletableFuture.allOf(requestList.toArray(new CompletableFuture[0]));
//         allRequests.join();

//         //230508 : 채널별 레지 성공 여부 json으로 리턴

//         //TODO : ACK만 받으면 됨
//         //화면에 성공여부 회신 안 해줘도 됨
//         //실패했을 때 일단 두자
//         //성공여부는 ㅇㅅㅇ
//         //값을 최초에 주는 느낌이라 목록형태로 주는 것임
//         //의미가 없으면 하지말자

//         //hazelcast에 부재한 channelid를 추가한다.
//         for (String tenantId : tenantSet) {
//             Map<String, Channel> channelMap = haConfig.getDataMap(tenantId);
// //        JsonObject hazelObject = JsonParser.parseString(endPoint).getAsJsonObject();

//             List<Long> existtIds = new ArrayList<>();
//             channelMap.entrySet()
//                     .stream()
//                     .map(entry -> Long.parseLong(entry.getKey()))
//                     .forEach(key -> {
//                                 channelList.stream()
//                                         .filter(channel -> channel.getChannelNo() == key)
//                                         .forEach(
//                                                 channel -> {
//                                                     // 키가 있는 경우에는 properties 값 변경
//                                                     OMSChannels keyInfo = haDao.findByChannelNo(key);
//                                                     if (ObjectUtils.isEmpty(keyInfo)) {
//                                                         return;
//                                                     }
//                                                     //hazelcast object 내에서 있는 값 가져와서 바꿔줘야 한다.
//                                                     Channel existChannel = channelMap.get(key);
//                                                     if(ObjectUtils.isEmpty(existChannel)) {
//                                                         existChannel = new Channel();
//                                                     }

//                                                     existChannel.setPrimary(keyInfo.getPrimaryEp());
//                                                     existChannel.setSecondary(keyInfo.getSecondaryEp());
//                                                     //keyObject.addProperty("registatus", 0);

//                                                     //230531 : registatus 는 가져올 수 있다.
//                                                     //keyObject.addProperty("registatus", 0);
//                                                     existChannel.setRegiStatus(rescanJson.get(String.valueOf(key)).getAsJsonObject().get("registatus").getAsInt());

//                                                     existChannel.setTenantId(keyInfo.getTenantId());
//                                                     existChannel.setDirection(keyInfo.getDirection());
//                                                     existChannel.setStatus(0);
//                                                     existChannel.setChannelNo(keyInfo.getChannelNo());


//                                                     //230601 : unregi 시 username 필요
//                                                     existChannel.setUserName(keyInfo.getUsername());
//                                                     existChannel.setScaRegiInfo(haDao.parseScaRegiInfo(keyInfo));

//                                                     // 키가 수정 완료 후 다시 추가
//                                                     haConfig.setHazelCastMap(tenantId, String.valueOf(existChannel.getChannelNo()), existChannel);
//                                                     existtIds.add(key);
//                                                 }
//                                         );
//                             }
//                     );

//             List<Long> channelNos = new ArrayList<>();
//             channelList.forEach(c -> channelNos.add(c.getChannelNo()));
//             channelNos.removeAll(existtIds);
//             channelNos.forEach(
//                     channelNo -> {
//                         // 키가 있는 경우에는 properties 값 변경
//                         OMSChannels keyInfo = haDao.findByChannelNo(channelNo);
//                         //hazelcast object 내에서 있는 값 가져와서 바꿔줘야 한다.
//                         Channel channel = new Channel();

//                         channel.setPrimary(keyInfo.getPrimaryEp());
//                         channel.setSecondary(keyInfo.getSecondaryEp());
//                         //keyObject.addProperty("registatus", 0);

//                         //230531 : registatus 는 가져올 수 있다.
//                         //keyObject.addProperty("registatus", 0);
//                         channel.setRegiStatus(rescanJson.get(String.valueOf(channelNo)).getAsJsonObject().get("registatus").getAsInt());

//                         channel.setTenantId(keyInfo.getTenantId());
//                         channel.setDirection(keyInfo.getDirection());
//                         channel.setStatus(0);
//                         channel.setChannelNo(keyInfo.getChannelNo());

//                         //230601 : unregi 시 username 필요
//                         channel.setUserName(keyInfo.getUsername());
//                         channel.setScaRegiInfo(haDao.parseScaRegiInfo(keyInfo));

//                         haConfig.setHazelCastMap(tenantId, String.valueOf(channel.getChannelNo()), channel);
//                     }
//             );
//         }

//         log.info("[addChannelRegist] Add registered channels to hazelcast. complete.");


//         //230601 : 임시로 막음 TODO
//         JsonObject nullJsonObject = new JsonObject();
//         //230531 : ep 정보를 json에 넣었다가, 하나씩 빼서 rescan 요청 보낸다. (json 사용)
//         for (String ep : rescanSet) {
//             CompletableFuture<Boolean> rescanConnector = connectionUtil.RestVBGWPostConnector(rescanUrl(ep), nullJsonObject, credentials);
//             rescanConnector.thenAcceptAsync(rescanResult -> {
//                 if (rescanResult) {
//                     log.info("[addChannelRegist] Rescan success by channel json. " + ep);
//                 } else {
//                     log.info("[addChannelRegist] Rescan fail by channel json. " + ep);
//                 }
//             });
//         }
//         return CommonConverter.getCommonResponse();
//     }


//     //#230426 : (WEB에서 VBC에게 전달 해줌) : SCA 채널 unregist 요청 -
//     //230531 : rescan 기능 추가 & 현재 바라보는 ep 정보를 hazelcast에서 가져온다.
//     //230602 : TODO 이것을 json으로 바꿔보아요 ^^... 기능추가됨 -> tr_id 추가됨
//     @Operation(summary = "SCA 채널 unregist 요청")
//     @RequestMapping(method = RequestMethod.POST, value = "/sca/unregist")
//     public ResponseCommon deleteChannelUnregist(@RequestBody RequestScaUnregist requestScaUnregist) {
//         if(requestScaUnregist.getChannelId() == null) {
//             throw new VBCException(ErrorCode.INVALID_ARGUMENT);
//         }
//         //230516 : 해당하는 channel이 있으면 hazelcast 내에서도 삭제하기 위해 사용한다.
//         //230530 : Connection 시 username, password 사용
//         String username = propertiesConfig.getVbgwConnectionUsername();
//         String password = propertiesConfig.getVbgwConnectionPassword();

//         // Base64로 사용자 이름과 비밀번호를 인코딩
//         String credentials = username + ":" + password;


//         //230515 : unregist도 list 형태로 받도록 변경
//         log.info("[deleteChannelUnregist] receive sca unregist channel id : {}", requestScaUnregist.getChannelId());
//         OMSChannels channels = restService.getChannelsById(requestScaUnregist.getChannelId());
//         if(ObjectUtils.isEmpty(channels)) {
//             throw new VBCException(ErrorCode.CHANNEL_NOT_FOUND);
//         }

//         String ep = "";
//         Channel channel = haConfig.getHazelCastMap(channels.getTenantId(), String.valueOf(channels.getChannelNo()));
//         if(ObjectUtils.isNotEmpty(channel)) {
//             channel.getScaRegiInfo().setTrId(requestScaUnregist.getTrId());

//             switch (channel.getRegiStatus()) {
//                 case 0 -> {
//                     //230531 : ep 정보를 json에 넣었다가, 하나씩 빼서 rescan 요청 보낸다. (json 사용)
//                     ep = channel.getPrimary();
//                 }
//                 case 1 -> {
//                     //230531 : ep 정보를 json에 넣었다가, 하나씩 빼서 rescan 요청 보낸다. (json 사용)
//                     ep = channel.getSecondary();
//                 }
//                 default -> {
//                 }
//             }

//             //230508 : unregi 요청
//             //connectionUtil.RestVBGWPostConnector(unregistUrl(ep), jsonObject, credentials);

//             //230602 : unregist 시 username만 필요하다고 하여 수정
//             if (StringUtils.isNotBlank(ep)) {
//                 log.info("[deleteChannelUnregist] Unregister request url : {}", unregistUrl(ep));
//                 connectionUtil.RestVBGWPostConnector(unregistUrl(ep), channel.getScaRegiInfo(), credentials);
//             }

//             log.info("[deleteChannelUnregist] Unregister success by channel json. " + requestScaUnregist.toString());

//             //230516 : 해당하는 channel이 있으면 hazelcast 내에서도 삭제하기 위해 사용한다.
// //        hazelObject.keySet().stream().filter(key -> requestScaUnregist.getChannelId() == Long.valueOf(key)).forEach(obj -> {
// //            hazelObject.remove(obj);
// //        });

//             haConfig.removeHazelCastMap(channels.getTenantId(), String.valueOf(channel.getChannelNo()));
//             log.info("[deleteChannelUnregist] Delete unregistered channels from hazelcast.");
//         }

//         if(StringUtils.isNotBlank(ep)) {
//             //230601 : 임시로 막음 TODO
//             JsonObject nullJsonObject = new JsonObject();
//             //230531 : ep 정보를 set에 넣었다가, 하나씩 빼서 rescan 요청 보낸다. (set 사용)
//             CompletableFuture<Boolean> rescanConnector = connectionUtil.RestVBGWPostConnector(rescanUrl(ep), nullJsonObject, credentials);
//             rescanConnector.thenAcceptAsync(rescanResult -> {
//                 if (rescanResult) {
//                     log.info("[deleteChannelUnregist] Rescan success by channel json. ");
//                     restService.removeOmsChannelOnlineById(requestScaUnregist.getChannelId());
//                     restService.removeOmsChannelsById(requestScaUnregist.getChannelId());
//                 } else {
//                     log.info("[deleteChannelUnregist] Rescan fail by channel json. ");
//                 }
//             });
//         } else {
//             restService.removeOmsChannelOnlineById(requestScaUnregist.getChannelId());
//             restService.removeOmsChannelsById(requestScaUnregist.getChannelId());
//         }

//         return CommonConverter.getCommonResponse();
//     }
// }
