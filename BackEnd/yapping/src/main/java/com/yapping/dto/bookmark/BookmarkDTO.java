package com.yapping.dto.bookmark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private Long userId;
    private Long postId;
    private String username;
    private String userFullName;
    private String userProfilePicture;
    private String postContent;
    private Instant createdAt;
}
