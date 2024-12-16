package com.example.API_Zalo_OA.service;

import com.example.API_Zalo_OA.model.voucher;
import com.example.API_Zalo_OA.repository.VoucherRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Service
public class ZaloService {

    @Value("${zalo.api.url}")
    private String zaloApiUrl;

    @Value("${zalo.access_token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private VoucherRepository voucherRepository;

    /**
     * Xử lý tin nhắn nhận được từ người dùng và gửi phản hồi nếu cần.
     *
     * @param requestBody Nội dung JSON từ webhook gửi đến.
     * @return JSON phản hồi hoặc null nếu không cần phản hồi.
     */
    public JSONObject processMessage(String requestBody) {
        try {
            // Parse request body
            JSONObject jsonRequest = new JSONObject(requestBody);

            // Lấy thông tin tin nhắn từ request
            JSONObject message = jsonRequest.optJSONObject("message");

            if (message != null) {
                String userMessage = message.optString("text", "").trim();
                String userId = jsonRequest.getJSONObject("sender").getString("id");

                // Kiểm tra nếu tin nhắn là "wifi"
                if ("wifi".equalsIgnoreCase(userMessage)) {
                    // Lấy mã từ database có trạng thái SUCCESS
                    String code = generateCodeFromDatabase();

                    if (code == null) {
                        return createErrorResponse("Không có mã Wi-Fi nào với trạng thái SUCCESS trong cơ sở dữ liệu");
                    }

                    // Trả về phản hồi với mã code
                    String responseText = "Thông tin Wi-Fi của bạn là: " + code;
                    return createDynamicSuccessResponse(responseText);
                } else {
                    return createErrorResponse("Tin nhắn không được hỗ trợ");
                }
            } else {
                return createErrorResponse("Cấu trúc tin nhắn không hợp lệ");
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            return createErrorResponse("Lỗi xử lý tin nhắn: " + e.getMessage());
        }
    }

    /**
     * Lấy mã từ cơ sở dữ liệu có trạng thái SUCCESS.
     *
     * @return Mã code từ cơ sở dữ liệu.
     */
    private String generateCodeFromDatabase() {
        // Truy vấn tất cả các voucher có trạng thái SUCCESS
        List<voucher> successfulVouchers = voucherRepository.findByStatus("SUCCESS");

        // Kiểm tra nếu có mã nào với trạng thái SUCCESS
        if (successfulVouchers.isEmpty()) {
            return null;  // Không có mã nào với trạng thái SUCCESS
        }

        // Chọn ngẫu nhiên một mã từ danh sách
        Random random = new Random();
        voucher selectedVoucher = successfulVouchers.get(random.nextInt(successfulVouchers.size()));

        // Trả về mã code từ voucher đã chọn
        return selectedVoucher.getCode();
    }

    /**
     * Gửi tin nhắn đến người dùng qua Zalo API.
     *
     * @param userId      ID của người dùng nhận tin nhắn.
     * @param messageText Nội dung tin nhắn.
     * @return true nếu gửi thành công, false nếu thất bại.
     */
    public boolean sendMessageToUser(String userId, String messageText) {
        try {
            // Tạo body của request
            JSONObject requestBody = new JSONObject();
            JSONObject recipient = new JSONObject();
            recipient.put("user_id", userId);

            JSONObject message = new JSONObject();
            message.put("text", messageText);

            requestBody.put("recipient", recipient);
            requestBody.put("message", message);

            // Tạo headers với access token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("access_token", accessToken);

            // Gửi request POST đến Zalo API
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(zaloApiUrl, HttpMethod.POST, entity, String.class);

            // Log kết quả phản hồi
            System.out.println("Zalo API Response: " + response.getBody());

            // Kiểm tra nếu gửi thành công (status code 200)
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tạo phản hồi thành công cho Dynamic OA với nội dung chỉ chứa text.
     *
     * @param message Nội dung phản hồi.
     * @return JSON phản hồi thành công chỉ chứa text.
     */
    private JSONObject createDynamicSuccessResponse(String message) {
        JSONObject response = new JSONObject();
        response.put("text", message);
        return response;
    }

    /**
     * Tạo phản hồi lỗi.
     *
     * @param errorMessage Nội dung lỗi.
     * @return JSON phản hồi lỗi.
     */
    private JSONObject createErrorResponse(String errorMessage) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("status", "error");
        errorResponse.put("message", errorMessage);
        return errorResponse;
    }
}