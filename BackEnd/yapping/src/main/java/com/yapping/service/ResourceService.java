package com.yapping.service;

import com.yapping.dto.resource.ResourceDTO;
import com.yapping.dto.resource.CreateResourceDTO;
import com.yapping.dto.resource.UpdateResourceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResourceService {
    // Lấy tất cả tài liệu (có phân trang)
    Page<ResourceDTO> getAllResources(Pageable pageable);
    
    // Lấy tài liệu theo ID
    ResourceDTO getResourceById(Long id);
    
    // Tạo tài liệu mới
    ResourceDTO createResource(CreateResourceDTO createResourceDTO, Long userId);
    
    // Cập nhật tài liệu
    ResourceDTO updateResource(Long id, UpdateResourceDTO updateResourceDTO, Long userId);
    
    // Xóa tài liệu
    void deleteResource(Long id, Long userId);
    
    // Lấy tài liệu theo người dùng
    List<ResourceDTO> getResourcesByUserId(Long userId);
    
    // Lấy tài liệu theo bài đăng
    List<ResourceDTO> getResourcesByPostId(Long postId);
    
    // Lấy tài liệu theo thể loại
    List<ResourceDTO> getResourcesByCategoryId(Long categoryId);
    
    // Lấy tài liệu theo thể loại phụ
    List<ResourceDTO> getResourcesBySubCategoryId(Long subCategoryId);
    
    // Tìm kiếm tài liệu theo tiêu đề
    List<ResourceDTO> searchResourcesByTitle(String title);
    
    // Lấy tài liệu nổi bật (nhiều lượt xem nhất)
    Page<ResourceDTO> getTrendingResources(Pageable pageable);
    
    // Lấy tài liệu mới nhất
    Page<ResourceDTO> getLatestResources(Pageable pageable);
    
    // Tìm kiếm tài liệu nâng cao
    Page<ResourceDTO> searchResources(String title, Long categoryId, Long subCategoryId, Long userId, Pageable pageable);
    
    // Tăng lượt xem tài liệu
    ResourceDTO incrementViewCount(Long id);
    
    // Tăng lượt tải tài liệu
    ResourceDTO incrementDownloadCount(Long id);
}
