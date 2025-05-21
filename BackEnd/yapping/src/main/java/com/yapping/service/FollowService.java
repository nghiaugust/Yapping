package com.yapping.service;

import com.yapping.dto.follow.FollowDTO;
import com.yapping.entity.Follow;
import com.yapping.entity.FollowId;

import java.util.List;

public interface FollowService {
    FollowDTO followUser(Long followerId, Long followedId);
    void unfollowUser(Long followerId, Long followedId);
    List<FollowDTO> getFollowers(Long userId);
    List<FollowDTO> getFollowing(Long userId);
    boolean isFollowing(Long followerId, Long followedId);
}