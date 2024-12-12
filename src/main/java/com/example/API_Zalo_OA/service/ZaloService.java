package com.example.API_Zalo_OA.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Xử lý tin nhắn nhận được từ người dùng và gửi phản hồi nếu cần.
     *
     * @param requestBody Nội dung JSON từ webhook gửi đến.
     * @return JSON phản hồi hoặc null nếu không cần phản hồi.
     */
    public JSONObject processMessage(String requestBody) {
        try {
            // Parse request body
            JSONObject jsonRequest = new JSONObject(requestBody);

            // Lấy thông tin tin nhắn từ request
            JSONObject message = jsonRequest.optJSONObject("message");

            if (message != null) {
                String userMessage = message.optString("text", "").trim();
                String userId = jsonRequest.getJSONObject("sender").getString("id");

                // Kiểm tra nếu tin nhắn là "wifi"
                if ("wifi".equalsIgnoreCase(userMessage)) {
                    String code = generateCode();
                    String responseText = "Thông tin Wi-Fi của bạn là: " + code;

                    // Gửi tin nhắn phản hồi
                    boolean success = sendMessageToUser(userId, responseText);

                    if (success) {
                        return createDynamicSuccessResponse(userId, responseText);
                    } else {
                        return createErrorResponse("Gửi tin nhắn thất bại");
                    }
                } else {
                    return createErrorResponse("Tin nhắn không được hỗ trợ");
                }
            } else {
                return createErrorResponse("Cấu trúc tin nhắn không hợp lệ");
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            return createErrorResponse("Lỗi xử lý tin nhắn");
        }
    }

    /**
     * Gửi tin nhắn đến người dùng qua Zalo API.
     *
     * @param userId      ID của người dùng nhận tin nhắn.
     * @param messageText Nội dung tin nhắn.
     * @return true nếu gửi thành công, false nếu thất bại.
     */
    public boolean sendMessageToUser(String userId, String messageText) {
        try {
            // Tạo body của request
            JSONObject requestBody = new JSONObject();
            JSONObject recipient = new JSONObject();
            recipient.put("user_id", userId);

            JSONObject message = new JSONObject();
            message.put("text", messageText);

            requestBody.put("recipient", recipient);
            requestBody.put("message", message);

            // Tạo headers với access token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("access_token", accessToken);

            // Gửi request POST đến Zalo API
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(zaloApiUrl, HttpMethod.POST, entity, String.class);

            // Log kết quả phản hồi
            System.out.println("Zalo API Response: " + response.getBody());

            // Kiểm tra nếu gửi thành công (status code 200)
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tạo mã code ngẫu nhiên.
     *
     * @return Mã code dạng "CODE1234".
     */
    private String generateCode() {
        return "CODE" + (int) (Math.random() * 10000);
    }

    /**
     * Tạo phản hồi thành công cho Dynamic OA.
     *
     * @param userId  ID của người dùng.
     * @param message Nội dung phản hồi.
     * @return JSON phản hồi thành công.
     */
    private JSONObject createDynamicSuccessResponse(String userId, String message) {
        JSONObject response = new JSONObject();
        JSONObject recipient = new JSONObject();
        recipient.put("user_id", userId);

        JSONObject messageObj = new JSONObject();
        messageObj.put("text", message);

        response.put("recipient", recipient);
        response.put("message", messageObj);

        return response;
    }

    /**
     * Tạo phản hồi lỗi.
     *
     * @param errorMessage Nội dung lỗi.
     * @return JSON phản hồi lỗi.
     */
    private JSONObject createErrorResponse(String errorMessage) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("status", "error");
        errorResponse.put("message", errorMessage);
        return errorResponse;
    }
}
