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

    public String processMessage(String requestBody) {
        try {
            // Parse the incoming JSON request body
            JSONObject jsonRequest = new JSONObject(requestBody);
            JSONObject message = jsonRequest.optJSONObject("message");
            String userMessage = message != null ? message.optString("text", "").trim() : "";

            // Check if the message contains "wifi"
            if ("wifi".equalsIgnoreCase(userMessage)) {
                String code = generateCode();  // Generate a code
                JSONObject sender = jsonRequest.optJSONObject("sender");
                if (sender != null) {
                    String senderId = sender.optString("id", "");  // Extract sender ID
                    sendMessageToUser(senderId, code);  // Send code back to the sender
                }
                return code;  // Return the generated code
            }

            // If message does not contain "wifi", return an empty response or custom message
            return "No action taken";
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            return "";  // Return empty if there's an error
        }
    }

    private String generateCode() {
        // Generate a random code (e.g., CODE1234)
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



