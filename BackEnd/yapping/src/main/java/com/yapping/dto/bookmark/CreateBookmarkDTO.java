package com.yapping.dto.bookmark;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookmarkDTO {
    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;
    
    @NotNull(message = "ID bài đăng không được để trống")
    private Long postId;
}
