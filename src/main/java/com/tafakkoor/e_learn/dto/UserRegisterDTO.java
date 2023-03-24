package com.tafakkoor.e_learn.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class UserRegisterDTO {
    private String username = "";
    private String password;
    private String confirmPassword;
    private String email = "";
    private String firstname;
    private String lastname;
}
