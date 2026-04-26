package it.progetto.backend.DTOs;
import java.util.List;

import lombok.Data;

@Data // Genera in automatico Getter, Setter, toString, equals e hashCode
public class CreaOrdineRequest
{
    private List<RigaOrdineDTO> articoli;
    private String indirizzoSpedizione;
}
