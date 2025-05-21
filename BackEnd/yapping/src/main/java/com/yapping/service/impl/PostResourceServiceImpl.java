package com.yapping.service.impl;

import com.yapping.dto.post.PostDTO;
import com.yapping.dto.post.PostWithResourceDTO;
import com.yapping.dto.resource.ResourceDTO;
import com.yapping.entity.Category;
import com.yapping.entity.Post;
import com.yapping.entity.Resource;
import com.yapping.entity.Subcategory;
import com.yapping.entity.User;
import com.yapping.repository.CategoryRepository;
import com.yapping.repository.PostRepository;
import com.yapping.repository.ResourceRepository;
import com.yapping.repository.SubcategoryRepository;
import com.yapping.repository.UserRepository;
import com.yapping.service.PostResourceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostResourceServiceImpl implements PostResourceService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Override
    @Transactional
    public PostDTO createPostWithResource(PostWithResourceDTO postWithResourceDTO, Long userId) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        // 1. Tạo Post trước
        Post post = new Post();
        post.setContent(postWithResourceDTO.getContent());
        post.setVisibility(postWithResourceDTO.getVisibility());
        post.setUser(user);
        // Set post_type là RESOURCE
        post.setPost_type(Post.Type.RESOURCE);
        
        // Khởi tạo các giá trị mặc định
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setRepostCount(0);
        post.setQuoteCount(0);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        // Xử lý parentPost nếu có
        if (postWithResourceDTO.getParentPostId() != null) {
            Post parentPost = postRepository.findById(postWithResourceDTO.getParentPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết gốc với ID: " + postWithResourceDTO.getParentPostId()));
            post.setParentPost(parentPost);
        }

        // Lưu post
        Post savedPost = postRepository.save(post);

        // 2. Tạo Resource
        Resource resource = createResource(postWithResourceDTO, savedPost, user);
        
        // 3. Chuyển Post thành DTO (kèm theo thông tin Resource)
        PostDTO postDTO = convertToPostDTO(savedPost, resource);

        return postDTO;
    }
    
    private Resource createResource(PostWithResourceDTO dto, Post post, User user) {
        // Kiểm tra thông tin bắt buộc
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề tài liệu không được để trống");
        }
        if (dto.getDriveLink() == null || dto.getDriveLink().trim().isEmpty()) {
            throw new IllegalArgumentException("Link tài liệu không được để trống");
        }
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("ID thể loại không được để trống");
        }
        
        // Tìm category
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + dto.getCategoryId()));
        
        // Tìm subcategory nếu có
        Subcategory subcategory = null;
        if (dto.getSubCategoryId() != null) {
            subcategory = subcategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại phụ với ID: " + dto.getSubCategoryId()));
            
            // Kiểm tra subcategory thuộc category
            if (!subcategory.getCategory().getId().equals(category.getId())) {
                throw new IllegalArgumentException("Thể loại phụ không thuộc thể loại chính đã chọn");
            }
        }
        
        // Tạo resource mới
        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setDescription(dto.getDescription());
        resource.setDriveLink(dto.getDriveLink());
        resource.setUser(user);
        resource.setPost(post);
        resource.setCategory(category);
        resource.setSubCategory(subcategory);
        resource.setViewCount(0);
        resource.setDownloadCount(0);
        resource.setCreatedAt(Instant.now());
        resource.setUpdatedAt(Instant.now());
        
        // ID tự tăng hoặc tạo trước khi lưu
        if (resource.getId() == null) {
            Long maxId = resourceRepository.findAll().stream()
                .map(Resource::getId)
                .max(Long::compareTo)
                .orElse(0L);
            resource.setId(maxId + 1);
        }
        
        return resourceRepository.save(resource);
    }

    private PostDTO convertToPostDTO(Post post, Resource resource) {
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO, "user", "parentPost");

        // Thiết lập thông tin người dùng
        PostDTO.UserDTO userDTO = new PostDTO.UserDTO();
        BeanUtils.copyProperties(post.getUser(), userDTO);
        postDTO.setUser(userDTO);

        // Thiết lập ID bài đăng cha nếu có
        postDTO.setParentPostId(post.getParentPost() != null ? post.getParentPost().getId() : null);
        
        // Chuyển đổi Resource thành ResourceDTO và thêm vào Post
        if (resource != null) {
            ResourceDTO resourceDTO = new ResourceDTO();
            resourceDTO.setId(resource.getId());
            resourceDTO.setPostId(post.getId());
            resourceDTO.setUserId(post.getUser().getId());
            resourceDTO.setUserName(post.getUser().getUsername());
            resourceDTO.setTitle(resource.getTitle());
            resourceDTO.setDescription(resource.getDescription());
            resourceDTO.setDriveLink(resource.getDriveLink());
            resourceDTO.setCategoryId(resource.getCategory().getId());
            resourceDTO.setCategoryName(resource.getCategory().getName());
            
            if (resource.getSubCategory() != null) {
                resourceDTO.setSubCategoryId(resource.getSubCategory().getId());
                resourceDTO.setSubCategoryName(resource.getSubCategory().getName());
            }
            
            resourceDTO.setViewCount(resource.getViewCount());
            resourceDTO.setDownloadCount(resource.getDownloadCount());
            resourceDTO.setCreatedAt(resource.getCreatedAt());
            resourceDTO.setUpdatedAt(resource.getUpdatedAt());
            
            // Thêm resourceDTO vào danh sách resources của postDTO
            List<ResourceDTO> resources = new ArrayList<>();
            resources.add(resourceDTO);
            postDTO.setResources(resources);
        }

        return postDTO;
    }
}
