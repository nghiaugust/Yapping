package com.yapping.dto;

import com.yapping.entity.Post;
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
}