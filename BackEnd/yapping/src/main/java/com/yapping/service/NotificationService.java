package com.yapping.service;

import com.yapping.dto.notification.CreateNotificationDTO;
import com.yapping.dto.notification.MarkNotificationsReadDTO;
import com.yapping.dto.notification.NotificationDTO;
import com.yapping.entity.Mention;
import com.yapping.entity.Notification.Type;
import com.yapping.entity.Notification.TargetType;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    
    // Tạo thông báo mới
    NotificationDTO createNotification(CreateNotificationDTO createNotificationDTO);
    
    // Tạo thông báo từ mention
    NotificationDTO createMentionNotification(Mention mention, String message);
    
    // Tạo thông báo follow
    NotificationDTO createFollowNotification(Long followerId, Long followedId);
    
    // Tạo thông báo like post
    NotificationDTO createLikePostNotification(Long actorId, Long postId, Long postOwnerId);
    
    // Tạo thông báo like comment
    NotificationDTO createLikeCommentNotification(Long actorId, Long commentId, Long commentOwnerId);
    
    // Tạo thông báo comment
    NotificationDTO createCommentNotification(Long actorId, Long postId, Long postOwnerId, Long commentId);
    
    // Tạo thông báo reply comment
    NotificationDTO createReplyCommentNotification(Long actorId, Long parentCommentId, Long parentCommentOwnerId, Long commentId);
    
    // Tạo thông báo repost
    NotificationDTO createRepostNotification(Long actorId, Long postId, Long postOwnerId);
    
    // Tạo thông báo bài đăng mới cho người theo dõi
    NotificationDTO createPostNotification(Long receiverId, Long actorId, Long postId);
    
    // Xóa thông báo
    void deleteNotification(Long notificationId);
    
    // Lấy thông báo theo ID
    NotificationDTO getNotificationById(Long notificationId);
    
    // Lấy tất cả thông báo của một người dùng
    List<NotificationDTO> getNotificationsByUserId(Long userId);
    
    // Lấy tất cả thông báo của một người dùng (có phân trang)
    Page<NotificationDTO> getNotificationsByUserId(Long userId, Pageable pageable);
    
    // Lấy tất cả thông báo chưa đọc của một người dùng
    List<NotificationDTO> getUnreadNotificationsByUserId(Long userId);
    
    // Lấy tất cả thông báo chưa đọc của một người dùng (có phân trang)
    Page<NotificationDTO> getUnreadNotificationsByUserId(Long userId, Pageable pageable);
    
    // Đánh dấu thông báo là đã đọc
    void markNotificationsAsRead(Long userId, MarkNotificationsReadDTO markNotificationsReadDTO);
    
    // Đánh dấu tất cả thông báo của một người dùng là đã đọc
    int markAllNotificationsAsRead(Long userId);
    
    // Đếm số thông báo chưa đọc của một người dùng
    long countUnreadNotificationsByUserId(Long userId);
    
    // Tìm kiếm thông báo
    Page<NotificationDTO> searchNotifications(
            Long userId,
            Type type,
            TargetType targetType,
            Long targetId,
            Boolean isRead,
            Pageable pageable);
    
    // Xóa thông báo cũ
    int cleanupOldNotifications(int daysToKeep);
}
