package com.rapeech.vbc.ha.daemon;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduleTest {

    @Scheduled(cron = "0/5 * * * * ?")
    public void scheduledCron() {
        System.out.println("Schedule Test");
    }

     // init 5초 후 5초마다 반복 실행 스케줄링
     @Scheduled(fixedDelayString = "5000",  initialDelay = 5000)
     public void test(){
        System.out.println("Fixed Delay Test");
     }
}