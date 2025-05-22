package com.yapping.repository;

import com.yapping.entity.Notification;
import com.yapping.entity.Notification.Type;
import com.yapping.entity.Notification.TargetType;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Tìm tất cả thông báo của một người dùng
    List<Notification> findByUserId(Long userId);
    
    // Tìm tất cả thông báo của một người dùng (có phân trang)
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    // Tìm tất cả thông báo chưa đọc của một người dùng
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    // Tìm tất cả thông báo chưa đọc của một người dùng (có phân trang)
    Page<Notification> findByUserIdAndIsReadFalse(Long userId, Pageable pageable);
    
    // Tìm tất cả thông báo đã đọc của một người dùng
    List<Notification> findByUserIdAndIsReadTrue(Long userId);
    
    // Tìm tất cả thông báo đã đọc của một người dùng (có phân trang)
    Page<Notification> findByUserIdAndIsReadTrue(Long userId, Pageable pageable);
    
    // Tìm tất cả thông báo của một người dùng theo loại
    List<Notification> findByUserIdAndType(Long userId, Type type);
    
    // Tìm tất cả thông báo của một người dùng theo loại (có phân trang)
    Page<Notification> findByUserIdAndType(Long userId, Type type, Pageable pageable);
    
    // Tìm tất cả thông báo của một người dùng theo loại đối tượng
    List<Notification> findByUserIdAndTargetType(Long userId, TargetType targetType);
    
    // Tìm tất cả thông báo của một người dùng theo loại đối tượng (có phân trang)
    Page<Notification> findByUserIdAndTargetType(Long userId, TargetType targetType, Pageable pageable);
    
    // Tìm thông báo của một người dùng về một đối tượng cụ thể
    List<Notification> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
    
    // Đếm số thông báo chưa đọc của một người dùng
    long countByUserIdAndIsReadFalse(Long userId);
    
    // Đánh dấu tất cả thông báo của một người dùng là đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);
    
    // Đánh dấu danh sách thông báo là đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :notificationIds AND n.user.id = :userId")
    int markAsReadByIds(@Param("notificationIds") List<Long> notificationIds, @Param("userId") Long userId);
    
    // Xóa thông báo cũ hơn một thời điểm nhất định
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :date")
    int deleteOldNotifications(@Param("date") Instant date);
    
    // Tìm thông báo theo nhiều điều kiện
    @Query("SELECT n FROM Notification n WHERE " +
           "n.user.id = :userId AND " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:targetType IS NULL OR n.targetType = :targetType) AND " +
           "(:targetId IS NULL OR n.targetId = :targetId) AND " +
           "(:isRead IS NULL OR n.isRead = :isRead)")
    Page<Notification> searchNotifications(
            @Param("userId") Long userId,
            @Param("type") Type type,
            @Param("targetType") TargetType targetType,
            @Param("targetId") Long targetId,
            @Param("isRead") Boolean isRead,
            Pageable pageable);
}
