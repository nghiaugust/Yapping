package com.yapping.dto.post;

import com.yapping.dto.media.MediaDTO;
import com.yapping.dto.resource.ResourceDTO;
import com.yapping.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ serialize các trường không null
public class PostDTO {

    private Long id;

    //@NotNull
    private UserDTO user;

    private Long parentPostId; // ID của bài đăng gốc (nếu là bài trả lời)

    @NotBlank
    private String content;

    @NotNull
    private Post.Visibility visibility;

    private Integer likeCount= 0;

    private Integer commentCount = 0;

    private Integer repostCount= 0;

    private Integer quoteCount= 0;

    private Instant createdAt;

    private Instant updatedAt;    @NotNull
    private Post.Type post_type;
    
    // Danh sách media đính kèm    
    private List<MediaDTO> media;
    
    // Danh sách tài liệu đính kèm
    private List<ResourceDTO> resources;

    // DTO lồng nhau để lưu thông tin người dùng
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserDTO {
        private Long id;
        private String username;
        private String fullName;
        private String profilePicture;
    }
}