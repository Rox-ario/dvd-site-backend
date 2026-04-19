package it.progetto.backend.DTOs;

import lombok.Data;

@Data
public class AggiornaAnagraficaRequestDTO
{
    private String nome;
    private String cognome;
    private String email;
}
