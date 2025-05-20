package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.user.PatchUserDTO;
import com.yapping.dto.user.UserDTO;
import com.yapping.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

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

    @PreAuthorize("hasRole('ADMIN') or authentication.details['claims']['userId'] == #id")
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
}