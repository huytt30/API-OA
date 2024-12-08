package com.example.API_OA.controller;


import com.example.API_OA.service.ZaloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class zaloController {

    private final ZaloService zaloService;

    @Autowired
    public zaloController(ZaloService zaloService) {
        this.zaloService = zaloService;
    }

    @PostMapping
    public void handleIncomingMessage(@RequestBody String requestBody) {
        zaloService.processMessage(requestBody);
    }
}