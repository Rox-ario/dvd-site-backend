package it.progetto.backend.DTOs;
import lombok.Data;

import java.util.Set;

@Data
public class ClienteProfileResponseDTO
{
    //no ID del database
    //no Password
    private String nome;
    private String cognome;
    private String email;
    private Set<String> filmPreferiti;
    private String ruolo;
}
