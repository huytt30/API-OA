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
        HttpHeaders headers = new HttpHeaders();  // Đảm bảo header được xử lý

        try {
            // Call the service to process the message and get the generated code as a plain string
            String response = zaloService.processMessage(requestBody);
            System.out.println("Mã code: " + response);  // Log the response (code)

            // Nếu không có mã code hoặc tin nhắn không hợp lệ, trả về lỗi
            if (response.isEmpty()) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "No code generated or invalid message");

                // Thiết lập header cho phản hồi lỗi
                headers.set("Content-Type", "application/json");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)  // Trả về mã lỗi 400 nếu không có mã code
                        .headers(headers)  // Thêm header vào phản hồi
                        .body(errorResponse.toString());
            }

            // Tạo JSON phản hồi với mã code
            JSONObject responseJson = new JSONObject();
            responseJson.put("code", response);  // Thêm mã code vào JSON

            // Thiết lập header cho phản hồi thành công
            headers.set("Content-Type", "application/json");

            // Trả về phản hồi dưới dạng JSON với header
            return ResponseEntity.ok()
                    .headers(headers)  // Thêm header vào phản hồi
                    .body(responseJson.toString());  // Trả về mã code dưới dạng JSON

        } catch (Exception e) {
            // Xử lý lỗi và trả về HTTP 500 nếu có lỗi trong quá trình xử lý
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error processing the message: " + e.getMessage());

            // Thiết lập header cho phản hồi lỗi
            headers.set("Content-Type", "application/json");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)  // Thêm header vào phản hồi lỗi
                    .body(errorResponse.toString());  // Đảm bảo trả về JSON cho lỗi
        }
    }
}
