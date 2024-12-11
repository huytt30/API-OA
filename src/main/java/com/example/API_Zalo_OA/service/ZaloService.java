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

@Service
public class ZaloService {

    @Value("${zalo.app_id}")
    private String appId;

    @Value("${zalo.app_secret}")
    private String appSecret;

    @Value("${zalo.redirect_uri}")
    private String redirectUri;

    @Value("${zalo.api_url}")
    private String apiUrl;

    // Để lưu access token, bạn có thể lưu trong bộ nhớ, cache hoặc database
    private String accessToken;

    public JSONObject processMessage(String requestBody) {
        try {
            // Parse the request body
            JSONObject jsonRequest = new JSONObject(requestBody);
            JSONObject message = jsonRequest.optJSONObject("message");

            if (message != null) {
                String userMessage = message.optString("text", "").trim(); // Get the "text" field value

                // Nếu tin nhắn là "wifi", gửi mã code
                if ("wifi".equalsIgnoreCase(userMessage)) {
                    String code = generateCode();
                    JSONObject response = new JSONObject();
                    JSONObject recipient = new JSONObject();
                    recipient.put("user_id", jsonRequest.getJSONObject("recipient").getString("id"));

                    JSONObject responseMessage = new JSONObject();
                    responseMessage.put("text", "Mã code: " + code);

                    response.put("recipient", recipient);
                    response.put("message", responseMessage);

                    return response;
                } else {
                    return createErrorResponse("Message not recognized");
                }
            } else {
                return createErrorResponse("Invalid message structure");
            }
        } catch (Exception e) {
            return createErrorResponse("Error processing message: " + e.getMessage());
        }
    }

    // Lấy access token bằng authorization code
    public String getAccessToken(String authorizationCode) {
        try {
            String url = apiUrl + "?app_id=" + appId + "&app_secret=" + appSecret +
                    "&code=" + authorizationCode + "&redirect_uri=" + redirectUri;

            // Sử dụng HttpClient để gửi yêu cầu POST
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());

            if (jsonResponse.has("access_token")) {
                accessToken = jsonResponse.getString("access_token");
                return accessToken;
            } else {
                return "Error: Unable to get access token";
            }

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String generateCode() {
        return "CODE" + (int) (Math.random() * 10000);  // Example code like CODE1234
    }

    private JSONObject createErrorResponse(String errorMessage) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }
}

