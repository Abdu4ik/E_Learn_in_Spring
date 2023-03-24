package com.tafakkoor.e_learn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public record UpdateUserDTO (String id, String username, String email, String firstName, String lastName, LocalDate birthDate) {
}
