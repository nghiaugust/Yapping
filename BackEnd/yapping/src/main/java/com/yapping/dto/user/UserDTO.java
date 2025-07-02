package com.yapping.dto.user;

import com.yapping.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor // Tạo constructor không tham số
@AllArgsConstructor // Tạo constructor với tất cả tham số
public class UserDTO {

    private Long id;
    @NotNull
    @Size(min = 3, max = 50)
    private String username;
    @NotNull
    @Email
    private String email;
    @Size(min = 6, max = 255)
    private String password; // Chỉ dùng khi tạo/cập nhật user
    @Size(max = 100)
    private String fullName;
    private String bio; // giới thieu ban than
    @Size(max = 255)
    private String profilePicture;
    private Boolean isVerified; //tích xanh
    private User.Status status; // trạng thái tài khoản
    private Instant createdAt;
    private Instant updatedAt;
    private Set<RoleDTO> roles;
}