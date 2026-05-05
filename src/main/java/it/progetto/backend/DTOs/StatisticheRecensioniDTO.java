package it.progetto.backend.DTOs;

import lombok.Data;
import java.util.Map;

@Data
public class StatisticheRecensioniDTO {
    private Double mediaStelle;
    private Integer totaleRecensioni;
    private Map<Integer, Long> distribuzione;
}
