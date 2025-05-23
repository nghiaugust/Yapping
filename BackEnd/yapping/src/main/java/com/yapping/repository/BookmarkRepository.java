package com.yapping.repository;

import com.yapping.entity.Bookmark;
import com.yapping.entity.BookmarkId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    // Tìm kiếm bookmark theo userId
    Page<Bookmark> findByUserId(Long userId, Pageable pageable);
    
    // Tìm kiếm bookmark theo postId
    Page<Bookmark> findByPostId(Long postId, Pageable pageable);
    
    // Tìm kiếm bookmark theo userId và postId
    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);
    
    // Kiểm tra bookmark có tồn tại không
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // Đếm số lượng bookmark của một bài đăng
    Long countByPostId(Long postId);
    
    // Lấy danh sách bookmark mới nhất của một người dùng
    List<Bookmark> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Xóa bookmark theo userId và postId
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
