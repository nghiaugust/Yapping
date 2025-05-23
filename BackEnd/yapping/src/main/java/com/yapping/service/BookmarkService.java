package com.yapping.service;

import com.yapping.dto.bookmark.BookmarkDTO;
import com.yapping.dto.bookmark.CreateBookmarkDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface BookmarkService {
    
    // Lấy danh sách bookmark theo user ID
    Page<BookmarkDTO> getBookmarksByUserId(Long userId, Pageable pageable);
    
    // Lấy danh sách bookmark theo post ID
    Page<BookmarkDTO> getBookmarksByPostId(Long postId, Pageable pageable);
    
    // Lấy bookmark cụ thể
    BookmarkDTO getBookmark(Long userId, Long postId);
    
    // Kiểm tra bookmark có tồn tại không
    boolean isBookmarked(Long userId, Long postId);
    
    // Tạo bookmark mới
    BookmarkDTO createBookmark(CreateBookmarkDTO createBookmarkDTO);
    
    // Xóa bookmark
    void deleteBookmark(Long userId, Long postId);
    
    // Lấy danh sách bookmark mới nhất của một người dùng
    List<BookmarkDTO> getLatestBookmarks(Long userId);
    
    // Đếm số lượng bookmark của một bài đăng
    Long countBookmarksByPostId(Long postId);
    
    // Lấy tất cả bookmark trong một khoảng thời gian
    List<BookmarkDTO> getBookmarksByDateRange(Instant startDate, Instant endDate);
    
    // Lấy tất cả bookmark trong một khoảng thời gian có phân trang
    Page<BookmarkDTO> getBookmarksByDateRange(Instant startDate, Instant endDate, Pageable pageable);
}
