package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.category.CategoryDTO;
import com.yapping.dto.category.CreateCategoryDTO;
import com.yapping.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách thể loại thành công",
                categories
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        try {
            CategoryDTO category = categoryService.getCategoryById(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy thông tin thể loại thành công",
                    category
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CreateCategoryDTO createCategoryDTO) {
        try {
            CategoryDTO createdCategory = categoryService.createCategory(createCategoryDTO);
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo thể loại thành công",
                    createdCategory
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryDTO updateCategoryDTO) {
        try {
            CategoryDTO updatedCategory = categoryService.updateCategory(id, updateCategoryDTO);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật thể loại thành công",
                    updatedCategory
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException 
                    ? HttpStatus.BAD_REQUEST
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa thể loại thành công",
                    null
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
