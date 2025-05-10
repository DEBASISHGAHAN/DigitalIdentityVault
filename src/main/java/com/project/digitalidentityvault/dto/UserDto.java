package com.project.digitalidentityvault.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String email;
    private String password;
    private String otp;

    public UserDto(String mail, String password) {
    }
}
