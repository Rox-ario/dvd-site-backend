package it.progetto.backend.DTOs;
import java.util.List;

import lombok.Data;

@Data
public class CreaOrdineRequest
{
    private List<RigaOrdineDTO> articoli;
    private String indirizzoSpedizione;
}
