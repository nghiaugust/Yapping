package com.yapping.dto.notification;

import com.yapping.entity.Notification.Type;
import com.yapping.entity.Notification.TargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateNotificationDTO {
    @NotNull(message = "ID người dùng nhận thông báo không được để trống")
    private Long userId;
    
    private Long actorId;
    
    @NotNull(message = "Loại thông báo không được để trống")
    private Type type;
    
    @NotNull(message = "Loại đối tượng không được để trống")
    private TargetType targetType;
    
    private Long targetId;
    
    private Long targetOwnerId;
    
    private String message;
}
