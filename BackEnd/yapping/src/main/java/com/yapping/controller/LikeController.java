package com.yapping.controller;

import com.yapping.dto.like.CreateLikeDTO;
import com.yapping.dto.like.LikeDTO;
import com.yapping.entity.Like.TargetType;
import com.yapping.service.LikeService;
import com.yapping.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "http://localhost:5173")
public class LikeController {

    @Autowired
    private LikeService likeService;
    
    // Tạo lượt thích mới
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createLike(@Valid @RequestBody CreateLikeDTO createLikeDTO) {
        try {
            LikeDTO createdLike = likeService.createLike(createLikeDTO);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo lượt thích thành công",
                    createdLike
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
    
    // Xóa lượt thích
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{likeId}")
    public ResponseEntity<ApiResponse> deleteLike(@PathVariable Long likeId) {
        try {
            likeService.deleteLike(likeId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa lượt thích thành công",
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
    
    // Xóa lượt thích của người dùng đối với một đối tượng
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/user/{userId}/target")
    public ResponseEntity<ApiResponse> deleteLikeByUserAndTarget(
            @PathVariable Long userId,
            @RequestParam TargetType targetType,
            @RequestParam Long targetId) {
        try {
            likeService.deleteLikeByUserAndTarget(userId, targetType, targetId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa lượt thích thành công",
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
    
    // Lấy tất cả lượt thích của một người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getLikesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<LikeDTO> likePage = likeService.getLikesByUserId(userId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách lượt thích của người dùng thành công",
                likePage
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tất cả lượt thích cho một bài đăng
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> getLikesByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<LikeDTO> likePage = likeService.getLikesByPost(postId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách lượt thích của bài đăng thành công",
                likePage
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tất cả lượt thích cho một bình luận
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse> getLikesByComment(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<LikeDTO> likePage = likeService.getLikesByComment(commentId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách lượt thích của bình luận thành công",
                likePage
        );
        return ResponseEntity.ok(response);
    }
    
    // Kiểm tra xem người dùng đã thích một đối tượng chưa
    @GetMapping("/check")
    public ResponseEntity<ApiResponse> hasUserLiked(
            @RequestParam Long userId,
            @RequestParam TargetType targetType,
            @RequestParam Long targetId) {
        
        boolean hasLiked = likeService.hasUserLiked(userId, targetType, targetId);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã kiểm tra lượt thích thành công",
                hasLiked
        );
        return ResponseEntity.ok(response);
    }
    
    // Đếm số lượt thích của một đối tượng
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countLikes(
            @RequestParam TargetType targetType,
            @RequestParam Long targetId) {
        
        long likeCount = likeService.countLikes(targetType, targetId);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã đếm số lượt thích thành công",
                likeCount
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy tất cả lượt thích trong một khoảng thời gian
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse> getLikesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<LikeDTO> likePage = likeService.getLikesByDateRange(startInstant, endInstant, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách lượt thích theo khoảng thời gian thành công",
                likePage
        );
        return ResponseEntity.ok(response);
    }
    
    // Lấy lượt thích của người dùng trong một khoảng thời gian
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<ApiResponse> getLikesByUserIdAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<LikeDTO> likePage = likeService.getLikesByUserIdAndDateRange(userId, startInstant, endInstant, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách lượt thích của người dùng theo khoảng thời gian thành công",
                likePage
        );
        return ResponseEntity.ok(response);
    }
    
    // Tìm kiếm lượt thích theo nhiều điều kiện
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchLikes(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) TargetType targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<LikeDTO> likePage = likeService.searchLikes(userId, targetType, targetId, startInstant, endInstant, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã tìm kiếm lượt thích thành công",
                likePage
        );
        return ResponseEntity.ok(response);
    }
    
    // Thích một bài đăng
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> likePost(@PathVariable Long postId) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            LikeDTO createdLike = likeService.likePost(userId, postId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã thích bài đăng thành công",
                    createdLike
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
    
    // Thích một bình luận
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse> likeComment(@PathVariable Long commentId) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            LikeDTO createdLike = likeService.likeComment(userId, commentId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã thích bình luận thành công",
                    createdLike
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
