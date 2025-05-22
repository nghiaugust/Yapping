package com.yapping.service.impl;

import com.yapping.dto.comment.CommentDTO;
import com.yapping.dto.comment.CreateCommentDTO;
import com.yapping.dto.comment.UpdateCommentDTO;
import com.yapping.entity.Comment;
import com.yapping.entity.Post;
import com.yapping.entity.User;
import com.yapping.repository.CommentRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.CommentService;
import com.yapping.service.MentionService;
import com.yapping.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private MentionService mentionService;

    @Override
    @Transactional
    public CommentDTO createComment(CreateCommentDTO createCommentDTO, Long userId) {
        // Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Kiểm tra post tồn tại
        Post post = postRepository.findById(createCommentDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với ID: " + createCommentDTO.getPostId()));
        
        // Kiểm tra parent comment nếu có
        Comment parentComment = null;
        if (createCommentDTO.getParentCommentId() != null) {
            parentComment = commentRepository.findById(createCommentDTO.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận cha với ID: " + createCommentDTO.getParentCommentId()));
            
            // Kiểm tra parent comment thuộc cùng post
            if (!parentComment.getPost().getId().equals(post.getId())) {
                throw new IllegalArgumentException("Bình luận cha không thuộc cùng bài đăng");
            }
        }
        
        // Tạo comment mới
        Comment comment = new Comment();
        comment.setContent(createCommentDTO.getContent());
        comment.setUser(user);
        comment.setPost(post);
        comment.setParentComment(parentComment);
        comment.setLikeCount(0);
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        
        // Lưu comment
        Comment savedComment = commentRepository.save(comment);
        
        // Tăng comment_count của post
        post.setCommentCount(post.getCommentCount() == null ? 1 : post.getCommentCount() + 1);
        postRepository.save(post);
        
        // Tạo thông báo
        // 1. Nếu đây là bình luận gốc, thông báo cho chủ bài đăng
        if (parentComment == null) {
            // Chỉ tạo thông báo nếu người bình luận không phải là chủ bài đăng
            if (!user.getId().equals(post.getUser().getId())) {
                notificationService.createCommentNotification(
                        userId, 
                        post.getId(), 
                        post.getUser().getId(),
                        savedComment.getId()
                );
            }
        } 
        // 2. Nếu đây là phản hồi cho một bình luận khác, thông báo cho chủ bình luận gốc
        else {
            // Chỉ tạo thông báo nếu người trả lời không phải là chủ bình luận gốc
            if (!user.getId().equals(parentComment.getUser().getId())) {
                notificationService.createReplyCommentNotification(
                        userId,
                        parentComment.getId(),
                        parentComment.getUser().getId(),
                        savedComment.getId()
                );
            }
        }
        
        // Xử lý mentions trong bình luận
        mentionService.createMentionsFromText(
                createCommentDTO.getContent(), 
                userId, 
                null, 
                savedComment.getId()
        );
        
        return convertToDTO(savedComment);
    }

    @Override
    @Transactional
    public CommentDTO updateComment(Long commentId, UpdateCommentDTO updateCommentDTO, Long userId) {
        // Kiểm tra comment tồn tại
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận với ID: " + commentId));
        
        // Kiểm tra quyền cập nhật (chỉ người tạo comment mới có quyền cập nhật)
        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("Người dùng không có quyền cập nhật bình luận này");
        }
        
        // Cập nhật nội dung
        comment.setContent(updateCommentDTO.getContent());
        comment.setUpdatedAt(Instant.now());
        
        // Lưu comment
        Comment updatedComment = commentRepository.save(comment);
        
        return convertToDTO(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // Kiểm tra comment tồn tại
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận với ID: " + commentId));
        
        // Kiểm tra quyền xóa (người tạo comment hoặc người tạo bài đăng)
        boolean isCommentOwner = comment.getUser().getId().equals(userId);
        boolean isPostOwner = comment.getPost().getUser().getId().equals(userId);
        
        if (!isCommentOwner && !isPostOwner) {
            throw new SecurityException("Người dùng không có quyền xóa bình luận này");
        }
        
        // Lấy post để cập nhật comment_count
        Post post = comment.getPost();
        
        // Xóa comment (các comment con sẽ bị xóa tự động do @OnDelete(action = OnDeleteAction.CASCADE))
        commentRepository.delete(comment);
        
        // Giảm comment_count của post
        long childCommentCount = commentRepository.countByParentCommentId(commentId);
        int totalComments = 1 + (int) childCommentCount; // comment chính + các comment con
        post.setCommentCount(post.getCommentCount() == null ? 0 : Math.max(0, post.getCommentCount() - totalComments));
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận với ID: " + commentId));
        return convertToDTO(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByPostId(Long postId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByPostId(postId, pageable);
        return commentPage.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getRootCommentsByPostId(Long postId) {
        List<Comment> rootComments = commentRepository.findByPostIdAndParentCommentIsNull(postId);
        return rootComments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getRootCommentsByPostId(Long postId, Pageable pageable) {
        Page<Comment> rootCommentPage = commentRepository.findByPostIdAndParentCommentIsNull(postId, pageable);
        return rootCommentPage.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getChildComments(Long parentCommentId) {
        List<Comment> childComments = commentRepository.findByParentCommentId(parentCommentId);
        return childComments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getChildComments(Long parentCommentId, Pageable pageable) {
        Page<Comment> childCommentPage = commentRepository.findByParentCommentId(parentCommentId, pageable);
        return childCommentPage.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByUserId(Long userId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByUserId(userId, pageable);
        return commentPage.map(this::convertToDTO);
    }    
    
    @Override
    @Transactional
    public CommentDTO likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận với ID: " + commentId));
        
        comment.setLikeCount(comment.getLikeCount() == null ? 1 : comment.getLikeCount() + 1);
        Comment updatedComment = commentRepository.save(comment);
        
        return convertToDTO(updatedComment);
    }
    
    @Override
    @Transactional
    public CommentDTO likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận với ID: " + commentId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        comment.setLikeCount(comment.getLikeCount() == null ? 1 : comment.getLikeCount() + 1);
        Comment updatedComment = commentRepository.save(comment);
        
        // Tạo thông báo cho chủ bình luận
        if (!user.getId().equals(comment.getUser().getId())) {
            notificationService.createLikeCommentNotification(
                    userId,
                    commentId,
                    comment.getUser().getId()
            );
        }
        
        return convertToDTO(updatedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCommentsByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
    
    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        BeanUtils.copyProperties(comment, commentDTO);
        
        // Set các trường quan hệ
        commentDTO.setPostId(comment.getPost().getId());
        commentDTO.setUserId(comment.getUser().getId());
        commentDTO.setUsername(comment.getUser().getUsername());
        commentDTO.setUserFullName(comment.getUser().getFullName());
        commentDTO.setUserProfilePicture(comment.getUser().getProfilePicture());
        
        if (comment.getParentComment() != null) {
            commentDTO.setParentCommentId(comment.getParentComment().getId());
        }
        
        return commentDTO;
    }
}
