package com.rapeech.vbc.rest.packet;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class AlarmPacket {

    private String ani;
    private String channel ;
    private String calluuid; //세션(콜) ID
    private String type; //알람 유형 (0.지연, 1.장애)
    private String system; //발생 시스템 (0.SOE, 1.STT, 2.TTS)
    private String extension;
    private String currentTime ;
    private String message ; //알람 메시지

    private String center_code; //센터 코드

    private String tenantId; //테넌트 ID

    private String status;  //알람 상태 (0.발생, 1.복구)

    private int alarm_code; //알람 코드

    private Date repaired_at; //알람 복구시각

    private String daily_ymd; //알람 발생일 (yyyymmdd)

    private String daily_hh; //알람 발생시간 (hh)

    //230614 : 트랜잭션 아이디 추가
    private String tr_id;


}
