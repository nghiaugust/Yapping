package com.yapping.controller;

import com.yapping.dto.ApiResponse;
import com.yapping.dto.UserDTO;
import com.yapping.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {
        UserDTO user = userService.findUserWithRoles(id);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                true,
                "Đã lấy thông tin người dùng có ID " + id + " thành công",
                user
        );
        return ResponseEntity.ok(response);
    }

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