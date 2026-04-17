package com.teach.studentadmin.controller;

import com.teach.studentadmin.dto.ApiResponse;
import com.teach.studentadmin.dto.LoginRequest;
import com.teach.studentadmin.dto.LoginResponse;
import com.teach.studentadmin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    @GetMapping("/current")
    public ApiResponse<Object> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String username = token.replace("Bearer ", "");
            var user = authService.getCurrentUser(username);
            return ApiResponse.success(java.util.Map.of(
                    "username", user.getUsername(),
                    "name", user.getName(),
                    "role", user.getRole()
            ));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
