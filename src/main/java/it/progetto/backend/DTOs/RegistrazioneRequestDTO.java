package it.progetto.backend.DTOs;

import lombok.Data;

@Data
public class RegistrazioneRequestDTO
{
    private String nome;
    private String cognome;
    private String email;
    private String password;
}
