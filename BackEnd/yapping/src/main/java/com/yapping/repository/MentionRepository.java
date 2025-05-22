package com.yapping.repository;

import com.yapping.entity.Mention;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentionRepository extends JpaRepository<Mention, Long> {
    
    // Tìm tất cả đề cập của một người dùng (người được đề cập)
    List<Mention> findByMentionedUserId(Long mentionedUserId);
    
    // Tìm tất cả đề cập của một người dùng (người được đề cập) có phân trang
    Page<Mention> findByMentionedUserId(Long mentionedUserId, Pageable pageable);
    
    // Tìm tất cả đề cập do một người dùng tạo ra (người đề cập)
    List<Mention> findByMentioningUserId(Long mentioningUserId);
    
    // Tìm tất cả đề cập do một người dùng tạo ra (người đề cập) có phân trang
    Page<Mention> findByMentioningUserId(Long mentioningUserId, Pageable pageable);
    
    // Tìm tất cả đề cập trong một bài đăng
    List<Mention> findByPostId(Long postId);
    
    // Tìm tất cả đề cập trong một bài đăng có phân trang
    Page<Mention> findByPostId(Long postId, Pageable pageable);
    
    // Tìm tất cả đề cập trong một bình luận
    List<Mention> findByCommentId(Long commentId);
    
    // Tìm tất cả đề cập trong một bình luận có phân trang
    Page<Mention> findByCommentId(Long commentId, Pageable pageable);
    
    // Tìm một đề cập cụ thể trong bài đăng
    Optional<Mention> findByMentionedUserIdAndPostId(Long mentionedUserId, Long postId);
    
    // Tìm một đề cập cụ thể trong bình luận
    Optional<Mention> findByMentionedUserIdAndCommentId(Long mentionedUserId, Long commentId);

    // Đếm số lượng đề cập của một người dùng
    long countByMentionedUserId(Long mentionedUserId);
    
    // Tìm các đề cập chưa đọc của một người dùng
    @Query("SELECT m FROM Mention m WHERE m.mentionedUser.id = :userId AND EXISTS (SELECT n FROM Notification n WHERE " +
           "((n.targetType = com.yapping.entity.Notification.TargetType.POST AND n.targetId = m.post.id) OR " +
           "(n.targetType = com.yapping.entity.Notification.TargetType.COMMENT AND n.targetId = m.comment.id)) AND " +
           "(n.type = com.yapping.entity.Notification.Type.MENTION_POST OR n.type = com.yapping.entity.Notification.Type.MENTION_COMMENT) AND " +
           "n.isRead = false AND n.user.id = :userId)")
    List<Mention> findUnreadMentionsByUserId(@Param("userId") Long userId);
    
    // Tìm các đề cập chưa đọc của một người dùng có phân trang
    @Query("SELECT m FROM Mention m WHERE m.mentionedUser.id = :userId AND EXISTS (SELECT n FROM Notification n WHERE " +
           "((n.targetType = com.yapping.entity.Notification.TargetType.POST AND n.targetId = m.post.id) OR " +
           "(n.targetType = com.yapping.entity.Notification.TargetType.COMMENT AND n.targetId = m.comment.id)) AND " +
           "(n.type = com.yapping.entity.Notification.Type.MENTION_POST OR n.type = com.yapping.entity.Notification.Type.MENTION_COMMENT) AND " +
           "n.isRead = false AND n.user.id = :userId)")
    Page<Mention> findUnreadMentionsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // Tìm kiếm đề cập theo nhiều điều kiện
    @Query("SELECT m FROM Mention m WHERE " +
           "(:mentionedUserId IS NULL OR m.mentionedUser.id = :mentionedUserId) AND " +
           "(:mentioningUserId IS NULL OR m.mentioningUser.id = :mentioningUserId) AND " +
           "(:postId IS NULL OR m.post.id = :postId) AND " +
           "(:commentId IS NULL OR m.comment.id = :commentId)")
    Page<Mention> searchMentions(
            @Param("mentionedUserId") Long mentionedUserId,
            @Param("mentioningUserId") Long mentioningUserId,
            @Param("postId") Long postId,
            @Param("commentId") Long commentId,
            Pageable pageable);
}
