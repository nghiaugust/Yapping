package com.yapping.service.impl;

import com.yapping.dto.like.CreateLikeDTO;
import com.yapping.dto.like.LikeDTO;
import com.yapping.dto.notification.CreateNotificationDTO;
import com.yapping.entity.*;
import com.yapping.entity.Like.TargetType;
import com.yapping.repository.CommentRepository;
import com.yapping.repository.LikeRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.LikeService;
import com.yapping.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationServiceImpl notificationServiceImpl;
    
    // Chuyển đổi từ entity sang DTO
    private LikeDTO convertToDTO(Like like) {
        LikeDTO likeDTO = new LikeDTO();
        BeanUtils.copyProperties(like, likeDTO);
        
        // Set thông tin người dùng
        likeDTO.setUsername(like.getUser().getUsername());
        likeDTO.setUserFullName(like.getUser().getFullName());
        likeDTO.setUserProfilePicture(like.getUser().getProfilePicture());
        
        return likeDTO;
    }
    
    @Override
    @Transactional
    public LikeDTO createLike(CreateLikeDTO createLikeDTO) {
        // Kiểm tra người dùng tồn tại
        User user = userRepository.findById(createLikeDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + createLikeDTO.getUserId()));
        
        // Kiểm tra xem đã tồn tại lượt thích này chưa
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetTypeAndTargetId(
                createLikeDTO.getUserId(),
                createLikeDTO.getTargetType(),
                createLikeDTO.getTargetId());
        
        if (existingLike.isPresent()) {
            return convertToDTO(existingLike.get());
        }
        
        // Tạo mới lượt thích
        Like like = new Like();
        like.setUser(user);
        like.setTargetType(createLikeDTO.getTargetType());
        like.setTargetId(createLikeDTO.getTargetId());
        like.setCreatedAt(Instant.now());
        
        Like savedLike = likeRepository.save(like);
        
        return convertToDTO(savedLike);
    }
    
    @Override
    @Transactional
    public void deleteLike(Long likeId) {
        // Kiểm tra lượt thích tồn tại
        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lượt thích với ID: " + likeId));
        
        likeRepository.delete(like);
    }
    
    @Override
    @Transactional
    public void deleteLikeByUserAndTarget(Long userId, TargetType targetType, Long targetId) {
        // Tìm lượt thích
        Optional<Like> like = likeRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        
        // Nếu tồn tại, xóa lượt thích
        if (like.isPresent()) {
            likeRepository.delete(like.get());
            
            // Nếu là bài đăng, giảm số lượt thích của bài đăng
            if (targetType == TargetType.POST) {
                Post post = postRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với ID: " + targetId));
                
                if (post.getLikeCount() != null && post.getLikeCount() > 0) {
                    post.setLikeCount(post.getLikeCount() - 1);
                    postRepository.save(post);
                }
            }
            
            // Nếu là bình luận, giảm số lượt thích của bình luận
            else if (targetType == TargetType.COMMENT) {
                Comment comment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận với ID: " + targetId));
                
                if (comment.getLikeCount() != null && comment.getLikeCount() > 0) {
                    comment.setLikeCount(comment.getLikeCount() - 1);
                    commentRepository.save(comment);
                }
            }
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LikeDTO> getLikesByUserId(Long userId) {
        List<Like> likes = likeRepository.findByUserId(userId);
        return likes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LikeDTO> getLikesByUserId(Long userId, Pageable pageable) {
        Page<Like> likePage = likeRepository.findByUserId(userId, pageable);
        return likePage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LikeDTO> getLikesByPost(Long postId) {
        List<Like> likes = likeRepository.findByTargetTypeAndTargetId(TargetType.POST, postId);
        return likes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LikeDTO> getLikesByPost(Long postId, Pageable pageable) {
        Page<Like> likePage = likeRepository.findByTargetTypeAndTargetId(TargetType.POST, postId, pageable);
        return likePage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LikeDTO> getLikesByComment(Long commentId) {
        List<Like> likes = likeRepository.findByTargetTypeAndTargetId(TargetType.COMMENT, commentId);
        return likes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LikeDTO> getLikesByComment(Long commentId, Pageable pageable) {
        Page<Like> likePage = likeRepository.findByTargetTypeAndTargetId(TargetType.COMMENT, commentId, pageable);
        return likePage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long userId, TargetType targetType, Long targetId) {
        return likeRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countLikes(TargetType targetType, Long targetId) {
        return likeRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LikeDTO> getLikesByDateRange(Instant startDate, Instant endDate) {
        List<Like> likes = likeRepository.findByCreatedAtBetween(startDate, endDate);
        return likes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LikeDTO> getLikesByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        Page<Like> likePage = likeRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return likePage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LikeDTO> getLikesByUserIdAndDateRange(Long userId, Instant startDate, Instant endDate) {
        List<Like> likes = likeRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
        return likes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LikeDTO> getLikesByUserIdAndDateRange(Long userId, Instant startDate, Instant endDate, Pageable pageable) {
        Page<Like> likePage = likeRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate, pageable);
        return likePage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<LikeDTO> searchLikes(
            Long userId,
            TargetType targetType,
            Long targetId,
            Instant startDate,
            Instant endDate,
            Pageable pageable) {
        
        Page<Like> likePage = likeRepository.searchLikes(userId, targetType, targetId, startDate, endDate, pageable);
        return likePage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public LikeDTO likePost(Long userId, Long postId) {
        // Kiểm tra bài đăng tồn tại
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với ID: " + postId));
        
        // Kiểm tra người dùng tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Kiểm tra xem đã tồn tại lượt thích này chưa
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetTypeAndTargetId(userId, TargetType.POST, postId);
        
        if (existingLike.isPresent()) {
            return convertToDTO(existingLike.get());
        }
        
        // Tạo mới lượt thích
        Like like = new Like();
        like.setUser(user);
        like.setTargetType(TargetType.POST);
        like.setTargetId(postId);
        like.setCreatedAt(Instant.now());
        
        Like savedLike = likeRepository.save(like);

        CreateNotificationDTO createNotificationDTO = new CreateNotificationDTO();
        createNotificationDTO.setUserId(post.getUser().getId());  // người nhận notification
        createNotificationDTO.setActorId(userId);                   // người thực hiện action
        createNotificationDTO.setType(Notification.Type.LIKE_POST);
        createNotificationDTO.setTargetType(Notification.TargetType.POST);
        createNotificationDTO.setTargetId(postId);
        createNotificationDTO.setTargetOwnerId(post.getUser().getId());
        createNotificationDTO.setMessage("Bạn có một lượt thích mới trên bài đăng của mình");
        notificationService.createNotification(createNotificationDTO);
        // Gửi notification nếu người dùng không phải là chủ bài đăng

        return convertToDTO(savedLike);
    }
    
    @Override
    @Transactional
    public LikeDTO likeComment(Long userId, Long commentId) {
        // Kiểm tra bình luận tồn tại
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận với ID: " + commentId));
        
        // Kiểm tra người dùng tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Kiểm tra xem đã tồn tại lượt thích này chưa
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetTypeAndTargetId(userId, TargetType.COMMENT, commentId);
        
        if (existingLike.isPresent()) {
            return convertToDTO(existingLike.get());
        }
        
        // Tạo mới lượt thích
        Like like = new Like();
        like.setUser(user);
        like.setTargetType(TargetType.COMMENT);
        like.setTargetId(commentId);
        like.setCreatedAt(Instant.now());
        
        Like savedLike = likeRepository.save(like);
        CreateNotificationDTO createNotificationDTO = new CreateNotificationDTO();

        createNotificationDTO.setUserId(comment.getUser().getId());  // người nhận notification
        createNotificationDTO.setActorId(userId);                   // người thực hiện action
        createNotificationDTO.setType(Notification.Type.LIKE_COMMENT);
        createNotificationDTO.setTargetType(Notification.TargetType.COMMENT);
        createNotificationDTO.setTargetId(commentId);
        createNotificationDTO.setTargetOwnerId(comment.getUser().getId());
        createNotificationDTO.setMessage("Bạn có một lượt thích mới trên bình luận của mình");
        notificationService.createNotification(createNotificationDTO);

//        if (!userId.equals(comment.getUser().getId())) {
//            notificationServiceImpl.createAndSendNotification(
//                    comment.getUser().getId(),  // người nhận notification
//                    userId,                     // người thực hiện action
//                    Notification.Type.LIKE_COMMENT,
//                    Notification.TargetType.COMMENT,
//                    commentId,
//                    comment.getUser().getId()
//            );
//        }
        return convertToDTO(savedLike);
    }
    
    @Override
    @Transactional
    public LikeDTO checkAndCreateLike(Long userId, TargetType targetType, Long targetId) {
        // Kiểm tra xem đã tồn tại lượt thích này chưa
        Optional<Like> existingLike = likeRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        
        if (existingLike.isPresent()) {
            return convertToDTO(existingLike.get());
        }
        
        // Nếu là bài đăng
        if (targetType == TargetType.POST) {
            return likePost(userId, targetId);
        }
        
        // Nếu là bình luận
        else if (targetType == TargetType.COMMENT) {
            return likeComment(userId, targetId);
        }
        
        throw new IllegalArgumentException("Loại đối tượng không hợp lệ");
    }
}
