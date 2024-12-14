package com.example.API_Zalo_OA.controller;


import com.example.API_Zalo_OA.service.ZaloService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    /**
     * Xác thực webhook từ Zalo (xử lý yêu cầu GET với tham số 'challenge').
     *
     * @param challenge Tham số challenge từ Zalo.
     * @return Trả về giá trị của challenge để xác thực.
     */
    @GetMapping
    public ResponseEntity<String> verifyWebhook(@RequestParam(value = "challenge") String challenge) {
        return ResponseEntity.ok(challenge);  // Xác thực webhook từ Zalo
    }

    /**
     * Xử lý tin nhắn nhận được từ Zalo (xử lý yêu cầu POST).
     *
     * @param requestBody Nội dung JSON từ webhook gửi đến.
     * @return Phản hồi JSON sau khi xử lý hoặc thông báo lỗi.
     */
    @PostMapping
    public ResponseEntity<String> handleIncomingMessage(@RequestBody String requestBody) {
        try {
            // Gọi service để xử lý tin nhắn và nhận phản hồi JSON
            JSONObject response = zaloService.processMessage(requestBody);

            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Trả về phản hồi chứa mã code (không gửi tin nhắn)
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.toString());  // Trả về JSON phản hồi chứa mã code

        } catch (Exception e) {
            // Xử lý lỗi và trả về phản hồi lỗi JSON
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Lỗi xử lý tin nhắn: " + e.getMessage());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Trả về phản hồi lỗi nếu có ngoại lệ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(errorResponse.toString());
        }
    }
}
