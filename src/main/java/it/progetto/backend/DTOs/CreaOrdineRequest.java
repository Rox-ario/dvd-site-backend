package it.progetto.backend.DTOs;
import java.util.List;

import lombok.Data;
import java.util.List;

@Data // Genera in automatico Getter, Setter, toString, equals e hashCode
public class CreaOrdineRequest
{
    private Long idCliente;
    private List<RigaOrdineDTO> articoli;
    private String indirizzoSpedizione;
}
