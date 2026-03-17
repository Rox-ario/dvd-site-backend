package it.progetto.backend.DTOs;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreaFilmRequestDTO
{
    private String titolo;
    private String trama;
    private BigDecimal prezzo;
    private List<Long> idGeneri;
    private List<Long> idRegisti;
    private List<Long> idAttori;
    private Integer anno;
    private Integer durataMinuti;
    private Integer stock;
}
