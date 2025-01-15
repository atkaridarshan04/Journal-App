package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @NotEmpty
    @Schema(description = "User's Usernanme")
    private String username;

    @NotEmpty
    @Size(min = 6)
    @Schema(description = "Not be less than 6 characters")
    private String password;

    private String city;

    @Email
    private String email;

    private boolean sentimentAnalysis;

    private List<String> roles;
}
