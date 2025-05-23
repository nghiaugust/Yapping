package com.yapping.repository;

import com.yapping.entity.Repost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RepostRepository extends JpaRepository<Repost, Long> {
    
    // Kiểm tra xem người dùng đã đăng lại bài viết này chưa
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // Tìm repost của người dùng với bài đăng cụ thể
    Optional<Repost> findByUserIdAndPostId(Long userId, Long postId);
    
    // Lấy tất cả bài đăng lại của một người dùng
    List<Repost> findByUserId(Long userId);
    
    // Lấy tất cả bài đăng lại của một người dùng có phân trang
    Page<Repost> findByUserId(Long userId, Pageable pageable);
    
    // Lấy tất cả bài đăng lại của một bài viết
    List<Repost> findByPostId(Long postId);
    
    // Lấy tất cả bài đăng lại của một bài viết có phân trang
    Page<Repost> findByPostId(Long postId, Pageable pageable);
    
    // Đếm số lượt repost của một bài viết
    long countByPostId(Long postId);
    
    // Lấy tất cả bài đăng lại trong một khoảng thời gian
    List<Repost> findByCreatedAtBetween(Instant startDate, Instant endDate);
    
    // Lấy tất cả bài đăng lại trong một khoảng thời gian có phân trang
    Page<Repost> findByCreatedAtBetween(Instant startDate, Instant endDate, Pageable pageable);
    
    // Tìm kiếm bài đăng lại theo nhiều điều kiện
    @Query("SELECT r FROM Repost r WHERE " +
           "(:userId IS NULL OR r.user.id = :userId) AND " +
           "(:postId IS NULL OR r.post.id = :postId) AND " +
           "(:startDate IS NULL OR r.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR r.createdAt <= :endDate)")
    Page<Repost> searchReposts(
            @Param("userId") Long userId,
            @Param("postId") Long postId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);
}
