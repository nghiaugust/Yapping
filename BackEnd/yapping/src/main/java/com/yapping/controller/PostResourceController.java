package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.post.PostDTO;
import com.yapping.dto.post.PostWithResourceDTO;
import com.yapping.entity.Post;
import com.yapping.service.PostResourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts-with-resource")
@CrossOrigin(origins = "http://localhost:5173")
public class PostResourceController {

    @Autowired
    private PostResourceService postResourceService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createPostWithResource(@Valid @RequestBody PostWithResourceDTO postWithResourceDTO) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = claims != null ? (Long) claims.get("userId") : null;
            
            if (userId == null) {
                ApiResponse response = new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        false,
                        "Không tìm thấy userId trong token",
                        null
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Kiểm tra các trường bắt buộc
            if (postWithResourceDTO.getContent() == null || postWithResourceDTO.getContent().trim().isEmpty()) {
                ApiResponse response = new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        false,
                        "Nội dung bài đăng không được để trống",
                        null
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (postWithResourceDTO.getTitle() == null || postWithResourceDTO.getTitle().trim().isEmpty()) {
                ApiResponse response = new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        false,
                        "Tiêu đề tài liệu không được để trống",
                        null
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (postWithResourceDTO.getDriveLink() == null || postWithResourceDTO.getDriveLink().trim().isEmpty()) {
                ApiResponse response = new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        false,
                        "Link tài liệu không được để trống",
                        null
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (postWithResourceDTO.getCategoryId() == null) {
                ApiResponse response = new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        false,
                        "Thể loại không được để trống",
                        null
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Đảm bảo visibility không null
            if (postWithResourceDTO.getVisibility() == null) {
                postWithResourceDTO.setVisibility(Post.Visibility.PUBLIC);
            }

            // Tạo bài đăng với tài liệu
            PostDTO createdPost = postResourceService.createPostWithResource(postWithResourceDTO, userId);

            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo bài đăng với tài liệu thành công",
                    createdPost
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Lỗi hệ thống: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
