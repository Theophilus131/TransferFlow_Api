package com.TransactFlow.TransactFlow.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String message;
}
