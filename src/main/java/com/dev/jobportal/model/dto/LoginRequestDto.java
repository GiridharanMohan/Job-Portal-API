package com.dev.jobportal.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestDto {

    @Email(message = "Not a valid email")
    @NotBlank(message = "E-mail is empty")
    String email;

    @NotBlank(message = "Password is empty")
    @Size(min = 8, max = 16)
    String password;
}
