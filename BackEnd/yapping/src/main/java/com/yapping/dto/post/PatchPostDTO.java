package com.yapping.dto.post;

import com.yapping.entity.Post;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchPostDTO {
    private String content;
    private Post.Visibility visibility;
    private Long parentPostId;
    private Post.Type post_type;
}