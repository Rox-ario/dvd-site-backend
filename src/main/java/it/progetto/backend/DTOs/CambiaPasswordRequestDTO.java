package it.progetto.backend.DTOs;

import lombok.Data;

@Data
public class CambiaPasswordRequestDTO
{
    private String vecchiaPassword; //obbligatorio per verificare l'identità
    private String nuovaPassword;
    private String confermaNuovaPassword; //anti-errore di battitura
}
