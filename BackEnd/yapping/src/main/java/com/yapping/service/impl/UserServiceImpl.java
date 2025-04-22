package com.yapping.service.impl;

import com.yapping.dto.UserDTO;
import com.yapping.entity.Role;
import com.yapping.entity.User;
import com.yapping.entity.Userrole;
import com.yapping.entity.UserroleId;
import com.yapping.mapper.UserMapper;
import com.yapping.repository.RoleRepository;
import com.yapping.repository.UserRepository;
import com.yapping.repository.UserroleRepository;
import com.yapping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserroleRepository userroleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO, Set<String> roleNames) {
        // Kiểm tra username và email duy nhất
        userRepository.findByUsername(userDTO.getUsername())
                .ifPresent(u -> { throw new RuntimeException("Username already exists"); });
        userRepository.findByEmail(userDTO.getEmail())
                .ifPresent(u -> { throw new RuntimeException("Email already exists"); });

        // Chuyển DTO thành entity
        User user = userMapper.toEntity(userDTO);
        if (userDTO.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setIsVerified(false);
        user.setStatus(User.Status.PENDING_VERIFICATION);

        // Lưu user
        user = userRepository.save(user);

        // Gán vai trò
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                assignRoleToUser(user.getId(), roleName);
            }
        }

        // Trả về DTO
        return userMapper.toDTO(userRepository.findById(user.getId()).orElseThrow());
    }

    @Override
    public UserDTO findUserWithRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra username và email duy nhất (nếu thay đổi)
        if (!user.getUsername().equals(userDTO.getUsername())) {
            userRepository.findByUsername(userDTO.getUsername())
                    .ifPresent(u -> { throw new RuntimeException("Username already exists"); });
        }
        if (!user.getEmail().equals(userDTO.getEmail())) {
            userRepository.findByEmail(userDTO.getEmail())
                    .ifPresent(u -> { throw new RuntimeException("Email already exists"); });
        }

        // Cập nhật thông tin user
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setFullName(userDTO.getFullName());
        user.setBio(userDTO.getBio());
        user.setProfilePicture(userDTO.getProfilePicture());
        user.setIsVerified(userDTO.getIsVerified() != null ? userDTO.getIsVerified() : user.getIsVerified());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : user.getStatus());

        // Cập nhật vai trò (xóa cũ, thêm mới)
        if (roleNames != null) {
            userroleRepository.deleteAll(userroleRepository.findByUserId(userId));
            for (String roleName : roleNames) {
                assignRoleToUser(userId, roleName);
            }
        }

        // Lưu user
        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Xóa các Userrole liên quan (do cascade trong entity)
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Kiểm tra xem vai trò đã được gán chưa
        UserroleId userroleId = new UserroleId(userId, role.getId());
        if (!userroleRepository.existsById(userroleId)) {
            Userrole userrole = new Userrole();
            userrole.setUser(user);
            userrole.setRole(role);
            userrole.setId(userroleId);
            userroleRepository.save(userrole);
        }
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserroleId userroleId = new UserroleId(userId, role.getId());
        userroleRepository.deleteById(userroleId);
    }
}