package it.progetto.backend.controllers;
import it.progetto.backend.DTOs.CreaOrdineRequest;
import it.progetto.backend.DTOs.OrdineResponseDTO;
import it.progetto.backend.entities.Ordine;
import it.progetto.backend.security.JwtService;
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
    private final JwtService jwtService;

    private String ottieniEmailDaToken(String authHeader) {
        return jwtService.extractUsername(authHeader.substring(7));
    }

    @PostMapping
    public Ordine creaOrdine(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreaOrdineRequest request) {

        String email = ottieniEmailDaToken(authHeader);
        return ordineService.elaboraAcquisto(email, request);
    }

    @GetMapping("/me/storico")
    public List<OrdineResponseDTO> ottieniMioStorico(
            @RequestHeader("Authorization") String authHeader) {

        String email = ottieniEmailDaToken(authHeader);
        return ordineService.ottieniStoricoCliente(email);
    }

    //esempio chiamata: PATCH /api/ordini/5/stato?nuovoStato=CONSEGNATO
    @PatchMapping("/{idOrdine}/stato")
    public OrdineResponseDTO cambiaStatoOrdine(
            @PathVariable Long idOrdine,
            @RequestParam String nuovoStato) {

        return ordineService.aggiornaStatoOrdine(idOrdine, nuovoStato);
    }

    @GetMapping("/admin/tutti")
    public List<OrdineResponseDTO> ottieniTuttiGliOrdini(
            @RequestParam(required = false) String stato) {
        return ordineService.ottieniTuttiGliOrdini(stato);
    }
}
