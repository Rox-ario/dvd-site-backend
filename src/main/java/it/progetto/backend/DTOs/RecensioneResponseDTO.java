package it.progetto.backend.DTOs;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RecensioneResponseDTO {
    private Long id;
    private String emailCliente;
    private String nomeCliente;
    private Integer stelle;
    private String commento;
    private LocalDateTime data;
}
