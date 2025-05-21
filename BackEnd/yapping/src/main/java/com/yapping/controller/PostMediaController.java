package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.post.PostDTO;
import com.yapping.dto.post.PostWithMediaDTO;
import com.yapping.entity.Post;
import com.yapping.service.PostMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts-with-media")
@CrossOrigin(origins = "http://localhost:5173")
public class PostMediaController {

    @Autowired
    private PostMediaService postMediaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createPostWithMedia(
            @RequestPart(value = "content", required = true) String content,
            @RequestPart(value = "visibility", required = true) String visibility,
            @RequestPart(value = "post_type", required = true) String type,
            @RequestPart(value = "parentPostId", required = false) String parentPostId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

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

            // Kiểm tra loại file
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    String contentType = file.getContentType();
                    String originalFileName = file.getOriginalFilename();
                    boolean isValidFile = false;

                    if (contentType != null && (contentType.startsWith("image/") || contentType.startsWith("video/"))) {
                        isValidFile = true;
                    } else if (originalFileName != null) {
                        String lowercaseFileName = originalFileName.toLowerCase();
                        if (lowercaseFileName.endsWith(".jpg") || lowercaseFileName.endsWith(".jpeg") ||
                                lowercaseFileName.endsWith(".png") || lowercaseFileName.endsWith(".gif") ||
                                lowercaseFileName.endsWith(".mp4") || lowercaseFileName.endsWith(".mov")) {
                            isValidFile = true;
                        }
                    }

                    if (!isValidFile) {
                        ApiResponse response = new ApiResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                false,
                                "Chỉ chấp nhận file ảnh hoặc video",
                                null
                        );
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                }
            }

            // Tạo PostWithMediaDTO từ các tham số
            PostWithMediaDTO postWithMediaDTO = new PostWithMediaDTO();
            postWithMediaDTO.setContent(content);
            postWithMediaDTO.setVisibility(Post.Visibility.valueOf(visibility));
            if (parentPostId != null && !parentPostId.isEmpty()) {
                try {
                    postWithMediaDTO.setParentPostId(Long.parseLong(parentPostId));
                } catch (NumberFormatException e) {
                    ApiResponse response = new ApiResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            false,
                            "parentPostId không hợp lệ",
                            null
                    );
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }

            // Tạo bài đăng với media
            PostDTO createdPost = postMediaService.createPostWithMedia(postWithMediaDTO, files, userId);

            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo bài đăng với media thành công",
                    createdPost
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Không thể lưu file: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    "Giá trị visibility không hợp lệ: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
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