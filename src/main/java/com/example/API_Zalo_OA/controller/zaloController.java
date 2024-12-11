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
        return ResponseEntity.ok(challenge);
    }

    // Xử lý yêu cầu POST để nhận và xử lý tin nhắn từ Zalo
    @PostMapping
    public ResponseEntity<String> handleIncomingMessage(@RequestBody String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        try {
            JSONObject response = zaloService.processMessage(requestBody);
            String jsonResponse = response.toString();

            headers.set("Content-Type", "application/json");
            headers.set("Content-Length", String.valueOf(jsonResponse.length()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(jsonResponse);

        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error processing the message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(errorResponse.toString());
        }
    }

    // Endpoint để lấy access token
    @GetMapping("/get-access-token")
    public ResponseEntity<String> getAccessToken(@RequestParam(value = "code", required = true) String code) {
        String accessToken = zaloService.getAccessToken(code);
        return ResponseEntity.ok(accessToken);
    }
}


