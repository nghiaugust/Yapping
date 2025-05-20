package com.yapping.dto.user;

import com.yapping.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PatchUserDTO {
    @Size(min = 3, max = 50)
    private String username;

    @Email
    private String email;

    @Size(min = 8, max = 255)
    private String password;

    @Size(max = 100)
    private String fullName;

    private String bio;

    @Size(max = 255)
    private String profilePicture;

    private Boolean isVerified;

    private User.Status status;

    private Set<RoleDTO> roles;
}