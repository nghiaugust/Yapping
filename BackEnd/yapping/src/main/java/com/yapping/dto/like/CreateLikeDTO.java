package com.yapping.dto.like;

import com.yapping.entity.Like.TargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLikeDTO {
    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;
    
    @NotNull(message = "Loại đối tượng không được để trống")
    private TargetType targetType;
    
    @NotNull(message = "ID đối tượng không được để trống")
    private Long targetId;
}
