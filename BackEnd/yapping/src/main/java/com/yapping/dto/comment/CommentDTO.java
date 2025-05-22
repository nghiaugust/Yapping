package com.yapping.dto.comment;

import java.time.Instant;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String userFullName;
    private String userProfilePicture;
    private Long parentCommentId;
    private String content;
    private Integer likeCount;
    private Instant createdAt;
    private Instant updatedAt;
}
