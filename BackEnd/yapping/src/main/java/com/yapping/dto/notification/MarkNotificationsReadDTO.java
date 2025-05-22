package com.yapping.dto.notification;

import java.util.List;
import lombok.Data;

@Data
public class MarkNotificationsReadDTO {
    private List<Long> notificationIds;
    private Boolean allNotifications;
}
