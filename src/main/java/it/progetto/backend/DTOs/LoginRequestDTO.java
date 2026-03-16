package it.progetto.backend.DTOs;

import lombok.Data;

@Data
public class LoginRequestDTO
{
    private String email;
    private String password;
}
