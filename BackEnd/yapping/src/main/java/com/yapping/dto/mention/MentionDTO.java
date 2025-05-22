package com.yapping.dto.mention;

import java.time.Instant;
import lombok.Data;

@Data
public class MentionDTO {
    private Long id;
    private Long mentionedUserId;
    private String mentionedUsername;
    private String mentionedUserFullName;
    private String mentionedUserProfilePicture;
    private Long mentioningUserId;
    private String mentioningUsername;
    private String mentioningUserFullName;
    private String mentioningUserProfilePicture;
    private Long postId;
    private String postContent;
    private Long commentId;
    private String commentContent;
    private Instant createdAt;
}
