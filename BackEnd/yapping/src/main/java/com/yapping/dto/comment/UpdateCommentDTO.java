package com.yapping.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentDTO {
    @NotBlank(message = "Nội dung bình luận không được để trống")
    private String content;
}
