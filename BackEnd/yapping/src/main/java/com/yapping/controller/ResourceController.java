package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.resource.ResourceDTO;
import com.yapping.dto.resource.CreateResourceDTO;
import com.yapping.dto.resource.UpdateResourceDTO;
import com.yapping.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "http://localhost:5173")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    // Lấy tất cả tài liệu (có phân trang)
    @GetMapping
    public ResponseEntity<ApiResponse> getAllResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ResourceDTO> resources = resourceService.getAllResources(pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách tài liệu thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tài liệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getResourceById(@PathVariable Long id) {
        try {
            ResourceDTO resource = resourceService.getResourceById(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy thông tin tài liệu thành công",
                    resource
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    // Tạo tài liệu mới
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createResource(@Valid @RequestBody CreateResourceDTO createResourceDTO) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            ResourceDTO createdResource = resourceService.createResource(createResourceDTO, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo tài liệu thành công",
                    createdResource
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof SecurityException 
                    ? HttpStatus.FORBIDDEN
                    : HttpStatus.BAD_REQUEST;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }
    
    // Cập nhật tài liệu
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody UpdateResourceDTO updateResourceDTO) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            ResourceDTO updatedResource = resourceService.updateResource(id, updateResourceDTO, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật tài liệu thành công",
                    updatedResource
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status;
            if (e instanceof SecurityException) {
                status = HttpStatus.FORBIDDEN;
            } else if (e instanceof IllegalArgumentException) {
                status = HttpStatus.BAD_REQUEST;
            } else {
                status = HttpStatus.NOT_FOUND;
            }
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }
    
    // Xóa tài liệu
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteResource(@PathVariable Long id) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            resourceService.deleteResource(id, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa tài liệu thành công",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof SecurityException 
                    ? HttpStatus.FORBIDDEN
                    : HttpStatus.NOT_FOUND;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }
    
    // Lấy tài liệu theo người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getResourcesByUserId(@PathVariable Long userId) {
        List<ResourceDTO> resources = resourceService.getResourcesByUserId(userId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách tài liệu của người dùng thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tài liệu theo bài đăng
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> getResourcesByPostId(@PathVariable Long postId) {
        List<ResourceDTO> resources = resourceService.getResourcesByPostId(postId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách tài liệu của bài đăng thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tài liệu theo thể loại
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getResourcesByCategoryId(@PathVariable Long categoryId) {
        List<ResourceDTO> resources = resourceService.getResourcesByCategoryId(categoryId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách tài liệu theo thể loại thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tài liệu theo thể loại phụ
    @GetMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<ApiResponse> getResourcesBySubCategoryId(@PathVariable Long subCategoryId) {
        List<ResourceDTO> resources = resourceService.getResourcesBySubCategoryId(subCategoryId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách tài liệu theo thể loại phụ thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Tìm kiếm tài liệu theo tiêu đề
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchResourcesByTitle(@RequestParam String title) {
        List<ResourceDTO> resources = resourceService.searchResourcesByTitle(title);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã tìm kiếm tài liệu thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tài liệu nổi bật (nhiều lượt xem nhất)
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse> getTrendingResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resources = resourceService.getTrendingResources(pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách tài liệu nổi bật thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tài liệu mới nhất
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse> getLatestResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resources = resourceService.getLatestResources(pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách tài liệu mới nhất thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Tìm kiếm tài liệu nâng cao
    @GetMapping("/advanced-search")
    public ResponseEntity<ApiResponse> searchResources(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ResourceDTO> resources = resourceService.searchResources(title, categoryId, subCategoryId, userId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã tìm kiếm tài liệu thành công",
                resources
        );
        return ResponseEntity.ok(response);
    }
    
    // Tăng lượt xem tài liệu
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse> incrementViewCount(@PathVariable Long id) {
        try {
            ResourceDTO resource = resourceService.incrementViewCount(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật lượt xem tài liệu thành công",
                    resource
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    // Tăng lượt tải tài liệu
    @PostMapping("/{id}/download")
    public ResponseEntity<ApiResponse> incrementDownloadCount(@PathVariable Long id) {
        try {
            ResourceDTO resource = resourceService.incrementDownloadCount(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật lượt tải tài liệu thành công",
                    resource
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
