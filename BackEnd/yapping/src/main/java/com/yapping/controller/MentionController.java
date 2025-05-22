package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.mention.CreateMentionDTO;
import com.yapping.dto.mention.MentionDTO;
import com.yapping.service.MentionService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentions")
@CrossOrigin(origins = "http://localhost:5173")
public class MentionController {

    @Autowired
    private MentionService mentionService;

    // Tạo đề cập mới
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse> createMention(@Valid @RequestBody CreateMentionDTO createMentionDTO) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            // Đặt người đề cập là người dùng hiện tại
            createMentionDTO.setMentioningUserId(userId);

            MentionDTO createdMention = mentionService.createMention(createMentionDTO);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo đề cập thành công",
                    createdMention
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

    // Tạo đề cập từ văn bản
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/from-text")
    public ResponseEntity<ApiResponse> createMentionsFromText(
            @RequestParam String text,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long commentId) {
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");

            if (postId == null && commentId == null) {
                throw new IllegalArgumentException("Phải chỉ định ít nhất một bài đăng hoặc bình luận");
            }

            List<MentionDTO> createdMentions = mentionService.createMentionsFromText(text, userId, postId, commentId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.CREATED.value(),
                    true,
                    "Đã tạo " + createdMentions.size() + " đề cập thành công",
                    createdMentions
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

    // Xóa đề cập
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{mentionId}")
    public ResponseEntity<ApiResponse> deleteMention(@PathVariable Long mentionId) {
        try {
            mentionService.deleteMention(mentionId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã xóa đề cập thành công",
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

    // Lấy đề cập theo ID
    @GetMapping("/{mentionId}")
    public ResponseEntity<ApiResponse> getMentionById(@PathVariable Long mentionId) {
        try {
            MentionDTO mention = mentionService.getMentionById(mentionId);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy đề cập thành công",
                    mention
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

    // Lấy tất cả đề cập của một người dùng (người được đề cập)
    @GetMapping("/mentioned-user/{userId}")
    public ResponseEntity<ApiResponse> getMentionsByMentionedUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<MentionDTO> mentionPage = mentionService.getMentionsByMentionedUserId(userId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách đề cập thành công",
                mentionPage
        );
        return ResponseEntity.ok(response);
    }

    // Lấy tất cả đề cập do một người dùng tạo ra (người đề cập)
    @GetMapping("/mentioning-user/{userId}")
    public ResponseEntity<ApiResponse> getMentionsByMentioningUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<MentionDTO> mentionPage = mentionService.getMentionsByMentioningUserId(userId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách đề cập thành công",
                mentionPage
        );
        return ResponseEntity.ok(response);
    }

    // Lấy tất cả đề cập trong một bài đăng
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse> getMentionsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<MentionDTO> mentionPage = mentionService.getMentionsByPostId(postId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách đề cập trong bài đăng thành công",
                mentionPage
        );
        return ResponseEntity.ok(response);
    }

    // Lấy tất cả đề cập trong một bình luận
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse> getMentionsByCommentId(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<MentionDTO> mentionPage = mentionService.getMentionsByCommentId(commentId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách đề cập trong bình luận thành công",
                mentionPage
        );
        return ResponseEntity.ok(response);
    }

    // Lấy các đề cập chưa đọc của người dùng
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse> getUnreadMentions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        try {
            // Lấy userId từ authentication
            Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
            Map<String, Object> claims = (Map<String, Object>) details.get("claims");
            Long userId = (Long) claims.get("userId");
            
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            
            Page<MentionDTO> mentionPage = mentionService.getUnreadMentionsByUserId(userId, pageable);
            
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã lấy danh sách đề cập chưa đọc thành công",
                    mentionPage
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Đếm số lượng đề cập của một người dùng
    @GetMapping("/count/{userId}")
    public ResponseEntity<ApiResponse> countMentionsByMentionedUserId(@PathVariable Long userId) {
        long mentionCount = mentionService.countMentionsByMentionedUserId(userId);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã đếm số đề cập thành công",
                mentionCount
        );
        return ResponseEntity.ok(response);
    }

    // Tìm kiếm đề cập
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchMentions(
            @RequestParam(required = false) Long mentionedUserId,
            @RequestParam(required = false) Long mentioningUserId,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<MentionDTO> mentionPage = mentionService.searchMentions(
                mentionedUserId, mentioningUserId, postId, commentId, pageable);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã tìm kiếm đề cập thành công",
                mentionPage
        );
        return ResponseEntity.ok(response);
    }
}
