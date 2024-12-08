package com.example.API_Zalo_OA.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ZaloService {
    @Value("${zalo.api.url}")
    private String zaloApiUrl;

    @Value("${zalo.access_token}")
    private String accessToken;

    public void processMessage(String requestBody) {
        // Giả sử bạn đã có đoạn mã JSON yêu cầu
        JSONObject jsonRequest = new JSONObject(requestBody);

        // Kiểm tra nếu người dùng nhắn tin với từ khóa "wifi"
        String userMessage = jsonRequest.getString("message");
        if (userMessage.equalsIgnoreCase("wifi")) {
            // Sinh mã code ngẫu nhiên
            String code = generateCode();

            // Gửi mã code lại cho người dùng
            sendMessageToUser(jsonRequest.getString("sender_id"), code);
        }
    }

    private String generateCode() {
        return "CODE" + (int) (Math.random() * 10000);  // Sinh mã ngẫu nhiên
    }

    private void sendMessageToUser(String userId, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("recipient", new JSONObject().put("user_id", userId));
        jsonObject.put("message", new JSONObject().put("text", message));

        // Gửi request đến API Zalo OA
        String url = zaloApiUrl + "?access_token=" + accessToken;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
