package it.progetto.backend.DTOs;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FilmResponseDTO
{
    private Long idFilm;
    private String titolo;
    private String trama;
    private BigDecimal prezzo;
    private List<String> genere;
    private List<String> registi;
    private List<String> attori;
    private Integer anno;
    private Integer durataMinuti;
    private Integer stock; //per quantità disponibile
    private boolean isAttivo; // per dire al cliente se sia in vendita ancora o meno
}
