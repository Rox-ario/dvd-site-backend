package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.RecensioneResponseDTO;
import it.progetto.backend.entities.Recensione;
import it.progetto.backend.services.ClienteService;
import it.progetto.backend.services.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recensioni")
@RequiredArgsConstructor
public class RecensioneController {

    private final FilmService filmService;
    private final ClienteService clienteService;

    @PutMapping("/{id}")
    public RecensioneResponseDTO modificaRecensione(
            @PathVariable Long id,
            @RequestBody Recensione request,
            @AuthenticationPrincipal Jwt jwt) {

        // Sincronizza il cliente nel DB se accede per la prima volta
        clienteService.sincronizzaClienteDaToken(jwt);
        String email = jwt.getClaimAsString("email");
        return filmService.modificaRecensione(id, email, request.getStelle(), request.getCommento());
    }

    @DeleteMapping("/{id}")
    public void eliminaRecensione(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        // Sincronizza il cliente nel DB se accede per la prima volta
        clienteService.sincronizzaClienteDaToken(jwt);
        String email = jwt.getClaimAsString("email");
        filmService.eliminaRecensione(id, email);
    }
    @GetMapping("/{id}")
    public Page<RecensioneResponseDTO> ottieniRecensioniPaginato(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 5) Pageable pageable)
    {
        return filmService.getRecensioni(id, pageable);
    }
}
