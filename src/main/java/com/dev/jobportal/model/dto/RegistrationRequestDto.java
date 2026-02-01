package com.dev.jobportal.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequestDto {
    @NotBlank(message = "Username is empty")
    String username;

    @Email(message = "Not a valid email address")
    @NotBlank(message = "E-mail is empty")
    String email;

    @NotBlank(message = "Password is empty")
    @Size(min = 8, max = 16, message = "Password length should be between 8 - 16 characters")
    String password;
}
