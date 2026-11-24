package com.gulbahar.secure_message_api.controller;

import com.gulbahar.secure_message_api.dto.LoginRequest;
import com.gulbahar.secure_message_api.dto.RegisterRequest;
import com.gulbahar.secure_message_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful.");

    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
        String token= userService.login(request);
        return ResponseEntity.ok(token);
    }
}
