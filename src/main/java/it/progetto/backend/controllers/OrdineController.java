package it.progetto.backend.controllers;
import com.nimbusds.jwt.JWT;
import it.progetto.backend.DTOs.CreaOrdineRequest;
import it.progetto.backend.DTOs.OrdineResponseDTO;
import it.progetto.backend.services.ClienteService;
import it.progetto.backend.services.OrdineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordini")
@RequiredArgsConstructor
public class OrdineController
{
    private final OrdineService ordineService;
    private final ClienteService clienteService;

    @PostMapping
    public OrdineResponseDTO creaOrdine(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreaOrdineRequest request) {

        // Assicura che l'utente esista nel DB locale
        clienteService.sincronizzaClienteDaToken(jwt);
        String email = jwt.getClaimAsString("email");
        return ordineService.elaboraAcquisto(email, request);
    }

    @GetMapping("/me/storico")
    public List<OrdineResponseDTO> ottieniMioStorico(@AuthenticationPrincipal Jwt jwt)
    {
        clienteService.sincronizzaClienteDaToken(jwt);
        String email = jwt.getClaimAsString("email");
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
            @RequestParam(required = false) String stato,
            @AuthenticationPrincipal Jwt jwt) {
        clienteService.sincronizzaClienteDaToken(jwt);
        return ordineService.ottieniTuttiGliOrdini(stato);
    }

    @PostMapping("/me/{idOrdine}/annulla")
    public OrdineResponseDTO annullaMioOrdine(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long idOrdine) {

        String email = jwt.getClaimAsString("email");
        return ordineService.annullaMioOrdine(email, idOrdine);
    }
}
