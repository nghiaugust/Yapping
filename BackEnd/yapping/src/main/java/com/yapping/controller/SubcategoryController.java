package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.category.SubcategoryDTO;
import com.yapping.dto.category.CreateSubcategoryDTO;
import com.yapping.service.SubcategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subcategories")
@CrossOrigin(origins = "http://localhost:5173")
public class SubcategoryController {

    @Autowired
    private SubcategoryService subcategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllSubcategories() {
        List<SubcategoryDTO> subcategories = subcategoryService.getAllSubcategories();
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách thể loại phụ thành công",
                subcategories
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getSubcategoriesByCategoryId(@PathVariable Long categoryId) {
        try {
            List<SubcategoryDTO> subcategories = subcategoryService.getSubcategoriesByCategoryId(categoryId);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy danh sách thể loại phụ theo thể loại chính thành công",
                    subcategories
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSubcategoryById(@PathVariable Long id) {
        try {
            SubcategoryDTO subcategory = subcategoryService.getSubcategoryById(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy thông tin thể loại phụ thành công",
                    subcategory
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
    public ResponseEntity<ApiResponse> createSubcategory(@Valid @RequestBody CreateSubcategoryDTO createSubcategoryDTO) {
        try {
            SubcategoryDTO createdSubcategory = subcategoryService.createSubcategory(createSubcategoryDTO);
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo thể loại phụ thành công",
                    createdSubcategory
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSubcategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubcategoryDTO updateSubcategoryDTO) {
        try {
            SubcategoryDTO updatedSubcategory = subcategoryService.updateSubcategory(id, updateSubcategoryDTO);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật thể loại phụ thành công",
                    updatedSubcategory
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
    public ResponseEntity<ApiResponse> deleteSubcategory(@PathVariable Long id) {
        try {
            subcategoryService.deleteSubcategory(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa thể loại phụ thành công",
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
