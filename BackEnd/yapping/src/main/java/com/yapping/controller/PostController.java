package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.post.PatchPostDTO;
import com.yapping.dto.post.PostDTO;
import com.yapping.service.PostService;
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
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createPost( @Valid @RequestBody PostDTO postDTO) {
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");

        PostDTO createdPost = postService.createPost(postDTO, userId);
        ApiResponse response = new ApiResponse(
                HttpStatus.CREATED.value(),
                true,
                "Đã tạo bài đăng thành công",
                createdPost
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPostById(@PathVariable Long id) {
        // Lấy userId từ authentication.details.claims
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");

        Optional<PostDTO> postDTO = postService.getPostById(id, userId);
        if (postDTO.isPresent()) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy thông tin bài đăng có ID " + id + " thành công",
                    postDTO.get()
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponse response = new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    "Không tìm thấy bài đăng với ID " + id,
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<ApiResponse> getPublicPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // xắp xếp theo thứ tự giảm dần của thời gian tạo
        Page<PostDTO> posts = postService.getPublicPosts(pageable);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách bài đăng công khai thành công",
                posts
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePost(@PathVariable Long id,
                                                  @Valid @RequestBody PostDTO postDTO) {
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");

        PostDTO updatedPost = postService.updatePost(id, postDTO, userId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã cập nhật bài đăng có ID " + id + " thành công",
                updatedPost
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long id){
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");

        PostDTO deletePost =postService.deletePost(id, userId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã xóa bài đăng có ID " + id + " thành công",
                deletePost
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> patchPost(@PathVariable Long id,
                                                 @Valid @RequestBody PatchPostDTO patchPostDTO) {
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");

        PostDTO updatedPost = postService.patchPost(id, patchPostDTO, userId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã cập nhật bài đăng có ID " + id + " thành công",
                updatedPost
        );
        return ResponseEntity.ok(response);
    }
}