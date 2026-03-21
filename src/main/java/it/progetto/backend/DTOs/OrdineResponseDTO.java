package it.progetto.backend.DTOs;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdineResponseDTO
{
    private Long numeroOrdine;
    private LocalDateTime dataAcquisto;
    private BigDecimal totale;
    private String stato;

    private List<RigaOrdineResponseDTO> righe;
}
