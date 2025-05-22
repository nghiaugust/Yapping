package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.comment.CommentDTO;
import com.yapping.dto.comment.CreateCommentDTO;
import com.yapping.dto.comment.UpdateCommentDTO;
import com.yapping.service.CommentService;
import jakarta.validation.Valid;
import java.util.Map;
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

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Tạo bình luận mới
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createComment(@Valid @RequestBody CreateCommentDTO createCommentDTO) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            CommentDTO createdComment = commentService.createComment(createCommentDTO, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo bình luận thành công",
                    createdComment
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException
                    ? HttpStatus.BAD_REQUEST
                    : HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }

    // Cập nhật bình luận
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDTO updateCommentDTO) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            CommentDTO updatedComment = commentService.updateComment(commentId, updateCommentDTO, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật bình luận thành công",
                    updatedComment
            );
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.FORBIDDEN.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException
                    ? HttpStatus.BAD_REQUEST
                    : HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }

    // Xóa bình luận
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long commentId) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            commentService.deleteComment(commentId, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa bình luận thành công",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.FORBIDDEN.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException
                    ? HttpStatus.BAD_REQUEST
                    : HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }

    // Lấy bình luận theo ID
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse> getCommentById(@PathVariable Long commentId) {
        try {
            CommentDTO comment = commentService.getCommentById(commentId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy bình luận thành công",
                    comment
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

    // Lấy tất cả bình luận của một bài đăng
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<CommentDTO> commentPage = commentService.getCommentsByPostId(postId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách bình luận của bài đăng thành công",
                commentPage
        );
        return ResponseEntity.ok(response);
    }

    // Lấy tất cả bình luận gốc của một bài đăng
    @GetMapping("/post/{postId}/root")
    public ResponseEntity<ApiResponse> getRootCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<CommentDTO> rootCommentPage = commentService.getRootCommentsByPostId(postId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách bình luận gốc của bài đăng thành công",
                rootCommentPage
        );
        return ResponseEntity.ok(response);
    }

    // Lấy tất cả bình luận con của một bình luận
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<ApiResponse> getChildComments(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<CommentDTO> childCommentPage = commentService.getChildComments(commentId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách phản hồi của bình luận thành công",
                childCommentPage
        );
        return ResponseEntity.ok(response);
    }

    // Lấy tất cả bình luận của một người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<CommentDTO> commentPage = commentService.getCommentsByUserId(userId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách bình luận của người dùng thành công",
                commentPage
        );
        return ResponseEntity.ok(response);
    }    
    
    // Tăng lượt thích cho bình luận
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/{commentId}/like")
    public ResponseEntity<ApiResponse> likeComment(@PathVariable Long commentId) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            CommentDTO updatedComment = commentService.likeComment(commentId, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã thích bình luận thành công",
                    updatedComment
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

    // Đếm số bình luận của một bài đăng
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<ApiResponse> countCommentsByPostId(@PathVariable Long postId) {
        long commentCount = commentService.countCommentsByPostId(postId);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã đếm số bình luận của bài đăng thành công",
                commentCount
        );
        return ResponseEntity.ok(response);
    }
}
