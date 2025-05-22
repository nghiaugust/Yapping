package com.yapping.repository;

import com.yapping.entity.Comment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Tìm tất cả comment thuộc về một bài đăng
    List<Comment> findByPostId(Long postId);
    
    // Tìm tất cả comment thuộc về một bài đăng (có phân trang)
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    
    // Tìm tất cả comment con của một comment
    List<Comment> findByParentCommentId(Long parentCommentId);
    
    // Tìm tất cả comment con của một comment (có phân trang)
    Page<Comment> findByParentCommentId(Long parentCommentId, Pageable pageable);
    
    // Tìm tất cả comment gốc (không có comment cha) của một bài đăng
    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);
    
    // Tìm tất cả comment gốc (không có comment cha) của một bài đăng (có phân trang)
    Page<Comment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);
    
    // Tìm tất cả comment của một người dùng
    List<Comment> findByUserId(Long userId);
    
    // Tìm tất cả comment của một người dùng (có phân trang)
    Page<Comment> findByUserId(Long userId, Pageable pageable);
    
    // Đếm số comment của một bài đăng
    long countByPostId(Long postId);
    
    // Đếm số comment con của một comment
    long countByParentCommentId(Long parentCommentId);
    
    // Tìm kiếm comment theo nội dung
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:keyword%")
    List<Comment> searchByContent(@Param("keyword") String keyword);
    
    // Tìm kiếm comment theo nội dung (có phân trang)
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:keyword%")
    Page<Comment> searchByContent(@Param("keyword") String keyword, Pageable pageable);
}
