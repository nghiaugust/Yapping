package com.yapping.dto.repost;

import java.time.Instant;
import lombok.Data;

@Data
public class RepostDTO {
    private Long id;
    private Long userId;
    private String username;
    private String userFullName;
    private String userProfilePicture;
    private Long postId;
    private Instant createdAt;
}
