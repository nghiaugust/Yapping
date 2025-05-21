package com.yapping.dto.category;

import lombok.Data;
import java.time.Instant;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
}
