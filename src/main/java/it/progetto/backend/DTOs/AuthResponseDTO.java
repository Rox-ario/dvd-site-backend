package it.progetto.backend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO
{
    private String token;
    private String nome;
    private String ruolo;
}
