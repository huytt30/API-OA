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

    @GetMapping
    public ResponseEntity<String> verifyWebhook(@RequestParam(value = "challenge", required = true) String challenge) {
        // Trả lại giá trị challenge để Zalo OA xác thực Webhook
        return ResponseEntity.ok(challenge);  // Trả về chính xác giá trị 'challenge' mà Zalo OA gửi đến
    }

    @PostMapping
    public ResponseEntity<String> handleIncomingMessage(@RequestBody String requestBody) {
        // Đây là nơi nhận tin nhắn từ Zalo OA (có thể bỏ qua việc xử lý nội dung nếu không cần thiết)
        // Chỉ cần trả về HTTP 200 OK mà không làm gì thêm
        return ResponseEntity.ok("OK");  // Trả về HTTP 200 OK mà không cần xử lý thêm
    }
}