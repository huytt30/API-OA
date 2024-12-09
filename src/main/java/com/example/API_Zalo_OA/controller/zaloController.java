package com.example.API_Zalo_OA.controller;


import com.example.API_Zalo_OA.service.ZaloService;
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

    @PostMapping
    public ResponseEntity<String> handleIncomingMessage(@RequestBody String requestBody) {
        System.out.println("Received request body: " + requestBody); // Log request body
        try {
            // Call service to process the message and get the generated code
            String generatedCode = zaloService.processMessage(requestBody);

            // Return the generated code as the response body
            return ResponseEntity.ok(generatedCode);  // Returning the generated code as response
        } catch (Exception e) {
            // Log and return error code if there's an issue processing the message
            System.err.println("Error processing the message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the message: " + e.getMessage());
        }
    }
}