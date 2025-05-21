package com.yapping.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yapping.entity.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostWithMediaDTO {
    private String content;
    private Post.Visibility visibility = Post.Visibility.PUBLIC;
    private Long parentPostId;
    private Post.Type post_type;
}
