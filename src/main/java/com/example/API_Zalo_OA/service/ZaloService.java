package com.example.API_Zalo_OA.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ZaloService {

    @Value("${zalo.api.url}")
    private String zaloApiUrl;

    @Value("${zalo.access_token}")
    private String accessToken;

    public JSONObject processMessage(String requestBody) {
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
                    // Create response JSON with recipient and message
                    JSONObject response = new JSONObject();

                    // Get the user_id from the incoming request
                    JSONObject sender = jsonRequest.optJSONObject("sender");
                    String userId = sender != null ? sender.optString("id", "") : "";

                    // Prepare recipient
                    JSONObject recipient = new JSONObject();
                    recipient.put("user_id", userId);

                    // Prepare message with the code
                    JSONObject responseMessage = new JSONObject();
                    responseMessage.put("text", "Mã code: " + code);

                    // Add recipient and message to the response
                    response.put("recipient", recipient);
                    response.put("message", responseMessage);

                    return response;  // Return the response JSON
                } else {
                    return createErrorResponse("Message not recognized");  // Return error response for unrecognized message
                }
            } else {
                return createErrorResponse("Invalid message structure");  // Return error response if message is missing
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            return createErrorResponse("Error processing message");  // Return error response if an exception occurs
        }
    }

    private String generateCode() {
        // Generate a random code (e.g., CODE1234)
        return "CODE" + (int) (Math.random() * 10000);  // Example code like CODE1234
    }

    private JSONObject createErrorResponse(String errorMessage) {
        // Helper method to create error response
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }
}

