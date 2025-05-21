package com.yapping.dto.resource;

import lombok.Data;
import java.time.Instant;

@Data
public class ResourceDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String userName; // Tên người dùng đăng tài liệu
    private String title;
    private String description;
    private String driveLink;
    private Long categoryId;
    private String categoryName;
    private Long subCategoryId;
    private String subCategoryName;
    private Integer viewCount;
    private Integer downloadCount;
    private Instant createdAt;
    private Instant updatedAt;
}
