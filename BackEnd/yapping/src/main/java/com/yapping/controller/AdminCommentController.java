package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    /**
     * Xóa bình luận (Admin only - không cần kiểm tra quyền sở hữu)
     * @param commentId ID của bình luận
     * @return ApiResponse
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteCommentByAdmin(commentId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Admin đã xóa bình luận thành công",
                    null
            );
            return ResponseEntity.ok(response);
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
}
