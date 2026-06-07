package it.progetto.backend.controllers;
import it.progetto.backend.DTOs.CreaOrdineRequest;
import it.progetto.backend.DTOs.OrdineResponseDTO;
import it.progetto.backend.services.OrdineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/ordini")
@RequiredArgsConstructor
public class OrdineController
{
    private final OrdineService ordineService;

    @PostMapping
    public OrdineResponseDTO creaOrdine(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreaOrdineRequest request) {

        String email = jwt.getClaimAsString("email");
        return ordineService.elaboraAcquisto(email, request);
    }

    @GetMapping("/me/storico")
    public Page<OrdineResponseDTO> ottieniMioStorico(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 10) Pageable pageable)
    {
        String email = jwt.getClaimAsString("email");
        return ordineService.ottieniStoricoCliente(email, pageable);
    }

    //esempio chiamata: PATCH /api/ordini/5/stato?nuovoStato=CONSEGNATO
    @PatchMapping("/{idOrdine}/stato")
    public OrdineResponseDTO cambiaStatoOrdine(
            @PathVariable Long idOrdine,
            @RequestParam String nuovoStato) {

        return ordineService.aggiornaStatoOrdine(idOrdine, nuovoStato);
    }

    @GetMapping("/admin/tutti")
    public Page<OrdineResponseDTO> ottieniTuttiGliOrdini(
            @RequestParam(required = false) String stato,
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "dataAcquisto", direction = Sort.Direction.DESC) Pageable pageable) {

        return ordineService.ottieniTuttiGliOrdini(stato, pageable);
    }

    @PostMapping("/me/{idOrdine}/annulla")
    public OrdineResponseDTO annullaMioOrdine(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long idOrdine) {

        String email = jwt.getClaimAsString("email");
        return ordineService.annullaMioOrdine(email, idOrdine);
    }
}
