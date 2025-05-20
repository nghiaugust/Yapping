package com.yapping.service.impl;

import com.yapping.dto.post.PatchPostDTO;
import com.yapping.dto.post.PostDTO;
import com.yapping.entity.Post;
import com.yapping.entity.User;
import com.yapping.repository.PostRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private PostDTO toPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO, "user", "parentPost");

        // Ánh xạ User sang UserDTO
        PostDTO.UserDTO userDTO = new PostDTO.UserDTO();
        BeanUtils.copyProperties(post.getUser(), userDTO);
        postDTO.setUser(userDTO);

        // Ánh xạ parentPostId
        postDTO.setParentPostId(post.getParentPost() != null ? post.getParentPost().getId() : null);

        return postDTO;
    }

    @Override
    public PostDTO createPost(PostDTO postDTO, Long userId) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Chuyển DTO thành entity
        Post post = new Post();
        BeanUtils.copyProperties(postDTO, post, "id", "user", "parentPostId", "createdAt", "updatedAt");
        post.setUser(user);

        // Xử lý parentPost nếu có
        if (postDTO.getParentPostId() != null) {
            Post parentPost = postRepository.findById(postDTO.getParentPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent post not found with id: " + postDTO.getParentPostId()));
            post.setParentPost(parentPost);
        }

        // Lưu post
        Post savedPost = postRepository.save(post);

        // Chuyển entity thành DTO
        return toPostDTO(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostDTO> getPostById(Long id) {
        return postRepository.findById(id).map(this::toPostDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPublicPosts(Pageable pageable) {
        return postRepository.findByVisibility(Post.Visibility.PUBLIC, pageable)
                .map(this::toPostDTO);
    }

    @Override
    public PostDTO updatePost(Long id, PostDTO postDTO, Long userId) {
        // Tìm post
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        // Kiểm tra quyền
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this post");
        }

        // Cập nhật thông tin từ DTO
        BeanUtils.copyProperties(postDTO, post, "id", "user", "parentPostId", "createdAt", "updatedAt");

        // Xử lý parentPost nếu có thay đổi
        if (postDTO.getParentPostId() != null) {
            Post parentPost = postRepository.findById(postDTO.getParentPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent post not found with id: " + postDTO.getParentPostId()));
            post.setParentPost(parentPost);
        } else {
            post.setParentPost(null);
        }

        // Lưu post
        Post updatedPost = postRepository.save(post);

        // Chuyển entity thành DTO
        return toPostDTO(updatedPost);
    }

    @Override
    public PostDTO deletePost(Long id, Long userId) {
        // Tìm post
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        // Kiểm tra quyền
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to delete this post");
        }

        // Xóa post
        postRepository.delete(post);
        return toPostDTO(post);
    }

    @Override
    public PostDTO patchPost(Long id, PatchPostDTO patchPostDTO, Long userId) {
        // Tìm bài đăng
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        // Kiểm tra quyền
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized to update this post");
        }

        // Cập nhật các thuộc tính nếu được cung cấp
        if (patchPostDTO.getContent() != null) {
            post.setContent(patchPostDTO.getContent());
        }
        if (patchPostDTO.getVisibility() != null) {
            post.setVisibility(patchPostDTO.getVisibility());
        }
        if (patchPostDTO.getParentPostId() != null) {
            Post parentPost = postRepository.findById(patchPostDTO.getParentPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent post not found with id: " + patchPostDTO.getParentPostId()));
            post.setParentPost(parentPost);
        } else if (patchPostDTO.getParentPostId() == null && post.getParentPost() != null) {
            post.setParentPost(null); // Cho phép xóa parentPost
        }

        // Lưu bài đăng
        Post updatedPost = postRepository.save(post);

        // Chuyển thành DTO
        return toPostDTO(updatedPost);
    }

}