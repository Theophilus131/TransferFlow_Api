package com.TransactFlow.TransactFlow.data.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private BigDecimal balance;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private boolean active;


}
