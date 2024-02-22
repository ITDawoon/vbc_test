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

    private final RestTemplate restTemplate;

    public VBCSchedular(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Server 상태값 가져오는 method
    @Scheduled(fixedDelayString = "1000")
    public void GetscheduledCron() {
        //String url = "http://127.0.0.1:8082"; // 상대 서버의 URL을 여기에 입력하세요
        String url = "https://jsonplaceholder.typicode.com/posts";

        try {
            String response = restTemplate.getForObject(url, String.class);
            // 응답을 받아와서 처리하는 코드 작성
            System.out.println("Status Code: " + response);
        } catch (Exception e) {
            // 예외 처리 (서버 응답이 없을 때 등)
            System.err.println("HTTP error code : " + e.getMessage());
        }
    }

    // TODO Regi/UnRegi request part로 수정할 예정
    // POST request send with String Body has Set
    @Scheduled(fixedDelayString = "10000") // (임시==1초로 변경함) 5초마다 실행되도록 스케쥴러 설정
    private void Request() {
        String IP = "127.0.0.1";
        String Port = "8082";

        HttpURLConnection connection = null;

        try {
            logger.debug("Host IP : " + IP + " Port : " + Port);

            String urlString = "http://" + IP + ":" + Port + "/status/callback"; // 스키마와 경로 추가
            
            logger.debug(urlString);

            java.net.URL url = new java.net.URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            // JSON DATA Send TEST DATA
            //String jsonInputString = "{\"id\": 6, \"title\": \"  \", \"body\": \"  \"}";

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

            // JSON DATA Send TEST
            //bw.write(jsonInputString);
            bw.write("data");
            bw.close();

            logger.debug("request sent");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder sb = new StringBuilder();
                // 응답 본문 확인 (예: 서버가 반환하는 데이터)
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line).append("&");
                }
                br.close();

                String output = sb.toString();
                byte[] bytes = output.getBytes();
            } else {
                logger.error("HTTP error code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            logger.error("Exception occurred", e); // 예외 처리 및 로깅
        } finally {
            if (connection != null) {
                connection.disconnect(); // 연결 종료
            }
        }
    }
}
