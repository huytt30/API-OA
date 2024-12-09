package com.example.API_Zalo_OA.controller;


import com.example.API_Zalo_OA.service.ZaloService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class zaloController {

    private ZaloService zaloService;

    @Autowired
    public zaloController(ZaloService zaloService) {
        this.zaloService = zaloService;
    }

    // Xử lý yêu cầu GET cho việc xác thực webhook từ Zalo (thường có tham số "challenge")
    @GetMapping
    public ResponseEntity<String> verifyWebhook(@RequestParam(value = "challenge", required = true) String challenge) {
        // Đảm bảo trả về giá trị của 'challenge' để Zalo OA xác thực webhook
        return ResponseEntity.ok(challenge);
    }

    // Xử lý yêu cầu POST để nhận và xử lý tin nhắn từ Zalo
    @PostMapping
    public ResponseEntity<String> handleIncomingMessage(@RequestBody String requestBody) {
        try {
            // Call the service to process the message and get the generated code as a plain string
            String response = zaloService.processMessage(requestBody);
            System.out.println("Mã code: " + response);  // Log the response (code)

            // Create a JSONObject to return the response as JSON
            JSONObject responseJson = new JSONObject();
            responseJson.put("code", response);  // Add the response (code) to the JSON

            // Convert JSONObject to String
            String jsonResponse = responseJson.toString();

            // Add Content-Length header to ensure no chunked transfer encoding
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Length", String.valueOf(jsonResponse.length()));

            // Return the response as a JSON object with Content-Length header
            return ResponseEntity.ok()
                    .headers(headers) // Add Content-Length header
                    .body(jsonResponse); // Return response body as a String

        } catch (Exception e) {
            // Handle errors and return an HTTP 500 with an error message
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error processing the message: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse.toString());  // Ensure JSON is returned as String
        }
    }
}