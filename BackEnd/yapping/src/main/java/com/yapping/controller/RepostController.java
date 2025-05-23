package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.repost.CreateRepostDTO;
import com.yapping.dto.repost.RepostDTO;
import com.yapping.service.RepostService;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.Map;

@RestController
@RequestMapping("/api/reposts")
@CrossOrigin(origins = "http://localhost:5173")
public class RepostController {

    @Autowired
    private RepostService repostService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createRepost(@Valid @RequestBody CreateRepostDTO createRepostDTO) {
        try {
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            RepostDTO createdRepost = repostService.createRepost(createRepostDTO, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã đăng lại bài viết thành công",
                    createdRepost
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Đã xảy ra lỗi khi đăng lại bài viết: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRepost(@PathVariable Long id) {
        try {
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            repostService.deleteRepost(id, userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã hủy đăng lại bài viết thành công",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (SecurityException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.FORBIDDEN.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Đã xảy ra lỗi khi hủy đăng lại bài viết: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> deleteRepostByPost(@PathVariable Long postId) {
        try {
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            repostService.deleteRepostByUserAndPost(userId, postId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã hủy đăng lại bài viết thành công",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Đã xảy ra lỗi khi hủy đăng lại bài viết: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRepostById(@PathVariable Long id) {
        try {
            RepostDTO repost = repostService.getRepostById(id);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Lấy thông tin đăng lại thành công",
                    repost
            );
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Đã xảy ra lỗi khi lấy thông tin đăng lại: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getRepostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<RepostDTO> repostsPage = repostService.getRepostsByUserId(userId, pageable);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Lấy danh sách đăng lại thành công",
                    repostsPage
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Đã xảy ra lỗi khi lấy danh sách đăng lại: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> getRepostsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<RepostDTO> repostsPage = repostService.getRepostsByPost(postId, pageable);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Lấy danh sách đăng lại thành công",
                    repostsPage
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Đã xảy ra lỗi khi lấy danh sách đăng lại: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/check")
    public ResponseEntity<ApiResponse> checkUserReposted(
            @RequestParam Long postId) {
        try {
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            boolean hasReposted = repostService.hasUserReposted(userId, postId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Kiểm tra thành công",
                    hasReposted
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Đã xảy ra lỗi khi kiểm tra: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countReposts(
            @RequestParam Long postId) {
        try {
            long repostCount = repostService.countReposts(postId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đếm số lượt đăng lại thành công",
                    repostCount
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Đã xảy ra lỗi khi đếm số lượt đăng lại: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
