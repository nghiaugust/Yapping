package com.yapping.service.impl;

import com.yapping.dto.resource.ResourceDTO;
import com.yapping.dto.resource.CreateResourceDTO;
import com.yapping.dto.resource.UpdateResourceDTO;
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
import com.yapping.service.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceDTO> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceDTO getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài liệu với ID: " + id));
        return convertToDTO(resource);
    }

    @Override
    @Transactional
    public ResourceDTO createResource(CreateResourceDTO createResourceDTO, Long userId) {
        // Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Kiểm tra post tồn tại
        Post post = postRepository.findById(createResourceDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với ID: " + createResourceDTO.getPostId()));
                
        // Kiểm tra quyền - chỉ người tạo post mới có thể thêm tài liệu cho post đó
        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("Người dùng không có quyền thêm tài liệu cho bài đăng này");
        }
        
        // Kiểm tra category tồn tại
        Category category = categoryRepository.findById(createResourceDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + createResourceDTO.getCategoryId()));
        
        // Kiểm tra subcategory nếu có
        Subcategory subcategory = null;
        if (createResourceDTO.getSubCategoryId() != null) {
            subcategory = subcategoryRepository.findById(createResourceDTO.getSubCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại phụ với ID: " + createResourceDTO.getSubCategoryId()));
            
            // Kiểm tra subcategory thuộc category
            if (!subcategory.getCategory().getId().equals(category.getId())) {
                throw new IllegalArgumentException("Thể loại phụ không thuộc thể loại chính đã chọn");
            }
        }
        
        // Cập nhật kiểu bài đăng thành RESOURCE
        post.setPost_type(Post.Type.RESOURCE);
        postRepository.save(post);
        
        // Tạo tài liệu mới
        Resource resource = new Resource();
        BeanUtils.copyProperties(createResourceDTO, resource);
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
        
        Resource savedResource = resourceRepository.save(resource);
        return convertToDTO(savedResource);
    }

    @Override
    @Transactional
    public ResourceDTO updateResource(Long id, UpdateResourceDTO updateResourceDTO, Long userId) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài liệu với ID: " + id));
        
        // Kiểm tra quyền cập nhật
        if (!resource.getUser().getId().equals(userId)) {
            throw new SecurityException("Người dùng không có quyền cập nhật tài liệu này");
        }
        
        // Kiểm tra category tồn tại
        Category category = categoryRepository.findById(updateResourceDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại với ID: " + updateResourceDTO.getCategoryId()));
        
        // Kiểm tra subcategory nếu có
        Subcategory subcategory = null;
        if (updateResourceDTO.getSubCategoryId() != null) {
            subcategory = subcategoryRepository.findById(updateResourceDTO.getSubCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại phụ với ID: " + updateResourceDTO.getSubCategoryId()));
            
            // Kiểm tra subcategory thuộc category
            if (!subcategory.getCategory().getId().equals(category.getId())) {
                throw new IllegalArgumentException("Thể loại phụ không thuộc thể loại chính đã chọn");
            }
        }
        
        // Cập nhật thông tin
        resource.setTitle(updateResourceDTO.getTitle());
        resource.setDescription(updateResourceDTO.getDescription());
        resource.setDriveLink(updateResourceDTO.getDriveLink());
        resource.setCategory(category);
        resource.setSubCategory(subcategory);
        resource.setUpdatedAt(Instant.now());
        
        Resource updatedResource = resourceRepository.save(resource);
        return convertToDTO(updatedResource);
    }

    @Override
    @Transactional
    public void deleteResource(Long id, Long userId) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài liệu với ID: " + id));
        
        // Kiểm tra quyền xóa
        if (!resource.getUser().getId().equals(userId)) {
            throw new SecurityException("Người dùng không có quyền xóa tài liệu này");
        }
        
        // Nếu post chỉ có 1 tài liệu, cập nhật kiểu post về TEXT
        List<Resource> postResources = resourceRepository.findByPostId(resource.getPost().getId());
        if (postResources.size() <= 1) {
            Post post = resource.getPost();
            post.setPost_type(Post.Type.TEXT);
            postRepository.save(post);
        }
        
        resourceRepository.delete(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceDTO> getResourcesByUserId(Long userId) {
        return resourceRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceDTO> getResourcesByPostId(Long postId) {
        return resourceRepository.findByPostId(postId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceDTO> getResourcesByCategoryId(Long categoryId) {
        return resourceRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceDTO> getResourcesBySubCategoryId(Long subCategoryId) {
        return resourceRepository.findBySubCategoryId(subCategoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceDTO> searchResourcesByTitle(String title) {
        return resourceRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceDTO> getTrendingResources(Pageable pageable) {
        return resourceRepository.findAllByOrderByViewCountDesc(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceDTO> getLatestResources(Pageable pageable) {
        return resourceRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceDTO> searchResources(String title, Long categoryId, Long subCategoryId, Long userId, Pageable pageable) {
        return resourceRepository.searchResources(title, categoryId, subCategoryId, userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ResourceDTO incrementViewCount(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài liệu với ID: " + id));
        
        resource.setViewCount(resource.getViewCount() == null ? 1 : resource.getViewCount() + 1);
        Resource updatedResource = resourceRepository.save(resource);
        return convertToDTO(updatedResource);
    }

    @Override
    @Transactional
    public ResourceDTO incrementDownloadCount(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài liệu với ID: " + id));
        
        resource.setDownloadCount(resource.getDownloadCount() == null ? 1 : resource.getDownloadCount() + 1);
        Resource updatedResource = resourceRepository.save(resource);
        return convertToDTO(updatedResource);
    }
    
    private ResourceDTO convertToDTO(Resource resource) {
        ResourceDTO resourceDTO = new ResourceDTO();
        BeanUtils.copyProperties(resource, resourceDTO);
        
        // Thiết lập các ID và thông tin bổ sung
        resourceDTO.setPostId(resource.getPost().getId());
        resourceDTO.setUserId(resource.getUser().getId());
        resourceDTO.setUserName(resource.getUser().getUsername());
        resourceDTO.setCategoryId(resource.getCategory().getId());
        resourceDTO.setCategoryName(resource.getCategory().getName());
        
        if (resource.getSubCategory() != null) {
            resourceDTO.setSubCategoryId(resource.getSubCategory().getId());
            resourceDTO.setSubCategoryName(resource.getSubCategory().getName());
        }
        
        return resourceDTO;
    }
}
