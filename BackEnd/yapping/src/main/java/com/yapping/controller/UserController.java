package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.follow.FollowDTO;
import com.yapping.dto.user.ChangePasswordDTO;
import com.yapping.dto.user.PatchUserDTO;
import com.yapping.dto.user.UserDTO;
import com.yapping.service.UserService;
import com.yapping.service.FollowService;
import com.yapping.service.FileStorageService;
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
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private FollowService followService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        ApiResponse response = new ApiResponse(
                HttpStatus.CREATED.value(),
                true,
                "Đã tạo người dùng thành công",
                createdUser
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //@PreAuthorize("hasRole('ADMIN') or authentication.details['claims']['userId'] == #id")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {
        UserDTO user = userService.findOneUser(id);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy thông tin người dùng có ID " + id + " thành công",
                user
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser() {
        // Lấy userId từ authentication.details.claims
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");

        // Gọi findOneUser với userId
        UserDTO user = userService.findOneUser(userId);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy thông tin người dùng hiện tại thành công",
                user
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách người dùng thành công",
                users
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN') or authentication.principal.username == #userDTO.username")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã cập nhật người dùng có ID " + id + " thành công",
                updatedUser
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<ApiResponse> patchUser(@PathVariable Long id, @Valid @RequestBody PatchUserDTO patchUserDTO) {
        UserDTO patchUser = userService.patchUser(id, patchUserDTO);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã cập nhật người dùng có ID " + id + " thành công",
                patchUser
        );
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse> patchCurrentUser(@Valid @RequestBody PatchUserDTO patchUserDTO) {
        // Lấy userId từ authentication.details.claims
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");
        
        UserDTO patchedUser = userService.patchForUser(userId, patchUserDTO);
        
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã cập nhật thông tin người dùng hiện tại thành công",
                patchedUser
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/me/profile-picture")
    public ResponseEntity<ApiResponse> patchProfilePicture(@RequestParam("file") MultipartFile file) {
        // Lấy userId từ authentication
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long userId = (Long) claims.get("userId");

        // Kiểm tra loại file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    "Chỉ chấp nhận file ảnh (JPEG, PNG, GIF)",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            // Lưu file và lấy đường dẫn
            String fileUrl = fileStorageService.storeFile(file);

            // Tạo PatchUserDTO với đường dẫn ảnh mới
            PatchUserDTO patchUserDTO = new PatchUserDTO();
            patchUserDTO.setProfilePicture(fileUrl);

            // Cập nhật thông tin user
            UserDTO patchedUser = userService.patchProfilePicture(userId, patchUserDTO);

            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    true,
                    "Đã cập nhật ảnh đại diện thành công",
                    patchedUser
            );
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    false,
                    "Không thể lưu ảnh đại diện: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        UserDTO deletedUser = userService.deleteUser(id);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã xóa người dùng có ID " + id + " thành công",
                deletedUser
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse> assignRole(@PathVariable Long id, @RequestParam String role) {
        String thongbao = userService.assignRoleToUser(id, role);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                thongbao,
                null
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<ApiResponse> removeRole(@PathVariable Long id, @PathVariable String role) {
        String thongbao = userService.removeRoleFromUser(id, role);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                thongbao,
                null
        );
        return ResponseEntity.ok(response);
    }

    // Đổi mật khẩu
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @PathVariable Long id, 
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đổi mật khẩu thành công",
                null
        );
        return ResponseEntity.ok(response);
    }
    
    //follow===================================================================
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/{followedId}/follow")
    public ResponseEntity<ApiResponse> followUser(@PathVariable Long followedId) {
        // Lấy userId từ authentication
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long followerId = (Long) claims.get("userId");

        // Kiểm tra không thể tự follow chính mình
        if (followerId.equals(followedId)) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    false,
                    "Không thể follow chính mình",
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        FollowDTO followDTO = followService.followUser(followerId, followedId);

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã follow người dùng thành công",
                followDTO
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{followedId}/unfollow")
    public ResponseEntity<ApiResponse> unfollowUser(@PathVariable Long followedId) {
        // Lấy userId từ authentication
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String, Object> claims = (Map<String, Object>) details.get("claims");
        Long followerId = (Long) claims.get("userId");

        followService.unfollowUser(followerId, followedId);

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã unfollow người dùng thành công",
                null
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse> getFollowers(@PathVariable Long userId) {
        List<FollowDTO> followers = followService.getFollowers(userId);

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách người theo dõi thành công",
                followers
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse> getFollowing(@PathVariable Long userId) {
        List<FollowDTO> following = followService.getFollowing(userId);

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy danh sách đang theo dõi thành công",
                following
        );
        return ResponseEntity.ok(response);
    }
}