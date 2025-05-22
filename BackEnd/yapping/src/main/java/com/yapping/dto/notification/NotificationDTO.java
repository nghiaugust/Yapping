package com.yapping.dto.notification;

import com.yapping.entity.Notification.Type;
import com.yapping.entity.Notification.TargetType;
import java.time.Instant;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String username;
    private String userFullName;
    private String userProfilePicture;
    private Long actorId;
    private String actorUsername;
    private String actorFullName;
    private String actorProfilePicture;
    private Type type;
    private TargetType targetType;
    private Long targetId;
    private Long targetOwnerId;
    private String targetOwnerUsername;
    private String targetOwnerFullName;
    private Boolean isRead;
    private Instant createdAt;
    private String message;
    private String redirectUrl;
}
