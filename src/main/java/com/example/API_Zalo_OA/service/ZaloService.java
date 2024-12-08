package com.example.API_Zalo_OA.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ZaloService {

    @Value("${zalo.api.url}")
    private String zaloApiUrl;

    @Value("${zalo.access_token}")
    private String accessToken;

    public void processMessage(String requestBody) {
        // Chuyển đổi dữ liệu nhận được từ webhook thành JSONObject
        JSONObject jsonRequest = new JSONObject(requestBody);
        JSONObject message = jsonRequest.getJSONObject("message");
        String userMessage = message.getString("text");
        String userId = jsonRequest.getJSONObject("sender").getString("id");

        // Kiểm tra nếu người dùng gửi từ khóa "wifi"
        if (userMessage.equalsIgnoreCase("wifi")) {
            // Sinh mã code ngẫu nhiên
            String code = generateCode();
            // Gửi mã code lại cho người dùng
            sendMessageToUser(userId, "Your code is: " + code);
        } else {
            // Gửi phản hồi mặc định nếu không phải từ khóa "wifi"
            sendMessageToUser(userId, "Sorry, I didn't understand that.");
        }
    }

    private String generateCode() {
        return "CODE" + (int) (Math.random() * 10000);  // Sinh mã ngẫu nhiên
    }

    private void sendMessageToUser(String userId, String message) {
        // Tạo JSON request body cho việc gửi tin nhắn
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("recipient", new JSONObject().put("user_id", userId));
        jsonObject.put("message", new JSONObject().put("text", message));

        // Gửi request tới API Zalo
        String url = zaloApiUrl + "?access_token=" + accessToken;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            // Gửi yêu cầu POST tới API Zalo
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            // Log response từ Zalo API
            System.out.println("Response from Zalo API: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending message to user: " + e.getMessage());
        }
    }
}
