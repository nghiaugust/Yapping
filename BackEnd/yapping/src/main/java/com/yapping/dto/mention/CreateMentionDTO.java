package com.yapping.dto.mention;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMentionDTO {
    @NotNull(message = "ID của người dùng được đề cập không được để trống")
    private Long mentionedUserId;
    
    @NotNull(message = "ID của người đề cập không được để trống")
    private Long mentioningUserId;
    
    private Long postId;
    
    private Long commentId;
}
