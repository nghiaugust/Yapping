package com.yapping.dto.repost;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRepostDTO {
    @NotNull(message = "ID bài đăng không được để trống")
    private Long postId;
}
