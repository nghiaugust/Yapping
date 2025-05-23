package com.yapping.service;

import com.yapping.dto.like.CreateLikeDTO;
import com.yapping.dto.like.LikeDTO;
import com.yapping.entity.Like.TargetType;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeService {
    
    // Tạo một lượt thích mới
    LikeDTO createLike(CreateLikeDTO createLikeDTO);
    
    // Xóa một lượt thích
    void deleteLike(Long likeId);
    
    // Xóa lượt thích của người dùng đối với một đối tượng
    void deleteLikeByUserAndTarget(Long userId, TargetType targetType, Long targetId);
    
    // Lấy tất cả lượt thích của một người dùng
    List<LikeDTO> getLikesByUserId(Long userId);
    
    // Lấy tất cả lượt thích của một người dùng có phân trang
    Page<LikeDTO> getLikesByUserId(Long userId, Pageable pageable);
    
    // Lấy tất cả lượt thích cho một bài đăng
    List<LikeDTO> getLikesByPost(Long postId);
    
    // Lấy tất cả lượt thích cho một bài đăng có phân trang
    Page<LikeDTO> getLikesByPost(Long postId, Pageable pageable);
    
    // Lấy tất cả lượt thích cho một bình luận
    List<LikeDTO> getLikesByComment(Long commentId);
    
    // Lấy tất cả lượt thích cho một bình luận có phân trang
    Page<LikeDTO> getLikesByComment(Long commentId, Pageable pageable);
    
    // Kiểm tra xem người dùng đã thích một đối tượng chưa
    boolean hasUserLiked(Long userId, TargetType targetType, Long targetId);
    
    // Đếm số lượt thích của một đối tượng
    long countLikes(TargetType targetType, Long targetId);
    
    // Lấy tất cả lượt thích trong một khoảng thời gian
    List<LikeDTO> getLikesByDateRange(Instant startDate, Instant endDate);
    
    // Lấy tất cả lượt thích trong một khoảng thời gian có phân trang
    Page<LikeDTO> getLikesByDateRange(Instant startDate, Instant endDate, Pageable pageable);
    
    // Lấy lượt thích của người dùng trong một khoảng thời gian
    List<LikeDTO> getLikesByUserIdAndDateRange(Long userId, Instant startDate, Instant endDate);
    
    // Lấy lượt thích của người dùng trong một khoảng thời gian có phân trang
    Page<LikeDTO> getLikesByUserIdAndDateRange(Long userId, Instant startDate, Instant endDate, Pageable pageable);
    
    // Tìm kiếm lượt thích theo nhiều điều kiện
    Page<LikeDTO> searchLikes(
            Long userId,
            TargetType targetType,
            Long targetId,
            Instant startDate,
            Instant endDate,
            Pageable pageable);
    
    // Tạo lượt thích và tăng số lượt thích cho bài đăng
    LikeDTO likePost(Long userId, Long postId);
    
    // Tạo lượt thích và tăng số lượt thích cho bình luận
    LikeDTO likeComment(Long userId, Long commentId);
    
    // Kiểm tra và tạo lượt thích nếu chưa tồn tại
    LikeDTO checkAndCreateLike(Long userId, TargetType targetType, Long targetId);
}
