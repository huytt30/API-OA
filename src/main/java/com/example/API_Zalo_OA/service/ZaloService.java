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
        try {
            // Chuyển đổi requestBody thành JSONObject
            JSONObject jsonRequest = new JSONObject(requestBody);

            // Lấy tin nhắn từ người dùng
            String userMessage = jsonRequest.optString("message", "").trim();

            // Kiểm tra nếu người dùng nhắn tin với từ khóa "wifi"
            if ("wifi".equalsIgnoreCase(userMessage)) {
                // Sinh mã code ngẫu nhiên
                String code = generateCode();

                // Gửi mã code lại cho người dùng
                sendMessageToUser(jsonRequest.getString("sender_id"), code);
            }
        } catch (Exception e) {
            // Log lỗi và thông báo nếu có lỗi trong quá trình xử lý
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private String generateCode() {
        return "CODE" + (int) (Math.random() * 10000);  // Sinh mã ngẫu nhiên
    }

    private static final RestTemplate restTemplate = new RestTemplate();  // Để tái sử dụng

    private void sendMessageToUser(String userId, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("recipient", new JSONObject().put("user_id", userId));
        jsonObject.put("message", new JSONObject().put("text", message));

        // Gửi request đến API Zalo OA
        String url = zaloApiUrl + "?access_token=" + accessToken;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

        try {
            // Gửi POST request và nhận phản hồi
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();

            // Log phản hồi từ Zalo OA (hoặc xử lý nếu cần)
            System.out.println("Response from Zalo API: " + response);
        } catch (Exception e) {
            // Log lỗi nếu có vấn đề với việc gọi API Zalo
            System.err.println("Error sending message to Zalo API: " + e.getMessage());
        }
    }
}
