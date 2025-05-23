package com.yapping.repository;

import com.yapping.entity.Like;
import com.yapping.entity.Like.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    // Tìm tất cả lượt thích của một người dùng
    List<Like> findByUserId(Long userId);
    
    // Tìm tất cả lượt thích của một người dùng có phân trang
    Page<Like> findByUserId(Long userId, Pageable pageable);
    
    // Tìm tất cả lượt thích cho một đối tượng (bài đăng hoặc bình luận)
    List<Like> findByTargetTypeAndTargetId(TargetType targetType, Long targetId);
    
    // Tìm tất cả lượt thích cho một đối tượng có phân trang
    Page<Like> findByTargetTypeAndTargetId(TargetType targetType, Long targetId, Pageable pageable);
    
    // Tìm một lượt thích cụ thể
    Optional<Like> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
    
    // Kiểm tra xem người dùng đã thích một đối tượng chưa
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
    
    // Đếm số lượt thích của một đối tượng
    long countByTargetTypeAndTargetId(TargetType targetType, Long targetId);
    
    // Tìm tất cả lượt thích trong một khoảng thời gian
    List<Like> findByCreatedAtBetween(Instant startDate, Instant endDate);
    
    // Tìm tất cả lượt thích trong một khoảng thời gian có phân trang
    Page<Like> findByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);
    
    // Tìm lượt thích của người dùng trong một khoảng thời gian
    List<Like> findByUserIdAndCreatedAtBetween(Long userId, Instant startDate, Instant endDate);
    
    // Tìm lượt thích của người dùng trong một khoảng thời gian có phân trang
    Page<Like> findByUserIdAndCreatedAtBetween(Long userId, Instant startDate, Instant endDate, Pageable pageable);
    
    // Tìm kiếm lượt thích theo nhiều điều kiện
    @Query("SELECT l FROM Like l WHERE " +
           "(:userId IS NULL OR l.user.id = :userId) AND " +
           "(:targetType IS NULL OR l.targetType = :targetType) AND " +
           "(:targetId IS NULL OR l.targetId = :targetId) AND " +
           "(:startDate IS NULL OR l.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR l.createdAt <= :endDate)")
    Page<Like> searchLikes(
            @Param("userId") Long userId,
            @Param("targetType") TargetType targetType,
            @Param("targetId") Long targetId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);
}
