package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.media.MediaDTO;
import com.yapping.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "http://localhost:5173")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam("postId") Long postId) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            // Kiểm tra loại file
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                ApiResponse response = new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        false,
                        "Chỉ chấp nhận file ảnh hoặc video",
                        null
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Tạo media
            MediaDTO createdMedia = mediaService.createMedia(postId, userId, file);

            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tải lên media thành công",
                    createdMedia
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
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMedia(@PathVariable Long id) {
        try {
            MediaDTO media = mediaService.getMediaById(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy thông tin media thành công",
                    media
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> getMediaByPost(@PathVariable Long postId) {
        List<MediaDTO> mediaList = mediaService.getMediaByPostId(postId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách media của bài viết thành công",
                mediaList
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getMediaByUser(@PathVariable Long userId) {
        List<MediaDTO> mediaList = mediaService.getMediaByUserId(userId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách media của người dùng thành công",
                mediaList
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMedia(@PathVariable Long id, @Valid @RequestBody MediaDTO mediaDTO) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            // Kiểm tra quyền
            MediaDTO existingMedia = mediaService.getMediaById(id);
            if (!existingMedia.getUserId().equals(userId) && 
                    !SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                ApiResponse response = new ApiResponse(
                        HttpStatus.FORBIDDEN.value(),
                        false,
                        "Không có quyền cập nhật media này",
                        null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            MediaDTO updatedMedia = mediaService.updateMedia(id, mediaDTO);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật media thành công",
                    updatedMedia
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMedia(@PathVariable Long id) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            // Kiểm tra quyền
            MediaDTO existingMedia = mediaService.getMediaById(id);
            if (!existingMedia.getUserId().equals(userId) && 
                    !SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                ApiResponse response = new ApiResponse(
                        HttpStatus.FORBIDDEN.value(),
                        false,
                        "Không có quyền xóa media này",
                        null
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            mediaService.deleteMedia(id);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa media thành công",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
