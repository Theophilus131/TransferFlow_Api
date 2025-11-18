package com.TransactFlow.TransactFlow.mapper;


import com.TransactFlow.TransactFlow.data.model.User;
import com.TransactFlow.TransactFlow.dtos.request.RegisterRequestDto;
import com.TransactFlow.TransactFlow.dtos.response.AuthResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User toEntity(RegisterRequestDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());
        user.setActive(true);
        return user;
    }


    public AuthResponseDto toAuthResponseDto(User user, String token, String message){
        AuthResponseDto responseDto = new AuthResponseDto();

        responseDto.setToken(token);
        responseDto.setEmail(user.getEmail());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        responseDto.setMessage(message);

        return responseDto;
    }
}
