package it.progetto.backend.DTOs;

import lombok.Data;

@Data
public class ResetPasswordConOtpDTO
{
    private String email;
    private String otp;
    private String nuovaPassword;
}
