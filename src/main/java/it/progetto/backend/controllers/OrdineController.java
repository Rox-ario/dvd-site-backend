package it.progetto.backend.controllers;
import it.progetto.backend.DTOs.CreaOrdineRequest;
import it.progetto.backend.DTOs.OrdineResponseDTO;
import it.progetto.backend.entities.Ordine;
import it.progetto.backend.services.OrdineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordini")
@RequiredArgsConstructor
public class OrdineController
{
    private final OrdineService ordineService;

    @PostMapping
    public Ordine creaOrdine(@RequestBody CreaOrdineRequest request)
    {
        return ordineService.elaboraAcquisto(request);
    }

    @GetMapping("/storico/{idCliente}")
    public List<OrdineResponseDTO> ottieniStorico(@PathVariable Long idCliente)
    {
        return ordineService.ottieniStoricoCliente(idCliente);
    }

    @GetMapping
    public List<OrdineResponseDTO> visualizzaTuttiGliOrdini(
            @RequestParam(required = false) String stato) {

        return ordineService.ottieniTuttiGliOrdini(stato);
    }

    //esempio chiamata: PATCH /api/ordini/5/stato?nuovoStato=CONSEGNATO
    @PatchMapping("/{idOrdine}/stato")
    public OrdineResponseDTO cambiaStatoOrdine(
            @PathVariable Long idOrdine,
            @RequestParam String nuovoStato) {

        return ordineService.aggiornaStatoOrdine(idOrdine, nuovoStato);
    }
}
