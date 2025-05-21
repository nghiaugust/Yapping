package com.yapping.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSubcategoryDTO {
    @NotNull(message = "ID thể loại chính không được để trống")
    private Long categoryId;
    
    @NotBlank(message = "Tên thể loại phụ không được để trống")
    @Size(max = 100, message = "Tên thể loại phụ không được vượt quá 100 ký tự")
    private String name;
    
    private String description;
}
