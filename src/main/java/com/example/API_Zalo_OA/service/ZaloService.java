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
    

    public JSONObject processMessage(String requestBody) {
        JSONObject responseJson = new JSONObject();  // Create a JSONObject to hold the response

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
                    responseJson.put("code", code);  // Put the generated code in the response JSON
                } else {
                    responseJson.put("code", "");  // If the message is not "wifi", return an empty code
                }
            } else {
                responseJson.put("error", "Invalid message structure");  // If message field is missing
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            responseJson.put("error", "Error processing message");  // Add an error field in case of failure
        }

        // Return the JSON response
        return responseJson;
    }

    private String generateCode() {
        // Generate a random code (e.g., CODE1234)
        return "CODE" + (int) (Math.random() * 10000);
    }
//    private void sendMessageToUser(String userId, String message) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("recipient", new JSONObject().put("user_id", userId));
//        jsonObject.put("message", new JSONObject().put("text", message));
//
//        String url = zaloApiUrl + "?access_token=" + accessToken;
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
//
//        try {
//            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
//            System.out.println("Response from Zalo API: " + response);
//
//            if (response == null || !response.contains("success")) {
//                System.err.println("Failed to send message: " + response);
//            }
//        } catch (HttpClientErrorException | HttpServerErrorException e) {
//            System.err.println("Error sending message: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
//        } catch (Exception e) {
//            System.err.println("Error sending message: " + e.getMessage());
//        }
//    }
}






