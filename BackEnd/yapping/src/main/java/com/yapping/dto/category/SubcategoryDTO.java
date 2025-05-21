package com.yapping.dto.category;

import lombok.Data;
import java.time.Instant;

@Data
public class SubcategoryDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private Instant createdAt;
}
