package com.TransactFlow.TransactFlow.dtos.request;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
}
