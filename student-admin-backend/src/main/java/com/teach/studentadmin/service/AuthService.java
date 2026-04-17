package com.teach.studentadmin.service;

import com.teach.studentadmin.dto.LoginRequest;
import com.teach.studentadmin.dto.LoginResponse;
import com.teach.studentadmin.entity.User;
import com.teach.studentadmin.repository.UserRepository;
import com.teach.studentadmin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("用户已被禁用");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());

        return new LoginResponse(token, user.getUsername(), user.getRole(), user.getName());
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
