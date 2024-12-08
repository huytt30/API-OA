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
    public ResponseEntity<String> getWebhookInfo() {
        return ResponseEntity.ok("Webhook expects POST requests.");
    }

    @PostMapping
    public void handleIncomingMessage(@RequestBody String requestBody) {
        zaloService.processMessage(requestBody);
    }
}