package com.yapping.service;

import com.yapping.dto.UserDTO;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO findUserWithRoles(Long userId);
    List<UserDTO> findAllUsers();
    UserDTO updateUser(Long userId, UserDTO userDTO);
    UserDTO deleteUser(Long userId);
    String assignRoleToUser(Long userId, String roleName); // gán role tai khoan
    String removeRoleFromUser(Long userId, String roleName);// gỡ role tài khoan
}