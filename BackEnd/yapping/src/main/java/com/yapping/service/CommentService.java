package com.yapping.service;

import com.yapping.dto.comment.CommentDTO;
import com.yapping.dto.comment.CreateCommentDTO;
import com.yapping.dto.comment.UpdateCommentDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    
    // Tạo bình luận mới
    CommentDTO createComment(CreateCommentDTO createCommentDTO, Long userId);
    
    // Cập nhật bình luận
    CommentDTO updateComment(Long commentId, UpdateCommentDTO updateCommentDTO, Long userId);
    
    // Xóa bình luận
    void deleteComment(Long commentId, Long userId);
    
    // Lấy bình luận theo ID
    CommentDTO getCommentById(Long commentId);
    
    // Lấy tất cả bình luận của một bài đăng
    List<CommentDTO> getCommentsByPostId(Long postId);
    
    // Lấy tất cả bình luận của một bài đăng (có phân trang)
    Page<CommentDTO> getCommentsByPostId(Long postId, Pageable pageable);
    
    // Lấy tất cả bình luận gốc (không có bình luận cha) của một bài đăng
    List<CommentDTO> getRootCommentsByPostId(Long postId);
    
    // Lấy tất cả bình luận gốc (không có bình luận cha) của một bài đăng (có phân trang)
    Page<CommentDTO> getRootCommentsByPostId(Long postId, Pageable pageable);
    
    // Lấy tất cả bình luận con của một bình luận
    List<CommentDTO> getChildComments(Long parentCommentId);
    
    // Lấy tất cả bình luận con của một bình luận (có phân trang)
    Page<CommentDTO> getChildComments(Long parentCommentId, Pageable pageable);
    
    // Lấy tất cả bình luận của một người dùng
    List<CommentDTO> getCommentsByUserId(Long userId);
    
    // Lấy tất cả bình luận của một người dùng (có phân trang)
    Page<CommentDTO> getCommentsByUserId(Long userId, Pageable pageable);
      
    // Tăng lượt thích cho bình luận
    CommentDTO likeComment(Long commentId);
    
    // Tăng lượt thích cho bình luận (với thông tin người thích)
    CommentDTO likeComment(Long commentId, Long userId);
    
    // Đếm số bình luận của một bài đăng
    long countCommentsByPostId(Long postId);
}
