package com.yapping.service.impl;

import com.yapping.dto.user.ChangePasswordDTO;
import com.yapping.dto.user.PatchUserDTO;
import com.yapping.dto.user.RoleDTO;
import com.yapping.dto.user.UserDTO;
import com.yapping.entity.Role;
import com.yapping.entity.User;
import com.yapping.entity.Userrole;
import com.yapping.entity.UserroleId;
import com.yapping.repository.RoleRepository;
import com.yapping.repository.UserRepository;
import com.yapping.repository.UserroleRepository;
import com.yapping.service.FileStorageService;
import com.yapping.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional // dam bao tinh nhat quan, nếu tất cả thành công được commit, nếu 1 thao tác thất bại rollback hủy bỏ tất cả thay đổi, ở đây có 2 thao tác là lưu và gán vai trò roll
    public UserDTO createUser(UserDTO userDTO) {
        // Kiểm tra username và email duy nhất
        userRepository.findByUsername(userDTO.getUsername())
                .ifPresent(u -> { throw new RuntimeException("Username already exists"); });
        userRepository.findByEmail(userDTO.getEmail())
                .ifPresent(u -> { throw new RuntimeException("Email already exists"); });

        // Chuyển DTO thành entity
        User user = new User();
        BeanUtils.copyProperties(userDTO, user, "id", "password", "roles", "createdAt", "updatedAt", "isVerified", "status");
        //dang sau la danh sach cac thuoc tinh se bo qua khi tao o đay userDTO la nguon, user la dich
        if (userDTO.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setIsVerified(false);// mac dinh chua co tich xanh
        user.setStatus(User.Status.PENDING_VERIFICATION);// chờ xác nhận từ mail

        // Lưu user
        user = userRepository.save(user);

        // Gán vai trò từ UserDTO
        Set<RoleDTO> roles = userDTO.getRoles();
        if (roles != null && !roles.isEmpty()) {
            for (RoleDTO roleDTO : roles) {
                assignRoleToUser(user.getId(), roleDTO.getName());
            }
        }

        // Chuyển entity thành DTO
        return convertToDTO(user);
    }

    @Override
    public UserDTO findOneUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(user -> {
                    try {
                        return convertToDTO(user);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to convert user with ID " + user.getId() + " to UserDTO. Cause: " + e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
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
        BeanUtils.copyProperties(userDTO, user, "id", "password", "roles", "createdAt", "updatedAt");
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setIsVerified(userDTO.getIsVerified() != null ? userDTO.getIsVerified() : user.getIsVerified());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : user.getStatus());

        // Cập nhật vai trò từ UserDTO
        Set<RoleDTO> roles = userDTO.getRoles();
        if (roles != null) {
            userroleRepository.deleteAll(userroleRepository.findByUserId(userId));
            for (RoleDTO roleDTO : roles) {
                assignRoleToUser(userId, roleDTO.getName());
            }
        }

        // Lưu user
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO patchUser(Long userId, PatchUserDTO patchUserDTO) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra username nếu được cung cấp
        if (patchUserDTO.getUsername() != null && !user.getUsername().equals(patchUserDTO.getUsername())) {
            userRepository.findByUsername(patchUserDTO.getUsername())
                    .ifPresent(u -> { throw new RuntimeException("Username already exists"); });
            user.setUsername(patchUserDTO.getUsername());
        }

        // Kiểm tra email nếu được cung cấp
        if (patchUserDTO.getEmail() != null && !user.getEmail().equals(patchUserDTO.getEmail())) {
            userRepository.findByEmail(patchUserDTO.getEmail())
                    .ifPresent(u -> { throw new RuntimeException("Email already exists"); });
            user.setEmail(patchUserDTO.getEmail());
        }


        // Cập nhật các thuộc tính khác nếu được cung cấp
        if (patchUserDTO.getFullName() != null) {
            user.setFullName(patchUserDTO.getFullName());
        }
        if (patchUserDTO.getBio() != null) {
            user.setBio(patchUserDTO.getBio());
        }
        if (patchUserDTO.getProfilePicture() != null) {
            user.setProfilePicture(patchUserDTO.getProfilePicture());
        }
        if (patchUserDTO.getIsVerified() != null) {
            user.setIsVerified(patchUserDTO.getIsVerified());
        }
        if (patchUserDTO.getStatus() != null) {
            user.setStatus(patchUserDTO.getStatus());
        }

        // Cập nhật vai trò nếu được cung cấp
        if (patchUserDTO.getRoles() != null) {
            userroleRepository.deleteAll(userroleRepository.findByUserId(userId));
            for (RoleDTO roleDTO : patchUserDTO.getRoles()) {
                assignRoleToUser(userId, roleDTO.getName());
            }
        }

        // Lưu user
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO patchForUser(Long userId, PatchUserDTO patchUserDTO) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Chỉ cập nhật các thuộc tính được phép
        if (patchUserDTO.getFullName() != null) {
            user.setFullName(patchUserDTO.getFullName());
        }
        if (patchUserDTO.getBio() != null) {
            user.setBio(patchUserDTO.getBio());
        }

        // Lưu user
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO patchProfilePicture(Long userId, PatchUserDTO patchUserDTO) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (patchUserDTO.getProfilePicture() != null) {
            // Xóa ảnh cũ nếu có
            try {
                if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                    fileStorageService.deleteFile(user.getProfilePicture());
                }
            } catch (IOException e) {
                // Log lỗi nhưng không dừng quá trình
                System.err.println("Không thể xóa ảnh cũ: " + e.getMessage());
            }

            // Cập nhật đường dẫn ảnh mới
            user.setProfilePicture(patchUserDTO.getProfilePicture());
        }

        // Lưu user
        user = userRepository.save(user);
        return convertToDTO(user);
    }
    
    @Override
    @Transactional
    public UserDTO deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Xóa tất cả Userrole liên quan
        userroleRepository.deleteByUserId(userId);
        userRepository.delete(user);
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public String assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserroleId userroleId = new UserroleId(userId, role.getId());
        if (!userroleRepository.existsById(userroleId)) {
            Userrole userrole = new Userrole();
            userrole.setUser(user);
            userrole.setRole(role);
            userrole.setId(userroleId);
            userroleRepository.save(userrole);
            return "Đã gán vai trò " + roleName + " cho người dùng có ID " + userId + " thành công";
        }
        else
            throw new RuntimeException("Role " + roleName + " is already assigned to user with ID " + userId);
    }

    @Override
    @Transactional
    public String removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserroleId userroleId = new UserroleId(userId, role.getId());
        if (!userroleRepository.existsById(userroleId)){
            throw new RuntimeException("Role " + roleName + " khong ton tai voi user co ID " + userId);
        }else {
            userroleRepository.deleteById(userroleId);
            return "Đã xoá vai trò " + roleName + " cho người dùng có ID " + userId + " thành công";
        }
    }

    private UserDTO convertToDTO(User user) {
        if (user == null) {
            throw new RuntimeException("User is null");
        }

        // Tạo UserDTO và ánh xạ thủ công các thuộc tính từ User
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO, "passwordHash");

        // Lấy danh sách Userrole từ repository
        List<Userrole> userroles = userroleRepository.findByUserId(user.getId());
        if (userroles == null) {
            throw new RuntimeException("Userroles not found for user ID " + user.getId());
        }

        // Chuyển đổi danh sách Userrole thành Set<RoleDTO>
        Set<RoleDTO> roleDTOs = userroles.stream()
                .map(userrole -> {
                    Role role = userrole.getRole();
                    if (role == null) {
                        throw new RuntimeException("Role is null for userrole with user ID " + user.getId());
                    }
                    if (role.getName() == null) {
                        throw new RuntimeException("Role name is null for role ID " + role.getId());
                    }
                    // Ánh xạ thủ công từ Role sang RoleDTO
                    RoleDTO roleDTO = new RoleDTO();
                    roleDTO.setId(role.getId());
                    roleDTO.setName(role.getName());
                    return roleDTO;
                })
                .collect(Collectors.toSet());

        // Gán danh sách RoleDTO vào UserDTO
        userDTO.setRoles(roleDTOs);
        return userDTO;
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        // Tìm user theo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        
        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        
        // Kiểm tra mật khẩu mới không được giống mật khẩu cũ
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu mới phải khác mật khẩu hiện tại");
        }
        
        // Mã hóa mật khẩu mới
        String encodedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        
        // Cập nhật mật khẩu
        user.setPasswordHash(encodedPassword);

        // Lưu user
        userRepository.save(user);
    }
}