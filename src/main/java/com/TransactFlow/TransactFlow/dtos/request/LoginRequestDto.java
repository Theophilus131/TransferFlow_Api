package com.TransactFlow.TransactFlow.dtos.request;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
