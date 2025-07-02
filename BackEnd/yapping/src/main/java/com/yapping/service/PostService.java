package com.yapping.service;

import com.yapping.dto.post.PatchPostDTO;
import com.yapping.dto.post.PostDTO;
import com.yapping.dto.post.PostStatisticsDTO;
import com.yapping.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostService {

    PostDTO createPost(PostDTO postDTO, Long userId);

    Optional<PostDTO> getPostById(Long id, Long userId);

    Page<PostDTO> getPublicPosts(Pageable pageable);

    Page<PostDTO> getPostsByUserId(Long userId, Pageable pageable);

    PostDTO updatePost(Long id, PostDTO postDTO, Long userId);    
    
    PostDTO deletePost(Long id, Long userId);

    PostDTO patchPost(Long id, PatchPostDTO patchPostDTO, Long userId);
    
    // Tăng lượt thích cho bài đăng
    PostDTO likePost(Long postId, Long userId);
    
    // Admin methods
    Page<PostDTO> getAllPostsForAdmin(Pageable pageable, Post.Visibility visibility, Post.Type postType);
    
    Optional<PostDTO> getPostByIdForAdmin(Long id);
    
    PostDTO updatePostVisibilityByAdmin(Long id, Post.Visibility visibility);
    
    PostDTO deletePostByAdmin(Long id);
    
    PostStatisticsDTO getPostStatistics();
}