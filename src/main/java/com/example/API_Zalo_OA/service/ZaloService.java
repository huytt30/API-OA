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

                    // Send the code back to the user
                    sendMessageToUser(jsonRequest.optString("sender", ""), code);  // Send code to the user

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

    private void sendMessageToUser(String userId, String message) {
        // Prepare the JSON object for the request to send a message
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("message", message);

        // Send request to Zalo API
        try {
            String url = zaloApiUrl;
            JSONObject payload = new JSONObject();
            payload.put("recipient", new JSONObject().put("user_id", userId));  // User ID of the recipient
            payload.put("message", new JSONObject().put("text", message));

            // Set the access token for the API request
            payload.put("access_token", accessToken);

            // Send HTTP POST request to Zalo API to send the message
            // Example using HttpURLConnection (or you can use libraries like RestTemplate or HttpClient)
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the API
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully sent message
                System.out.println("Message sent successfully.");
            } else {
                // Failed to send message
                System.err.println("Failed to send message. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}





