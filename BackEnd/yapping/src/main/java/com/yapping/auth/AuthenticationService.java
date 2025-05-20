package com.yapping.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapping.filter.JwtService;
import com.yapping.entity.User;
import com.yapping.repository.UserRepository;
import com.yapping.repository.UserroleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserroleRepository userroleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthenticationResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        tim người dùng, sau đó so sánh mật khẩu dùng passwordEncoder.matches
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

//      Xác minh thành công thì tải UserDetails lấy tt người dùng và roles
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Lấy danh sách role
        Set<String> roles = userroleRepository.findByUserId(user.getId()).stream()
                .map(userrole -> userrole.getRole().getName())
                .collect(Collectors.toSet());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .roles(roles)
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String refreshToken = authHeader.substring(7);
        final String username = jwtService.extractUsername(refreshToken);
        final Long userId = jwtService.extractUserId(refreshToken); // Thêm userId

        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String accessToken = jwtService.generateToken(userDetails);
                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userId(userId)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
