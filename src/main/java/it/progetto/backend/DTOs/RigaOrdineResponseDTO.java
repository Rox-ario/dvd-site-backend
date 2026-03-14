package it.progetto.backend.DTOs;

import lombok.Data;
import java.math.BigDecimal;

@Data
//uso per risposta alla richiesta dello storico degli ordini
public class RigaOrdineResponseDTO
{
    private String titoloFilm;
    private Integer quantita;
    private BigDecimal prezzoAcquisto;
}
