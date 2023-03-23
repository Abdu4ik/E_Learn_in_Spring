package com.tafakkoor.e_learn.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class UserRegisterDTO {
    private String username = "";
    private String password;
    private String confirmPassword;
    private String email = "";


    public String getUsername() {
        return username.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getEmail() {
        return email.toLowerCase();
    }
}
