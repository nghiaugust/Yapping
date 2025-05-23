package com.yapping.service.impl;

import com.yapping.dto.repost.CreateRepostDTO;
import com.yapping.dto.repost.RepostDTO;
import com.yapping.entity.Post;
import com.yapping.entity.Repost;
import com.yapping.entity.User;
import com.yapping.entity.Notification.Type;
import com.yapping.entity.Notification.TargetType;
import com.yapping.repository.PostRepository;
import com.yapping.repository.RepostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.NotificationService;
import com.yapping.service.RepostService;
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
public class RepostServiceImpl implements RepostService {

    @Autowired
    private RepostRepository repostRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public RepostDTO createRepost(CreateRepostDTO createRepostDTO, Long userId) {
        // Kiểm tra bài đăng tồn tại
        Post post = postRepository.findById(createRepostDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với ID: " + createRepostDTO.getPostId()));
        
        // Kiểm tra người dùng tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Kiểm tra xem đã tồn tại repost này chưa
        if (repostRepository.existsByUserIdAndPostId(userId, createRepostDTO.getPostId())) {
            throw new IllegalStateException("Người dùng đã đăng lại bài viết này");
        }
        
        // Tạo mới repost
        Repost repost = new Repost();
        repost.setUser(user);
        repost.setPost(post);
        repost.setCreatedAt(Instant.now());
        
        Repost savedRepost = repostRepository.save(repost);
        
        // Tăng số lượt repost của bài đăng
        post.setRepostCount(post.getRepostCount() == null ? 1 : post.getRepostCount() + 1);
        postRepository.save(post);
        
        // Tạo thông báo cho chủ bài đăng nếu người repost không phải là chủ bài đăng
        if (!userId.equals(post.getUser().getId())) {
            notificationService.createRepostNotification(
                    userId, 
                    createRepostDTO.getPostId(), 
                    post.getUser().getId()
            );
        }
        
        return convertToDTO(savedRepost);
    }

    @Override
    @Transactional
    public void deleteRepost(Long repostId, Long userId) {
        Repost repost = repostRepository.findById(repostId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy repost với ID: " + repostId));
        
        // Kiểm tra quyền xóa
        if (!repost.getUser().getId().equals(userId)) {
            throw new SecurityException("Không có quyền xóa repost này");
        }
        
        // Giảm số lượt repost của bài đăng
        Post post = repost.getPost();
        post.setRepostCount(post.getRepostCount() == null ? 0 : Math.max(0, post.getRepostCount() - 1));
        postRepository.save(post);
        
        repostRepository.delete(repost);
    }

    @Override
    @Transactional
    public void deleteRepostByUserAndPost(Long userId, Long postId) {
        Optional<Repost> repostOpt = repostRepository.findByUserIdAndPostId(userId, postId);
        
        if (repostOpt.isPresent()) {
            Repost repost = repostOpt.get();
            
            // Giảm số lượt repost của bài đăng
            Post post = repost.getPost();
            post.setRepostCount(post.getRepostCount() == null ? 0 : Math.max(0, post.getRepostCount() - 1));
            postRepository.save(post);
            
            repostRepository.delete(repost);
        }
    }

    @Override
    public RepostDTO getRepostById(Long repostId) {
        Repost repost = repostRepository.findById(repostId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy repost với ID: " + repostId));
        
        return convertToDTO(repost);
    }

    @Override
    public List<RepostDTO> getRepostsByUserId(Long userId) {
        List<Repost> reposts = repostRepository.findByUserId(userId);
        return reposts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RepostDTO> getRepostsByUserId(Long userId, Pageable pageable) {
        Page<Repost> repostPage = repostRepository.findByUserId(userId, pageable);
        return repostPage.map(this::convertToDTO);
    }

    @Override
    public List<RepostDTO> getRepostsByPost(Long postId) {
        List<Repost> reposts = repostRepository.findByPostId(postId);
        return reposts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RepostDTO> getRepostsByPost(Long postId, Pageable pageable) {
        Page<Repost> repostPage = repostRepository.findByPostId(postId, pageable);
        return repostPage.map(this::convertToDTO);
    }

    @Override
    public boolean hasUserReposted(Long userId, Long postId) {
        return repostRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public long countReposts(Long postId) {
        return repostRepository.countByPostId(postId);
    }

    @Override
    public List<RepostDTO> getRepostsByDateRange(Instant startDate, Instant endDate) {
        List<Repost> reposts = repostRepository.findByCreatedAtBetween(startDate, endDate);
        return reposts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RepostDTO> getRepostsByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        Page<Repost> repostPage = repostRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return repostPage.map(this::convertToDTO);
    }

    @Override
    public Page<RepostDTO> searchReposts(
            Long userId,
            Long postId,
            Instant startDate,
            Instant endDate,
            Pageable pageable) {
        Page<Repost> repostPage = repostRepository.searchReposts(userId, postId, startDate, endDate, pageable);
        return repostPage.map(this::convertToDTO);
    }
    
    // Phương thức hỗ trợ chuyển đổi từ entity sang DTO
    private RepostDTO convertToDTO(Repost repost) {
        RepostDTO repostDTO = new RepostDTO();
        repostDTO.setId(repost.getId());
        repostDTO.setUserId(repost.getUser().getId());
        repostDTO.setUsername(repost.getUser().getUsername());
        repostDTO.setUserFullName(repost.getUser().getFullName());
        repostDTO.setUserProfilePicture(repost.getUser().getProfilePicture());
        repostDTO.setPostId(repost.getPost().getId());
        repostDTO.setCreatedAt(repost.getCreatedAt());
        return repostDTO;
    }
}
