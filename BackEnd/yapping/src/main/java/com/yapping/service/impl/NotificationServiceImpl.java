package com.yapping.service.impl;

import com.yapping.dto.notification.CreateNotificationDTO;
import com.yapping.dto.notification.MarkNotificationsReadDTO;
import com.yapping.dto.notification.NotificationDTO;
import com.yapping.entity.Comment;
import com.yapping.entity.Mention;
import com.yapping.entity.Notification;
import com.yapping.entity.Notification.Type;
import com.yapping.entity.Notification.TargetType;
import com.yapping.entity.Post;
import com.yapping.entity.User;
import com.yapping.repository.CommentRepository;
import com.yapping.repository.NotificationRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.FCMService;
import com.yapping.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FCMService fcmService;
    
    @Autowired
    private CommentRepository commentRepository;
    
    // Chuyển đổi từ entity sang DTO
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        
        if (notification.getUser() != null) {
            dto.setUserId(notification.getUser().getId());
            dto.setUsername(notification.getUser().getUsername());
            dto.setUserFullName(notification.getUser().getFullName());
            dto.setUserProfilePicture(notification.getUser().getProfilePicture());
        }
        
        if (notification.getActor() != null) {
            dto.setActorId(notification.getActor().getId());
            dto.setActorUsername(notification.getActor().getUsername());
            dto.setActorFullName(notification.getActor().getFullName());
            dto.setActorProfilePicture(notification.getActor().getProfilePicture());
        }
        
        dto.setType(notification.getType());
        dto.setTargetType(notification.getTargetType());
        dto.setTargetId(notification.getTargetId());
        
        if (notification.getTargetOwner() != null) {
            dto.setTargetOwnerId(notification.getTargetOwner().getId());
            dto.setTargetOwnerUsername(notification.getTargetOwner().getUsername());
            dto.setTargetOwnerFullName(notification.getTargetOwner().getFullName());
        }
        
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        
        // Tạo message dựa vào loại thông báo
        String message = buildMessage(notification);
        dto.setMessage(message);
        
        // Tạo URL chuyển hướng dựa vào loại thông báo và target
        String redirectUrl = buildRedirectUrl(notification);
        dto.setRedirectUrl(redirectUrl);
        
        return dto;
    }
    
    // Xây dựng thông điệp dựa trên loại thông báo
    private String buildMessage(Notification notification) {
        String message = "";
        String actorName = notification.getActor() != null ? notification.getActor().getFullName() : "Ai đó";
        
        switch (notification.getType()) {
            case LIKE_POST:
                message = actorName + " đã thích bài viết của bạn";
                break;
            case LIKE_COMMENT:
                message = actorName + " đã thích bình luận của bạn";
                break;
            case COMMENT:
                message = actorName + " đã bình luận về bài viết của bạn";
                break;
            case REPLY_POST:
                message = actorName + " đã trả lời bài viết của bạn";
                break;
            case REPLY_COMMENT:
                message = actorName + " đã trả lời bình luận của bạn";
                break;
            case FOLLOW:
                message = actorName + " đã bắt đầu theo dõi bạn";
                break;
            case MENTION_POST:
                message = actorName + " đã đề cập đến bạn trong một bài viết";
                break;            case MENTION_COMMENT:
                message = actorName + " đã đề cập đến bạn trong một bình luận";
                break;
            case REPOST:
                message = actorName + " đã chia sẻ bài viết của bạn";
                break;
            case NEW_POST:
                message = actorName + " đã đăng một bài viết mới";
                break;
            case SYSTEM:
                message = "Thông báo hệ thống";
                break;
            default:
                message = "Bạn có một thông báo mới";
                break;
        }
        
        return message;
    }
    
    // Xây dựng URL chuyển hướng
    private String buildRedirectUrl(Notification notification) {
        String baseUrl = "/";
        
        switch (notification.getTargetType()) {
            case POST:
                return baseUrl + "post/" + notification.getTargetId();
            case COMMENT:
                return baseUrl + "comment/" + notification.getTargetId();
            case USER:
                return baseUrl + "user/" + notification.getTargetId();
            default:
                return baseUrl;
        }
    }

//    public void createAndSendNotification(Long userId) {
//        try {
//            // Tạo thông báo trong database
//            CreateNotificationDTO createDTO = new CreateNotificationDTO();
//            createDTO.setUserId(userId);
//            createDTO.setActorId(actorId);
//            createDTO.setType(type);
//            createDTO.setTargetType(targetType);
//            createDTO.setTargetId(targetId);
//            createDTO.setTargetOwnerId(targetOwnerId);
//            NotificationDTO notification = createNotification(createDTO);
//
//            // Tạo message cho push notification
//            String title = generateNotificationTitle(type);
//            String body = generateNotificationBody(notification);
//
//            // Gửi push notification
//            fcmService.sendNotificationToUser(userId, title, body, type, targetId, actorId);
//
//        } catch (Exception e) {
//            // Log lỗi nếu có
//            System.err.println("Error creating or sending notification: " + e.getMessage());
//        }
//    }
    private String generateNotificationTitle(Notification.Type type) {
        switch (type) {
            case LIKE_POST:
                return "Bài viết được thích";
            case LIKE_COMMENT:
                return "Bình luận được thích";
            case COMMENT:
                return "Bình luận mới";
            case REPLY_POST:
                return "Phản hồi bài viết";
            case REPLY_COMMENT:
                return "Phản hồi bình luận";
            case FOLLOW:
                return "Người theo dõi mới";
            case MENTION_POST:
                return "Được nhắc đến trong bài viết";
            case MENTION_COMMENT:
                return "Được nhắc đến trong bình luận";
            case REPOST:
                return "Bài viết được chia sẻ";
            default:
                return "Thông báo mới";
        }
    }
    private String generateNotificationBody(NotificationDTO notification) {
        String actorName = notification.getActorFullName() != null && !notification.getActorFullName().isEmpty()
                ? notification.getActorFullName()
                : notification.getActorUsername();

        switch (notification.getType()) {
            case LIKE_POST:
                return actorName + " đã thích bài viết của bạn";
            case LIKE_COMMENT:
                return actorName + " đã thích bình luận của bạn";
            case COMMENT:
                return actorName + " đã bình luận về bài viết của bạn";
            case REPLY_POST:
                return actorName + " đã phản hồi bài viết của bạn";
            case REPLY_COMMENT:
                return actorName + " đã phản hồi bình luận của bạn";
            case FOLLOW:
                return actorName + " đã bắt đầu theo dõi bạn";
            case MENTION_POST:
                return actorName + " đã nhắc đến bạn trong một bài viết";
            case MENTION_COMMENT:
                return actorName + " đã nhắc đến bạn trong một bình luận";
            case REPOST:
                return actorName + " đã chia sẻ bài viết của bạn";
            default:
                return "Bạn có thông báo mới từ " + actorName;
        }
    }
    @Override
    @Transactional
    public NotificationDTO createNotification(CreateNotificationDTO createNotificationDTO) {
        User user = userRepository.findById(createNotificationDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại với ID: " + createNotificationDTO.getUserId()));
        
        User actor = null;
        if (createNotificationDTO.getActorId() != null) {
            actor = userRepository.findById(createNotificationDTO.getActorId())
                    .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại với ID: " + createNotificationDTO.getActorId()));
        }
        
        User targetOwner = null;
        if (createNotificationDTO.getTargetOwnerId() != null) {
            targetOwner = userRepository.findById(createNotificationDTO.getTargetOwnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại với ID: " + createNotificationDTO.getTargetOwnerId()));
        }
        
        // Nếu người nhận thông báo và người tạo thông báo là cùng một người, không tạo thông báo
        if (actor != null && user.getId().equals(actor.getId())) {
            throw new IllegalArgumentException("Không thể tạo thông báo cho chính người tạo hành động");
        }
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setActor(actor);
        notification.setType(createNotificationDTO.getType());
        notification.setTargetType(createNotificationDTO.getTargetType());
        notification.setTargetId(createNotificationDTO.getTargetId());
        notification.setTargetOwner(targetOwner);
        notification.setIsRead(false);
        notification.setCreatedAt(Instant.now());
        
        Notification savedNotification = notificationRepository.save(notification);

        NotificationDTO notificationDTO = convertToDTO(savedNotification);
        // Tạo message cho push notification
        String title = generateNotificationTitle(notificationDTO.getType());
        String body = generateNotificationBody(notificationDTO);

        // Gửi push notification
        fcmService.sendNotificationToUser(notificationDTO.getUserId(), title, body, notificationDTO.getType(), notificationDTO.getTargetId(), notificationDTO.getActorId());
        
        return convertToDTO(savedNotification);
    }
    
    @Override
    @Transactional
    public NotificationDTO createMentionNotification(Mention mention, String message) {
        // Đảm bảo người được đề cập và người đề cập không phải là cùng một người
        if (mention.getMentionedUser().getId().equals(mention.getMentioningUser().getId())) {
            return null;
        }
        
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(mention.getMentionedUser().getId());
        dto.setActorId(mention.getMentioningUser().getId());
        
        if (mention.getPost() != null) {
            dto.setType(Type.MENTION_POST);
            dto.setTargetType(TargetType.POST);
            dto.setTargetId(mention.getPost().getId());
            dto.setTargetOwnerId(mention.getPost().getUser().getId());
        } else if (mention.getComment() != null) {
            dto.setType(Type.MENTION_COMMENT);
            dto.setTargetType(TargetType.COMMENT);
            dto.setTargetId(mention.getComment().getId());
            dto.setTargetOwnerId(mention.getComment().getUser().getId());
        } else {
            throw new IllegalArgumentException("Đề cập phải liên kết với bài đăng hoặc bình luận");
        }
        
        dto.setMessage(message);
        
        return createNotification(dto);
    }
    
    @Override
    @Transactional
    public NotificationDTO createFollowNotification(Long followerId, Long followedId) {
        // Đảm bảo người theo dõi và người được theo dõi không phải là cùng một người
        if (followerId.equals(followedId)) {
            return null;
        }
        
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(followedId);
        dto.setActorId(followerId);
        dto.setType(Type.FOLLOW);
        dto.setTargetType(TargetType.USER);
        dto.setTargetId(followerId);
        
        return createNotification(dto);
    }
    
    @Override
    @Transactional
    public NotificationDTO createLikePostNotification(Long actorId, Long postId, Long postOwnerId) {
        // Đảm bảo người thích và chủ bài đăng không phải là cùng một người
        if (actorId.equals(postOwnerId)) {
            return null;
        }
        
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(postOwnerId);
        dto.setActorId(actorId);
        dto.setType(Type.LIKE_POST);
        dto.setTargetType(TargetType.POST);
        dto.setTargetId(postId);
        dto.setTargetOwnerId(postOwnerId);
        
        return createNotification(dto);
    }
    
    @Override
    @Transactional
    public NotificationDTO createLikeCommentNotification(Long actorId, Long commentId, Long commentOwnerId) {
        // Đảm bảo người thích và chủ bình luận không phải là cùng một người
        if (actorId.equals(commentOwnerId)) {
            return null;
        }
        
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(commentOwnerId);
        dto.setActorId(actorId);
        dto.setType(Type.LIKE_COMMENT);
        dto.setTargetType(TargetType.COMMENT);
        dto.setTargetId(commentId);
        dto.setTargetOwnerId(commentOwnerId);
        
        return createNotification(dto);
    }
    
    @Override
    @Transactional
    public NotificationDTO createCommentNotification(Long actorId, Long postId, Long postOwnerId, Long commentId) {
        // Đảm bảo người bình luận và chủ bài đăng không phải là cùng một người
        if (actorId.equals(postOwnerId)) {
            return null;
        }
        
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(postOwnerId);
        dto.setActorId(actorId);
        dto.setType(Type.COMMENT);
        dto.setTargetType(TargetType.POST);
        dto.setTargetId(postId);
        dto.setTargetOwnerId(postOwnerId);
        
        return createNotification(dto);
    }
    
    @Override
    @Transactional
    public NotificationDTO createReplyCommentNotification(Long actorId, Long parentCommentId, Long parentCommentOwnerId, Long commentId) {
        // Đảm bảo người trả lời và chủ bình luận gốc không phải là cùng một người
        if (actorId.equals(parentCommentOwnerId)) {
            return null;
        }
        
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(parentCommentOwnerId);
        dto.setActorId(actorId);
        dto.setType(Type.REPLY_COMMENT);
        dto.setTargetType(TargetType.COMMENT);
        dto.setTargetId(commentId); // ID của bình luận trả lời
        dto.setTargetOwnerId(parentCommentOwnerId);        
        return createNotification(dto);
    }
    
    @Override
    @Transactional
    public NotificationDTO createRepostNotification(Long actorId, Long postId, Long postOwnerId) {
        // Đảm bảo người repost và chủ bài đăng không phải là cùng một người
        if (actorId.equals(postOwnerId)) {
            return null;
        }
        
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(postOwnerId);
        dto.setActorId(actorId);
        dto.setType(Type.REPOST);
        dto.setTargetType(TargetType.POST);
        dto.setTargetId(postId);
        dto.setTargetOwnerId(postOwnerId);
        
        return createNotification(dto);
    }
    
    @Override
    @Transactional
    public NotificationDTO createPostNotification(Long receiverId, Long actorId, Long postId) {
        // Đảm bảo người nhận thông báo và người tạo bài đăng không phải là cùng một người
        if (receiverId.equals(actorId)) {
            return null;
        }
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Bài đăng không tồn tại với ID: " + postId));
        
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(receiverId);
        dto.setActorId(actorId);
        dto.setType(Type.NEW_POST);
        dto.setTargetType(TargetType.POST);
        dto.setTargetId(postId);
        dto.setTargetOwnerId(actorId);
        
        return createNotification(dto);
    }
    
    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Thông báo không tồn tại với ID: " + notificationId));
        
        notificationRepository.delete(notification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Thông báo không tồn tại với ID: " + notificationId));
        
        return convertToDTO(notification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsByUserId(Long userId, Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.findByUserId(userId, pageable);
        return notificationPage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUnreadNotificationsByUserId(Long userId, Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.findByUserIdAndIsReadFalse(userId, pageable);
        return notificationPage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public void markNotificationsAsRead(Long userId, MarkNotificationsReadDTO markNotificationsReadDTO) {
        // Nếu yêu cầu đánh dấu tất cả thông báo
        if (markNotificationsReadDTO.getAllNotifications() != null && markNotificationsReadDTO.getAllNotifications()) {
            markAllNotificationsAsRead(userId);
            return;
        }
        
        // Nếu có danh sách ID thông báo cụ thể
        if (markNotificationsReadDTO.getNotificationIds() != null && !markNotificationsReadDTO.getNotificationIds().isEmpty()) {
            notificationRepository.markAsReadByIds(markNotificationsReadDTO.getNotificationIds(), userId);
        }
    }
    
    @Override
    @Transactional
    public int markAllNotificationsAsRead(Long userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> searchNotifications(
            Long userId,
            Type type,
            TargetType targetType,
            Long targetId,
            Boolean isRead,
            Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.searchNotifications(
                userId, type, targetType, targetId, isRead, pageable);
        return notificationPage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public int cleanupOldNotifications(int daysToKeep) {
        Instant cutoffDate = Instant.now().minus(daysToKeep, ChronoUnit.DAYS);
        return notificationRepository.deleteOldNotifications(cutoffDate);
    }
}
