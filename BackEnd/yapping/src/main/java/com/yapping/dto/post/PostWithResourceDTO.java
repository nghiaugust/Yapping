package com.yapping.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yapping.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostWithResourceDTO {
    private String content;
    private Post.Visibility visibility = Post.Visibility.PUBLIC;
    private Long parentPostId;
    
    // Thông tin tài liệu
    private String title; // Tiêu đề tài liệu
    private String description; // Mô tả tài liệu
    private String driveLink; // Link tài liệu
    private Long categoryId; // ID thể loại
    private Long subCategoryId; // ID thể loại phụ (có thể null)
}
