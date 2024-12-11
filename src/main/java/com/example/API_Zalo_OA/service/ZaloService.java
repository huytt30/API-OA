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

                    // Create the JSON response
                    JSONObject response = new JSONObject();

                    // Prepare recipient (recipient is the user we are sending the message to)
                    JSONObject recipient = new JSONObject();
                    recipient.put("user_id", jsonRequest.getJSONObject("recipient").getString("id"));

                    // Prepare the message
                    JSONObject responseMessage = new JSONObject();
                    responseMessage.put("text", "Mã code của bạn: " + code);

                    // Add recipient and message to the response
                    response.put("recipient", recipient);
                    response.put("message", responseMessage);

                    return response;  // Return the response as a valid JSON object
                } else {
                    return createErrorResponse("Message not recognized");  // If the message is not "wifi", return an error
                }
            } else {
                return createErrorResponse("Invalid message structure");  // If there's no message field, return an error
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            return createErrorResponse("Error processing message");  // Return an error response if an exception occurs
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

