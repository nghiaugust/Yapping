package com.yapping.config;

import com.yapping.entity.User;
import com.yapping.entity.Userrole;
import com.yapping.repository.UserRepository;
import com.yapping.repository.UserroleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserroleRepository userroleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Kiểm tra trạng thái của người dùng
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new UsernameNotFoundException("User account is not active. Current status: " + user.getStatus());
        }
        // Lấy danh sách vai trò từ Userrole
        List<Userrole> userRoles = userroleRepository.findByUserId(user.getId());
        List<GrantedAuthority> authorities = userRoles.stream()
                .map(userrole -> new SimpleGrantedAuthority("ROLE_" + userrole.getRole().getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities // danh sách quyền
        ); // cấu hình thông tin trả về cho userdetail
    }
}