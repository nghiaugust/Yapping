package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.service.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fcm")
public class FCMController {

    @Autowired
    private FCMService fcmService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/token")
    public ResponseEntity<ApiResponse> updateFCMToken(@RequestBody Map<String, String> request) {
        try {
            // Lấy userId từ authentication
            System.out.println("=== FCM TOKEN UPDATE DEBUG ===");
            System.out.println("Request body: " + request);
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext()
                    .getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            System.out.println("User ID from token: " + userId);
            String fcmToken = request.get("fcmToken");
            System.out.println("FCM Token received: " + (fcmToken != null ? fcmToken.substring(0, 20) + "..." : "null"));
            System.out.println("FCM Token length: " + (fcmToken != null ? fcmToken.length() : 0));

            if (fcmToken == null || fcmToken.trim().isEmpty()) {
                System.out.println("ERROR: FCM token is null or empty");
                return ResponseEntity.badRequest().body(new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        false,
                        "FCM token không được để trống",
                        null
                ));
            }

            System.out.println("Calling fcmService.updateUserFCMToken...");
            fcmService.updateUserFCMToken(userId, fcmToken);
            System.out.println("FCM token updated successfully!");

            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật FCM token thành công",
                    null
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERROR in updateFCMToken: " + e.getMessage());
            e.printStackTrace();
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Lỗi khi cập nhật FCM token: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/token")
    public ResponseEntity<ApiResponse> removeFCMToken() {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext()
                    .getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            fcmService.removeUserFCMToken(userId);

            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa FCM token thành công",
                    null
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Lỗi khi xóa FCM token: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/test")
    public ResponseEntity<ApiResponse> testNotification(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String title = (String) request.get("title");
            String body = (String) request.get("body");

            fcmService.sendNotificationToUser(userId, title, body, null, null, null);

            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã gửi thông báo test thành công",
                    null
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Lỗi khi gửi thông báo test: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}