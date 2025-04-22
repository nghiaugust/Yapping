package com.yapping.service;

import com.yapping.dto.UserDTO;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDTO createUser(UserDTO userDTO, Set<String> roleNames);
    UserDTO findUserWithRoles(Long userId);
    List<UserDTO> findAllUsers();
    UserDTO updateUser(Long userId, UserDTO userDTO, Set<String> roleNames);
    void deleteUser(Long userId);
    void assignRoleToUser(Long userId, String roleName);
    void removeRoleFromUser(Long userId, String roleName);
}