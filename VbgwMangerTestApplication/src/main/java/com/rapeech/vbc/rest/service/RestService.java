// package com.rapeech.vbc.rest.service;

// import com.google.gson.JsonArray;
// import com.mysql.cj.log.Log;
// import com.rapeech.vbc.data.entity.OMSChannels;
// import com.rapeech.vbc.rest.model.request.RequestCallChannel;
// import com.rapeech.vbc.rest.packet.AlarmPacket;
// import com.rapeech.vbc.rest.packet.CallAndConversationPacket;
// import com.google.gson.JsonObject;
// import org.json.JSONArray;

// import java.util.List;
// import java.util.Map;

// public interface RestService {

//     List<OMSChannels> getChannelsByIdList(List<Long> idList);

//     OMSChannels getChannelsById(Long channalId);


//     //230507 : 지연,오류 insert
//     void delayAndErrorInsert(AlarmPacket packet);


//     //230512 : 채널 번호를 받아와서, status (채널 상태) 를 반환
//     int findStatusByChannelNo(long channelNo);

//     String findByChannelNo(String channelNo);

//     //230508 : callStart 시 채널 상태 '통화중'으로 변경 = 1
//     //230508 : callEnd 시 채널 상태 '대기' 로 변경 = 0
//     void callStatus(CallAndConversationPacket packet, int status);

//     //230508 : 지연         : delays    - 1
//     //230508 : 오류/장애    : errors    - 2
//     //230508 : 이상징후     : ai_alarms - 3
//     void callAnomarly(AlarmPacket packet);

//     //230512 : SCA에게 값을 전달받아서, 필요 사항을 update 한다.
//     void conversationUpdate(CallAndConversationPacket packet);

//     //230607 : 긴급운용모드 추가
//     JsonObject getUrgentInfo(int vdn);

//     String getCenterCode(String channel);

//     String getScenarioName(String workspaceId);

//     void removeOmsChannelOnlineById(Long channelId);

//     void removeOmsChannelsById(Long channelId);

//     JsonObject getCallChannelStatus(RequestCallChannel requestCallChannel);
// }