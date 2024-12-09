package com.example.API_Zalo_OA.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ZaloService {
    @Value("${zalo.api.url}")
    private String zaloApiUrl;

    @Value("${zalo.access_token}")
    private String accessToken;

    private static final RestTemplate restTemplate = new RestTemplate();

    public void processMessage(String requestBody) {
        try {
            JSONObject jsonRequest = new JSONObject(requestBody);
            String userMessage = jsonRequest.optString("message", "").trim();

            if ("wifi".equalsIgnoreCase(userMessage)) {
                String code = generateCode();
                sendMessageToUser(jsonRequest.getString("sender_id"), code);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private String generateCode() {
        return "CODE" + (int) (Math.random() * 10000);
    }

    private void sendMessageToUser(String userId, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("recipient", new JSONObject().put("user_id", userId));
        jsonObject.put("message", new JSONObject().put("text", message));

        String url = zaloApiUrl + "?access_token=" + accessToken;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

        try {
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
            System.out.println("Response from Zalo API: " + response);

            if (response == null || !response.contains("success")) {
                System.err.println("Failed to send message: " + response);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error sending message: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}






