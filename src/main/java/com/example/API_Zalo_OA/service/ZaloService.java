package com.example.API_Zalo_OA.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ZaloService {

    @Value("${zalo.api.url}")
    private String zaloApiUrl;

    @Value("${zalo.access_token}")
    private String accessToken;

    public String processMessage(String requestBody) {
        try {
            // Parse the request body
            JSONObject jsonRequest = new JSONObject(requestBody);

            // Get the "message" object from the incoming request
            JSONObject message = jsonRequest.optJSONObject("message");

            // Check if the message object is present and contains a "text" field
            if (message != null) {
                String userMessage = message.optString("text", "").trim(); // Get the "text" field value

                // If the "text" field is "wifi", generate and return a code
                if ("wifi".equalsIgnoreCase(userMessage)) {
                    String code = generateCode();  // Generate code if message is "wifi"
                    return code;  // Return the generated code as a plain string
                } else {
                    return "";  // If the message is not "wifi", return an empty string
                }
            } else {
                return "Invalid message structure";  // If message field is missing
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            return "Error processing message";  // Return error message if an exception occurs
        }
    }

    private String generateCode() {
        // Generate a random code (e.g., CODE1234)
        return "CODE" + (int) (Math.random() * 10000);  // Example code like CODE1234
    }
}







