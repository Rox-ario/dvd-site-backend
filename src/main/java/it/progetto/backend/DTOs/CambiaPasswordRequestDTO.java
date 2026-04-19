package it.progetto.backend.DTOs;

import lombok.Data;

@Data
public class CambiaPasswordRequestDTO
{
    private String vecchiaPassword;
    private String nuovaPassword;
    private String confermaNuovaPassword;
}
