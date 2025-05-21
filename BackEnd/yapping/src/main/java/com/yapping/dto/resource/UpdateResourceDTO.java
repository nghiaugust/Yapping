package com.yapping.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateResourceDTO {
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Drive link không được để trống")
    @Size(max = 255, message = "Drive link không được vượt quá 255 ký tự")
    private String driveLink;
    
    @NotNull(message = "ID thể loại chính không được để trống")
    private Long categoryId;
    
    private Long subCategoryId;
}
