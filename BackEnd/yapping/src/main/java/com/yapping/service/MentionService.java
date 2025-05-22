package com.yapping.service;

import com.yapping.dto.mention.CreateMentionDTO;
import com.yapping.dto.mention.MentionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MentionService {
    
    // Tạo đề cập mới
    MentionDTO createMention(CreateMentionDTO createMentionDTO);
    
    // Tạo nhiều đề cập từ text (phân tích @ trong nội dung)
    List<MentionDTO> createMentionsFromText(String text, Long mentioningUserId, Long postId, Long commentId);
    
    // Xóa đề cập
    void deleteMention(Long mentionId);
    
    // Lấy đề cập theo ID
    MentionDTO getMentionById(Long mentionId);
    
    // Lấy tất cả đề cập của một người dùng (người được đề cập)
    List<MentionDTO> getMentionsByMentionedUserId(Long mentionedUserId);
    
    // Lấy tất cả đề cập của một người dùng (người được đề cập) có phân trang
    Page<MentionDTO> getMentionsByMentionedUserId(Long mentionedUserId, Pageable pageable);
    
    // Lấy tất cả đề cập do một người dùng tạo ra (người đề cập)
    List<MentionDTO> getMentionsByMentioningUserId(Long mentioningUserId);
    
    // Lấy tất cả đề cập do một người dùng tạo ra (người đề cập) có phân trang
    Page<MentionDTO> getMentionsByMentioningUserId(Long mentioningUserId, Pageable pageable);
    
    // Lấy tất cả đề cập trong một bài đăng
    List<MentionDTO> getMentionsByPostId(Long postId);
    
    // Lấy tất cả đề cập trong một bài đăng có phân trang
    Page<MentionDTO> getMentionsByPostId(Long postId, Pageable pageable);
    
    // Lấy tất cả đề cập trong một bình luận
    List<MentionDTO> getMentionsByCommentId(Long commentId);
    
    // Lấy tất cả đề cập trong một bình luận có phân trang
    Page<MentionDTO> getMentionsByCommentId(Long commentId, Pageable pageable);
    
    // Lấy các đề cập chưa đọc của một người dùng
    List<MentionDTO> getUnreadMentionsByUserId(Long userId);
    
    // Lấy các đề cập chưa đọc của một người dùng có phân trang
    Page<MentionDTO> getUnreadMentionsByUserId(Long userId, Pageable pageable);
    
    // Đếm số lượng đề cập của một người dùng
    long countMentionsByMentionedUserId(Long mentionedUserId);
    
    // Tìm kiếm đề cập theo nhiều điều kiện
    Page<MentionDTO> searchMentions(
            Long mentionedUserId,
            Long mentioningUserId,
            Long postId,
            Long commentId,
            Pageable pageable);
    
    // Xóa các đề cập khi xóa bài đăng
    void deleteMentionsByPostId(Long postId);
    
    // Xóa các đề cập khi xóa bình luận
    void deleteMentionsByCommentId(Long commentId);
}
