package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.post.PostDTO;
import com.yapping.dto.post.PostStatisticsDTO;
import com.yapping.dto.post.UpdateVisibilityDTO;
import com.yapping.entity.Post;
import com.yapping.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    @Autowired
    private PostService postService;

    /**
     * Lấy danh sách tất cả bài đăng cho admin
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Post.Visibility visibility,
            @RequestParam(required = false) Post.Type postType) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostDTO> posts = postService.getAllPostsForAdmin(pageable, visibility, postType);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Lấy danh sách bài đăng thành công",
                posts
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy chi tiết bài đăng cho admin
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPostById(@PathVariable Long id) {
        Optional<PostDTO> postDTO = postService.getPostByIdForAdmin(id);
        if (postDTO.isPresent()) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Lấy thông tin bài đăng thành công",
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

    /**
     * Cập nhật trạng thái hiển thị bài đăng
     */
    @PatchMapping("/{id}/visibility")
    public ResponseEntity<ApiResponse> updatePostVisibility(
            @PathVariable Long id,
            @RequestBody UpdateVisibilityDTO updateVisibilityDTO) {
        
        PostDTO updatedPost = postService.updatePostVisibilityByAdmin(id, updateVisibilityDTO.getVisibility());
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Cập nhật trạng thái bài đăng thành công",
                updatedPost
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa bài đăng (Admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long id) {
        PostDTO deletedPost = postService.deletePostByAdmin(id);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Xóa bài đăng thành công",
                deletedPost
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy thống kê bài đăng
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getPostStatistics() {
        PostStatisticsDTO statistics = postService.getPostStatistics();
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Lấy thống kê bài đăng thành công",
                statistics
        );
        return ResponseEntity.ok(response);
    }
}
