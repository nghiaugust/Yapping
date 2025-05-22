package com.yapping.service.impl;

import com.yapping.dto.follow.FollowDTO;
import com.yapping.entity.Follow;
import com.yapping.entity.FollowId;
import com.yapping.entity.User;
import com.yapping.repository.FollowRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.FollowService;
import com.yapping.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService { 

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public FollowDTO followUser(Long followerId, Long followedId) {
        // Kiểm tra người dùng tồn tại
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + followerId));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + followedId));

        // Kiểm tra xem đã follow chưa
        FollowId followId = new FollowId();
        followId.setFollowerId(followerId);
        followId.setFollowedId(followedId);

        if (followRepository.existsById(followId)) {
            throw new RuntimeException("Bạn đã follow người dùng này rồi");
        }

        // Tạo follow mới
        Follow follow = new Follow();
        follow.setId(followId);
        follow.setFollower(follower);
        follow.setFollowed(followed);
        follow.setCreatedAt(Instant.now());        
        followRepository.save(follow);

        // Tạo thông báo cho người được follow
        notificationService.createFollowNotification(followerId, followedId);

        // Chuyển đổi thành DTO để trả về
        FollowDTO followDTO = new FollowDTO();
        followDTO.setFollowerId(followerId);
        followDTO.setFollowedId(followedId);

        return followDTO;
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followedId) {
        FollowId followId = new FollowId();
        followId.setFollowerId(followerId);
        followId.setFollowedId(followedId);

        if (!followRepository.existsById(followId)) {
            throw new RuntimeException("Bạn chưa follow người dùng này");
        }

        followRepository.deleteById(followId);
    }

    @Override
    public List<FollowDTO> getFollowers(Long userId) {
        return followRepository.findByFollowedId(userId).stream()
                .map(follow -> {
                    FollowDTO dto = new FollowDTO();
                    dto.setFollowerId(follow.getFollower().getId());
                    dto.setFollowedId(follow.getFollowed().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<FollowDTO> getFollowing(Long userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(follow -> {
                    FollowDTO dto = new FollowDTO();
                    dto.setFollowerId(follow.getFollower().getId());
                    dto.setFollowedId(follow.getFollowed().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFollowing(Long followerId, Long followedId) {
        FollowId followId = new FollowId();
        followId.setFollowerId(followerId);
        followId.setFollowedId(followedId);
        return followRepository.existsById(followId);
    }
}