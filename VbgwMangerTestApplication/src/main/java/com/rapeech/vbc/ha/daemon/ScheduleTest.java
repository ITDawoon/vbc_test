package com.rapeech.vbc.ha.daemon;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@EnableScheduling
public class ScheduleTest {

    @Scheduled(cron = "0/5 * * * * ?")
    public void scheduledCron() {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        
        // GET 요청 보내기
        String response = restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", String.class);
        
        // 응답 출력
        System.out.println(response);
        System.out.println("Schedule Test");
    }

     // init 5초 후 5초마다 반복 실행 스케줄링
     @Scheduled(fixedDelayString = "5000",  initialDelay = 5000)
     public void test(){
        System.out.println("Fixed Delay Test");
     }
}