package it.progetto.backend.DTOs;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdineResponseDTO
{
    private Long numeroOrdine; // Mappiamo l'ID qui
    private LocalDateTime dataAcquisto;
    private BigDecimal totale;
    private String stato; // Lo trasformiamo da Enum a stringa semplice

    // Inseriamo la lista dei DTO figli, NON delle entità! Questo spezza il loop infinito.
    private List<RigaOrdineResponseDTO> righe;
}
