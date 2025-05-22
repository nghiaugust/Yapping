package com.yapping.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentDTO {
    @NotNull(message = "ID bài đăng không được để trống")
    private Long postId;
    
    private Long parentCommentId;
    
    @NotBlank(message = "Nội dung bình luận không được để trống")
    private String content;
}
