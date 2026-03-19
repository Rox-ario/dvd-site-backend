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
public class OrdineController {

    private final OrdineService ordineService;

    @PostMapping
    public ResponseEntity<Ordine> creaOrdine(@RequestBody CreaOrdineRequest request)
    {
        Ordine nuovoOrdine = ordineService.elaboraAcquisto(request);
        return ResponseEntity.ok(nuovoOrdine);
    }

    @GetMapping("/storico/{idCliente}")
    public ResponseEntity<List<OrdineResponseDTO>> ottieniStorico(@PathVariable Long idCliente)
    {
        List<OrdineResponseDTO> storico = ordineService.ottieniStoricoCliente(idCliente);
        return ResponseEntity.ok(storico);
    }

    @GetMapping
    public ResponseEntity<List<OrdineResponseDTO>> visualizzaTuttiGliOrdini(
            @RequestParam(required = false) String stato) {

        List<OrdineResponseDTO> ordini = ordineService.ottieniTuttiGliOrdini(stato);
        return ResponseEntity.ok(ordini);
    }

    //esempio chiamata: PATCH /api/ordini/5/stato?nuovoStato=CONSEGNATO
    @PatchMapping("/{idOrdine}/stato")
    public ResponseEntity<OrdineResponseDTO> cambiaStatoOrdine(
            @PathVariable Long idOrdine,
            @RequestParam String nuovoStato) {

        OrdineResponseDTO ordineAggiornato = ordineService.aggiornaStatoOrdine(idOrdine, nuovoStato);
        return ResponseEntity.ok(ordineAggiornato);
    }
}
