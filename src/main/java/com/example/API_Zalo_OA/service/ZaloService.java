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

    // Phương thức xử lý tin nhắn người dùng
    public void processMessage(String requestBody) throws Exception {
        JSONObject jsonRequest = new JSONObject(requestBody);

        // Kiểm tra nếu người dùng nhắn tin với từ khóa "wifi"
        String userMessage = jsonRequest.getString("message");
        if (userMessage.equalsIgnoreCase("wifi")) {
            // Sinh mã code ngẫu nhiên
            String code = generateCode();

            // Gửi mã code lại cho người dùng
            sendMessageToUser(jsonRequest.getString("sender_id"), code);
        } else {
            // Nếu tin nhắn không phải "wifi", có thể xử lý các tin nhắn khác ở đây
            sendMessageToUser(jsonRequest.getString("sender_id"), "Sorry, I didn't understand that.");
        }
    }

    // Phương thức sinh mã code ngẫu nhiên
    private String generateCode() {
        return "CODE" + (int) (Math.random() * 10000);  // Sinh mã ngẫu nhiên
    }

    // Phương thức gửi tin nhắn cho người dùng
    private void sendMessageToUser(String userId, String message) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("recipient", new JSONObject().put("user_id", userId));
        jsonObject.put("message", new JSONObject().put("text", message));

        // Gửi request đến API Zalo OA
        String url = zaloApiUrl + "?access_token=" + accessToken;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            // Gửi yêu cầu POST tới API Zalo
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            // Nếu có lỗi, log lỗi và ném ra Exception
            System.err.println("Error sending message to user: " + e.getMessage());
            throw new Exception("Error sending message to user", e);
        }
    }
}