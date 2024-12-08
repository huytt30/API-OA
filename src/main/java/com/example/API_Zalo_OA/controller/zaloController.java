package com.example.API_Zalo_OA.controller;


import com.example.API_Zalo_OA.service.ZaloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class zaloController {

    private final ZaloService zaloService;

    @Autowired
    public zaloController(ZaloService zaloService) {
        this.zaloService = zaloService;
    }

    @GetMapping
    public ResponseEntity<String> verifyWebhook(@RequestParam(value = "challenge", required = true) String challenge) {
        // Đảm bảo trả về đúng giá trị của 'challenge' để Zalo OA xác thực webhook
        return ResponseEntity.ok(challenge);
    }

    @PostMapping
    public void handleIncomingMessage(@RequestBody String requestBody) {
        zaloService.processMessage(requestBody);
    }
}