package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.bookmark.BookmarkDTO;
import com.yapping.dto.bookmark.CreateBookmarkDTO;
import com.yapping.service.BookmarkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "http://localhost:5173")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;    
    
    // Lấy danh sách bookmark của một người dùng
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getBookmarksByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByUserId(userId, pageable);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Lấy danh sách bookmark thành công",
                    bookmarks
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException ? 
                    HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }    
    
    // Lấy danh sách bookmark của một bài đăng
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> getBookmarksByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BookmarkDTO> bookmarks = bookmarkService.getBookmarksByPostId(postId, pageable);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Lấy danh sách bookmark thành công",
                    bookmarks
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException ? 
                    HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }    
    
    // Lấy thông tin một bookmark cụ thể
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{userId}/{postId}")
    public ResponseEntity<ApiResponse> getBookmark(
            @PathVariable Long userId,
            @PathVariable Long postId) {
        try {
            BookmarkDTO bookmark = bookmarkService.getBookmark(userId, postId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Lấy thông tin bookmark thành công",
                    bookmark
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException ? 
                    HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }    
    
    // Kiểm tra một bài đăng đã được bookmark bởi người dùng hay chưa
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/check")
    public ResponseEntity<ApiResponse> checkBookmark(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        try {
            boolean isBookmarked = bookmarkService.isBookmarked(userId, postId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Kiểm tra bookmark thành công",
                    Map.of("bookmarked", isBookmarked)
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }

    // Lấy bookmark mới nhất của người dùng
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/latest/{userId}")
    public ResponseEntity<ApiResponse> getLatestBookmarks(@PathVariable Long userId) {
        try {
            List<BookmarkDTO> bookmarks = bookmarkService.getLatestBookmarks(userId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Lấy danh sách bookmark mới nhất thành công",
                    bookmarks
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException ? 
                    HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }    
    
    // Đếm số lượng bookmark của một bài đăng
    @GetMapping("/count/{postId}")
    public ResponseEntity<ApiResponse> countBookmarks(@PathVariable Long postId) {
        try {
            Long count = bookmarkService.countBookmarksByPostId(postId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đếm số lượng bookmark thành công",
                    Map.of("count", count)
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }

    // Tạo bookmark mới
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createBookmark(@Valid @RequestBody CreateBookmarkDTO createBookmarkDTO) {
        try {
            BookmarkDTO createdBookmark = bookmarkService.createBookmark(createBookmarkDTO);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Tạo bookmark thành công",
                    createdBookmark
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException ? 
                    HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
            
            ApiResponse response = new ApiResponse(
                    status.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(status).body(response);
        }
    }

    // Xóa bookmark
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{userId}/{postId}")
    public ResponseEntity<ApiResponse> deleteBookmark(
            @PathVariable Long userId,
            @PathVariable Long postId) {
        try {
            bookmarkService.deleteBookmark(userId, postId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Xóa bookmark thành công",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = e instanceof IllegalArgumentException ? 
                    HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
            
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
