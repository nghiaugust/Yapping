package com.yapping.dto.post;

import com.yapping.entity.Post;

public class UpdateVisibilityDTO {
    private Post.Visibility visibility;

    // Constructors
    public UpdateVisibilityDTO() {}

    public UpdateVisibilityDTO(Post.Visibility visibility) {
        this.visibility = visibility;
    }

    // Getters and Setters
    public Post.Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Post.Visibility visibility) {
        this.visibility = visibility;
    }
}
