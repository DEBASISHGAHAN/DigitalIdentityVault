package com.project.digitalidentityvault.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {
    private String email;
    private String otp;
}
