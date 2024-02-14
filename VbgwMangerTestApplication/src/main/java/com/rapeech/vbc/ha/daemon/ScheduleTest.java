package com.rapeech.vbc.ha.daemon;

//import org.apache.hc.core5.http.HttpHeaders;
import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@EnableScheduling
public class ScheduleTest {

    // API GET TEST
    @Scheduled(cron = "0/5 * * * * ?")
    public void GetscheduledCron() {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        
        // GET 요청 보내기
        String response = restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", String.class);
        
        // 응답 출력
        System.out.println(response);
        System.out.println("GET Schedule Test");
    }

    // // API POST TEST
    // @Scheduled(cron = "0/5 * * * * ?")
    // public void PostscheduledCron() {
    //     // RestTemplate 객체 생성
    //     RestTemplate restTemplate = new RestTemplate();

    //     // API 엔드포인트 URL 설정
    //     String url = "http://example.com/api/endpoint";

    //     // HTTP 요청 헤더 설정
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);

    //     // HTTP 요청 본문 데이터 설정
    //     String requestBody = "{\"key\": \"value\"}";

    //     // HTTP 요청 엔티티 생성
    //     HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

    //     // POST 요청 보내고 응답 받기
    //     ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

    //     // 응답 출력
    //     System.out.println(responseEntity.getBody());
    // }
    

     // init 5초 후 5초마다 반복 실행 스케줄링
     @Scheduled(fixedDelayString = "5000",  initialDelay = 5000)
     public void test(){
        System.out.println("Fixed Delay Test");
     }
}