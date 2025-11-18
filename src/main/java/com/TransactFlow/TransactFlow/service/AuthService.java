package com.TransactFlow.TransactFlow.service;


import com.TransactFlow.TransactFlow.data.model.User;
import com.TransactFlow.TransactFlow.dtos.request.LoginRequestDto;
import com.TransactFlow.TransactFlow.dtos.request.RegisterRequestDto;
import com.TransactFlow.TransactFlow.dtos.response.AuthResponseDto;
import com.TransactFlow.TransactFlow.exceptions.InvalidCredentialsException;
import com.TransactFlow.TransactFlow.exceptions.UserAlreadyExistsException;
import com.TransactFlow.TransactFlow.exceptions.UserNotFoundException;
import com.TransactFlow.TransactFlow.mapper.UserMapper;
import com.TransactFlow.TransactFlow.repository.UserRepository;
import com.TransactFlow.TransactFlow.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;


    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        validateRegisterRequest(registerRequest);

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + registerRequest.getEmail()+ "already exists");
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        user.setBalance(new BigDecimal("00.00"));

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail());

        return userMapper.toAuthResponseDto(savedUser, token, "User registered successfully");

    }

    public AuthResponseDto login(LoginRequestDto loginRequest) {

        validateLoginRequest(loginRequest);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email " + loginRequest.getEmail() ));

        if(!user.isActive()){
            throw new InvalidCredentialsException("Invalid credentials");

        }

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid credentials");
        }

        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return userMapper.toAuthResponseDto(user, token, "User logged in successfully");


    }

    private void validateRegisterRequest(RegisterRequestDto request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }


    private void validateLoginRequest(LoginRequestDto request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}


