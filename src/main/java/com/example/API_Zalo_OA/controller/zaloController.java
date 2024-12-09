package com.example.API_Zalo_OA.controller;


import com.example.API_Zalo_OA.service.ZaloService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class zaloController {

    private  ZaloService zaloService;

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
    public ResponseEntity<JSONObject> handleIncomingMessage(@RequestBody String requestBody) {
        try {
            // Call the service to process the message and get the generated code in JSON format
            JSONObject responseJson = zaloService.processMessage(requestBody);
            System.out.println("Mã code: " + responseJson);  // Log the JSON response

            // Return the generated code wrapped in a JSON response
            return ResponseEntity.ok(responseJson);
        } catch (Exception e) {
            // Handle errors and return an HTTP 500 with an error message
            JSONObject errorResponse = new JSONObject().put("error", "Error processing the message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}