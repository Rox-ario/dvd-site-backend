package it.progetto.backend.DTOs;

import lombok.Data;

@Data
public class RegistrazioneClienteRequestDTO {
    private String nome;
    private String cognome;
    private String email;
}
