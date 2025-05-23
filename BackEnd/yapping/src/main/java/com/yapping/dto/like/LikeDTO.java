package com.yapping.dto.like;

import java.time.Instant;
import lombok.Data;
import com.yapping.entity.Like.TargetType;

@Data
public class LikeDTO {
    private Long id;
    private Long userId;
    private String username;
    private String userFullName;
    private String userProfilePicture;
    private TargetType targetType;
    private Long targetId;
    private Instant createdAt;
}
