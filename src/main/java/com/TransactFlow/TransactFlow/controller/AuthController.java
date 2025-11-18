package com.TransactFlow.TransactFlow.controller;

import com.TransactFlow.TransactFlow.dtos.request.LoginRequestDto;
import com.TransactFlow.TransactFlow.dtos.request.RegisterRequestDto;
import com.TransactFlow.TransactFlow.dtos.response.AuthResponseDto;
import com.TransactFlow.TransactFlow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto registerRequest){
        AuthResponseDto response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }





}
