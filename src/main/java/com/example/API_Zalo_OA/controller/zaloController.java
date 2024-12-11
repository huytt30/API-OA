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
        HttpHeaders headers = new HttpHeaders();
        try {
            // Call the service to process the message and get the response
            JSONObject response = zaloService.processMessage(requestBody);
            String jsonResponse = response.toString();  // Convert JSONObject to String

            // Set the correct Content-Type and Content-Length headers
            headers.set("Content-Type", "application/json");
            headers.set("Content-Length", String.valueOf(jsonResponse.length()));

            // Return the response as a JSON object with the headers
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(jsonResponse); // Return the response as a JSON body

        } catch (Exception e) {
            // Handle errors and return an HTTP 500 with an error message
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error processing the message: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(errorResponse.toString());  // Ensure error response is returned in JSON format
        }
    }
}


