package com.yapping.service;

import com.yapping.dto.user.ChangePasswordDTO;
import com.yapping.dto.user.PatchUserDTO;
import com.yapping.dto.user.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO findOneUser(Long userId);
    List<UserDTO> findAllUsers();
    UserDTO updateUser(Long userId, UserDTO userDTO);
    UserDTO patchUser(Long userId, PatchUserDTO patchUserDTO);
    UserDTO patchForUser(Long userId, PatchUserDTO patchUserDTO);
    UserDTO patchProfilePicture(Long userId, PatchUserDTO patchUserDTO);
    UserDTO deleteUser(Long userId);
    String assignRoleToUser(Long userId, String roleName); // gán role tai khoan
    String removeRoleFromUser(Long userId, String roleName);// gỡ role tài khoan
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO); // đổi mật khẩu
}