package com.yapping.service.impl;

import com.yapping.dto.mention.CreateMentionDTO;
import com.yapping.dto.mention.MentionDTO;
import com.yapping.entity.Comment;
import com.yapping.entity.Mention;
import com.yapping.entity.Post;
import com.yapping.entity.User;
import com.yapping.repository.CommentRepository;
import com.yapping.repository.MentionRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.MentionService;
import com.yapping.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MentionServiceImpl implements MentionService {

    @Autowired
    private MentionRepository mentionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    // Chuyển đổi từ entity sang DTO
    private MentionDTO convertToDTO(Mention mention) {
        MentionDTO dto = new MentionDTO();
        dto.setId(mention.getId());
        
        if (mention.getMentionedUser() != null) {
            dto.setMentionedUserId(mention.getMentionedUser().getId());
            dto.setMentionedUsername(mention.getMentionedUser().getUsername());
            dto.setMentionedUserFullName(mention.getMentionedUser().getFullName());
            dto.setMentionedUserProfilePicture(mention.getMentionedUser().getProfilePicture());
        }
        
        if (mention.getMentioningUser() != null) {
            dto.setMentioningUserId(mention.getMentioningUser().getId());
            dto.setMentioningUsername(mention.getMentioningUser().getUsername());
            dto.setMentioningUserFullName(mention.getMentioningUser().getFullName());
            dto.setMentioningUserProfilePicture(mention.getMentioningUser().getProfilePicture());
        }
        
        if (mention.getPost() != null) {
            dto.setPostId(mention.getPost().getId());
            dto.setPostContent(mention.getPost().getContent());
        }
        
        if (mention.getComment() != null) {
            dto.setCommentId(mention.getComment().getId());
            dto.setCommentContent(mention.getComment().getContent());
        }
        
        dto.setCreatedAt(mention.getCreatedAt());
        
        return dto;
    }
    
    @Override
    @Transactional
    public MentionDTO createMention(CreateMentionDTO createMentionDTO) {
        User mentionedUser = userRepository.findById(createMentionDTO.getMentionedUserId())
                .orElseThrow(() -> new EntityNotFoundException("Người dùng được đề cập không tồn tại"));
        
        User mentioningUser = userRepository.findById(createMentionDTO.getMentioningUserId())
                .orElseThrow(() -> new EntityNotFoundException("Người dùng đề cập không tồn tại"));
        
        Post post = null;
        if (createMentionDTO.getPostId() != null) {
            post = postRepository.findById(createMentionDTO.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Bài đăng không tồn tại"));
        }
        
        Comment comment = null;
        if (createMentionDTO.getCommentId() != null) {
            comment = commentRepository.findById(createMentionDTO.getCommentId())
                    .orElseThrow(() -> new EntityNotFoundException("Bình luận không tồn tại"));
        }
        
        // Kiểm tra xem bài đăng hoặc bình luận có được chỉ định không
        if (post == null && comment == null) {
            throw new IllegalArgumentException("Phải chỉ định ít nhất một bài đăng hoặc bình luận");
        }
        
        // Kiểm tra xem mention đã tồn tại chưa
        if (post != null) {
            Optional<Mention> existingMention = mentionRepository.findByMentionedUserIdAndPostId(
                    createMentionDTO.getMentionedUserId(), createMentionDTO.getPostId());
            if (existingMention.isPresent()) {
                return convertToDTO(existingMention.get());
            }
        }
        
        if (comment != null) {
            Optional<Mention> existingMention = mentionRepository.findByMentionedUserIdAndCommentId(
                    createMentionDTO.getMentionedUserId(), createMentionDTO.getCommentId());
            if (existingMention.isPresent()) {
                return convertToDTO(existingMention.get());
            }
        }
        
        Mention mention = new Mention();
        mention.setMentionedUser(mentionedUser);
        mention.setMentioningUser(mentioningUser);
        mention.setPost(post);
        mention.setComment(comment);
        mention.setCreatedAt(Instant.now());
        
        Mention savedMention = mentionRepository.save(mention);
        
        // Tạo thông báo cho người được đề cập
        if (!mentionedUser.getId().equals(mentioningUser.getId())) {
            String message = mentioningUser.getUsername() + " đã đề cập đến bạn";
            if (post != null) {
                message += " trong một bài đăng";
            } else if (comment != null) {
                message += " trong một bình luận";
            }
            
            // Giả định rằng NotificationService có một phương thức tạo thông báo từ đề cập
            notificationService.createMentionNotification(savedMention, message);
        }
        
        return convertToDTO(savedMention);
    }
    
    @Override
    @Transactional
    public List<MentionDTO> createMentionsFromText(String text, Long mentioningUserId, Long postId, Long commentId) {
        List<MentionDTO> results = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return results;
        }
        
        // Tìm tất cả các username được đề cập trong văn bản (bắt đầu bằng @)
        Pattern pattern = Pattern.compile("@([\\w]+)");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            String username = matcher.group(1);
            Optional<User> mentionedUser = userRepository.findByUsername(username);
            
            if (mentionedUser.isPresent()) {
                CreateMentionDTO createMentionDTO = new CreateMentionDTO();
                createMentionDTO.setMentionedUserId(mentionedUser.get().getId());
                createMentionDTO.setMentioningUserId(mentioningUserId);
                createMentionDTO.setPostId(postId);
                createMentionDTO.setCommentId(commentId);
                
                try {
                    MentionDTO mentionDTO = createMention(createMentionDTO);
                    results.add(mentionDTO);
                } catch (Exception e) {
                    // Bỏ qua nếu có lỗi khi tạo đề cập
                }
            }
        }
        
        return results;
    }
    
    @Override
    @Transactional
    public void deleteMention(Long mentionId) {
        Mention mention = mentionRepository.findById(mentionId)
                .orElseThrow(() -> new EntityNotFoundException("Đề cập không tồn tại với ID: " + mentionId));
        
        mentionRepository.delete(mention);
    }
    
    @Override
    public MentionDTO getMentionById(Long mentionId) {
        Mention mention = mentionRepository.findById(mentionId)
                .orElseThrow(() -> new EntityNotFoundException("Đề cập không tồn tại với ID: " + mentionId));
        
        return convertToDTO(mention);
    }
    
    @Override
    public List<MentionDTO> getMentionsByMentionedUserId(Long mentionedUserId) {
        List<Mention> mentions = mentionRepository.findByMentionedUserId(mentionedUserId);
        return mentions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<MentionDTO> getMentionsByMentionedUserId(Long mentionedUserId, Pageable pageable) {
        Page<Mention> mentionPage = mentionRepository.findByMentionedUserId(mentionedUserId, pageable);
        return mentionPage.map(this::convertToDTO);
    }
    
    @Override
    public List<MentionDTO> getMentionsByMentioningUserId(Long mentioningUserId) {
        List<Mention> mentions = mentionRepository.findByMentioningUserId(mentioningUserId);
        return mentions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<MentionDTO> getMentionsByMentioningUserId(Long mentioningUserId, Pageable pageable) {
        Page<Mention> mentionPage = mentionRepository.findByMentioningUserId(mentioningUserId, pageable);
        return mentionPage.map(this::convertToDTO);
    }
    
    @Override
    public List<MentionDTO> getMentionsByPostId(Long postId) {
        List<Mention> mentions = mentionRepository.findByPostId(postId);
        return mentions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<MentionDTO> getMentionsByPostId(Long postId, Pageable pageable) {
        Page<Mention> mentionPage = mentionRepository.findByPostId(postId, pageable);
        return mentionPage.map(this::convertToDTO);
    }
    
    @Override
    public List<MentionDTO> getMentionsByCommentId(Long commentId) {
        List<Mention> mentions = mentionRepository.findByCommentId(commentId);
        return mentions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<MentionDTO> getMentionsByCommentId(Long commentId, Pageable pageable) {
        Page<Mention> mentionPage = mentionRepository.findByCommentId(commentId, pageable);
        return mentionPage.map(this::convertToDTO);
    }
    
    @Override
    public List<MentionDTO> getUnreadMentionsByUserId(Long userId) {
        List<Mention> mentions = mentionRepository.findUnreadMentionsByUserId(userId);
        return mentions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<MentionDTO> getUnreadMentionsByUserId(Long userId, Pageable pageable) {
        Page<Mention> mentionPage = mentionRepository.findUnreadMentionsByUserId(userId, pageable);
        return mentionPage.map(this::convertToDTO);
    }
    
    @Override
    public long countMentionsByMentionedUserId(Long mentionedUserId) {
        return mentionRepository.countByMentionedUserId(mentionedUserId);
    }
    
    @Override
    public Page<MentionDTO> searchMentions(
            Long mentionedUserId,
            Long mentioningUserId,
            Long postId,
            Long commentId,
            Pageable pageable) {
        Page<Mention> mentionPage = mentionRepository.searchMentions(
                mentionedUserId, mentioningUserId, postId, commentId, pageable);
        return mentionPage.map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public void deleteMentionsByPostId(Long postId) {
        List<Mention> mentions = mentionRepository.findByPostId(postId);
        mentionRepository.deleteAll(mentions);
    }
    
    @Override
    @Transactional
    public void deleteMentionsByCommentId(Long commentId) {
        List<Mention> mentions = mentionRepository.findByCommentId(commentId);
        mentionRepository.deleteAll(mentions);
    }
}
