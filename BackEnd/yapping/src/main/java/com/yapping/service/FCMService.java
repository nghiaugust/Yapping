package com.yapping.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.yapping.entity.Notification.Type;
import com.yapping.entity.User;
import com.yapping.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static ch.qos.logback.core.util.AggregationType.NOT_FOUND;

@Service
public class FCMService {

    private static final Logger logger = LoggerFactory.getLogger(FCMService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    public void sendNotificationToUser(Long userId, String title, String body, Type type, Long targetId, Long actorId) {
        try {

            System.out.println("=== SEND NOTIFICATION DEBUG ===");
            System.out.println("Target User ID: " + userId);
            System.out.println("Title: " + title);
            System.out.println("Body: " + body);
            System.out.println("Type: " + type);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            System.out.println("User found: " + user.getUsername() + " (ID: " + user.getId() + ")");
            System.out.println("User FCM token: " + (user.getFcmToken() != null ? user.getFcmToken().substring(0, 20) + "..." : "NULL"));
            System.out.println("Token length: " + (user.getFcmToken() != null ? user.getFcmToken().length() : 0));

            if (user.getFcmToken() == null || user.getFcmToken().isEmpty()) {
                logger.warn("User {} does not have FCM token", userId);
                return;
            }

            System.out.println("Calling sendPushNotification...");
            sendPushNotification(user.getFcmToken(), title, body, type, targetId, actorId);

        } catch (Exception e) {
            System.err.println("ERROR in sendNotificationToUser: " + e.getMessage());
            e.printStackTrace();
            logger.error("Error sending notification to user {}: {}", userId, e.getMessage());
        }
    }

    public void sendPushNotification(String fcmToken, String title, String body, Type type, Long targetId, Long actorId) {
        try {
            System.out.println("=== SEND PUSH NOTIFICATION DEBUG ===");
            System.out.println("FCM Token: " + fcmToken.substring(0, 20) + "...");
            System.out.println("Token length: " + fcmToken.length());
            System.out.println("Title: " + title);
            System.out.println("Body: " + body);
            System.out.println("Type: " + type);
            System.out.println("TargetId: " + targetId);
            System.out.println("ActorId: " + actorId);

            Map<String, String> data = new HashMap<>();
            data.put("type", type != null ? type.toString() : "");
            data.put("title", title);
            data.put("body", body);
            if (targetId != null) {
                data.put("targetId", targetId.toString());
            }
            if (actorId != null) {
                data.put("actorId", actorId.toString());
            }

            System.out.println("Data payload: " + data);

            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .putAllData(data)
                    .build();

            System.out.println("Firebase message built successfully");
            System.out.println("Sending to Firebase...");

            String response = firebaseMessaging.send(message);
            System.out.println("SUCCESS! Firebase response: " + response);
            logger.info("Successfully sent message: {}", response);

        } catch (com.google.firebase.messaging.FirebaseMessagingException e) {
            System.err.println("FIREBASE MESSAGING ERROR!");
            System.err.println("Error Code: " + e.getMessagingErrorCode());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("FCM Token used: " + fcmToken.substring(0, 20) + "...");

            // Thay switch bằng if-else để tránh lỗi enum
            String errorCode = e.getMessagingErrorCode() != null ? e.getMessagingErrorCode().toString() : "UNKNOWN";
            System.err.println("Error type: " + errorCode);

            if (errorCode.contains("UNREGISTERED")) {
                System.err.println("Token is UNREGISTERED - app uninstalled or token expired");
            } else if (errorCode.contains("INVALID_ARGUMENT")) {
                System.err.println("INVALID_ARGUMENT - token format is wrong");
            } else if (errorCode.contains("NOT_FOUND")) {
                System.err.println("NOT_FOUND - project or token not found");
            } else {
                System.err.println("Other FCM error: " + errorCode);
            }

            e.printStackTrace();
            logger.error("Firebase messaging error: {}", e.getMessage());

        } catch (Exception e) {
            System.err.println("GENERAL ERROR in sendPushNotification: " + e.getMessage());
            e.printStackTrace();
            logger.error("Error sending push notification: {}", e.getMessage());
        }
    }

    public void updateUserFCMToken(Long userId, String fcmToken) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setFcmToken(fcmToken);
            userRepository.save(user);

            logger.info("Updated FCM token for user: {}", userId);

        } catch (Exception e) {
            logger.error("Error updating FCM token for user {}: {}", userId, e.getMessage());
        }
    }

    public void removeUserFCMToken(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setFcmToken(null);
            userRepository.save(user);

            logger.info("Removed FCM token for user: {}", userId);

        } catch (Exception e) {
            logger.error("Error removing FCM token for user {}: {}", userId, e.getMessage());
        }
    }
}