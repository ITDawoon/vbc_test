package com.rapeech.vbc.ha.daemon;

import java.io.*;
import java.net.URL;
import org.slf4j.Logger;

import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class VBCSchedular {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // API GET TEST
    @Scheduled(fixedDelayString = "1000")
    public void GetscheduledCron() {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        
        // GET 요청 보내기
        String response = restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", String.class);
        
        // 응답 출력
        logger.debug(response);
        logger.debug("GET Scheule Test");
    }

    // // TODO Regi/UnRegi request part로 수정할 예정
    // // POST request send with String Body has Set
    // @Scheduled(fixedDelayString = "1000") // (임시==1초로 변경함) 5초마다 실행되도록 스케쥴러 설정
    // private void Request() {
    //     String IP = "127.0.0.1";
    //     String Port = "8082";

    //     HttpURLConnection connection = null;

    //     try {
    //         logger.debug("Host IP : " + IP + " Port : " + Port);

    //         String urlString = "http://" + IP + ":" + Port + "/status/callback"; // 스키마와 경로 추가
    //         logger.debug(urlString);

    //         java.net.URL url = new java.net.URL(urlString);
    //         connection = (HttpURLConnection) url.openConnection();

    //         connection.setRequestMethod("POST");
    //         connection.setRequestProperty("Content-Type", "text/plain"); // Content-Type 오타 수정
    //         connection.setConnectTimeout(10000);
    //         connection.setReadTimeout(10000);
    //         connection.setDoOutput(true);

    //         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

    //         bw.write("data"); // 요청 본문에 데이터 쓰기
    //         bw.close();

    //         logger.debug("request sent");

    //         if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
    //             StringBuilder sb = new StringBuilder();
    //             BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

    //             String line;

    //             while ((line = br.readLine()) != null) {
    //                 sb.append(line).append("&");
    //             }
    //             br.close();

    //             String output = sb.toString();
    //             byte[] bytes = output.getBytes();
    //         } else {
    //             logger.error("HTTP error code: " + connection.getResponseCode());
    //         }
    //     } catch (Exception e) {
    //         logger.error("Exception occurred", e); // 예외 처리 및 로깅
    //     } finally {
    //         if (connection != null) {
    //             connection.disconnect(); // 연결 종료
    //         }
    //     }
    // }
}
