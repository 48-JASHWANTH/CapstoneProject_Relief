package org.hartford.relief.controller;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.JwtRequest;
import org.hartford.relief.dto.request.RegisterRequest;
import org.hartford.relief.dto.response.JwtResponse;
import org.hartford.relief.dto.response.UserResponse;
import org.hartford.relief.service.userService.RegisterService;
import org.hartford.relief.service.userService.UserLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final RegisterService registerService;
    private final UserLoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }
}