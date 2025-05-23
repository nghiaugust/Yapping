package com.yapping.service;

import com.yapping.dto.repost.CreateRepostDTO;
import com.yapping.dto.repost.RepostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface RepostService {
    
    // Tạo một repost mới
    RepostDTO createRepost(CreateRepostDTO createRepostDTO, Long userId);
    
    // Xóa một repost
    void deleteRepost(Long repostId, Long userId);
    
    // Xóa repost của người dùng đối với một bài đăng
    void deleteRepostByUserAndPost(Long userId, Long postId);
    
    // Lấy repost theo ID
    RepostDTO getRepostById(Long repostId);
    
    // Lấy tất cả repost của một người dùng
    List<RepostDTO> getRepostsByUserId(Long userId);
    
    // Lấy tất cả repost của một người dùng có phân trang
    Page<RepostDTO> getRepostsByUserId(Long userId, Pageable pageable);
    
    // Lấy tất cả repost của một bài viết
    List<RepostDTO> getRepostsByPost(Long postId);
    
    // Lấy tất cả repost của một bài viết có phân trang
    Page<RepostDTO> getRepostsByPost(Long postId, Pageable pageable);
    
    // Kiểm tra xem người dùng đã repost một bài viết chưa
    boolean hasUserReposted(Long userId, Long postId);
    
    // Đếm số lượt repost của một bài viết
    long countReposts(Long postId);
    
    // Lấy tất cả repost trong một khoảng thời gian
    List<RepostDTO> getRepostsByDateRange(Instant startDate, Instant endDate);
    
    // Lấy tất cả repost trong một khoảng thời gian có phân trang
    Page<RepostDTO> getRepostsByDateRange(Instant startDate, Instant endDate, Pageable pageable);
    
    // Tìm kiếm repost theo nhiều điều kiện
    Page<RepostDTO> searchReposts(
            Long userId,
            Long postId,
            Instant startDate,
            Instant endDate,
            Pageable pageable);
}
