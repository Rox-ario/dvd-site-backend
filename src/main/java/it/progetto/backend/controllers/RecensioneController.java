package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.RecensioneResponseDTO;
import it.progetto.backend.entities.Recensione;
import it.progetto.backend.security.JwtService;
import it.progetto.backend.services.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recensioni")
@RequiredArgsConstructor
public class RecensioneController {

    private final FilmService filmService;
    private final JwtService jwtService;

    @PutMapping("/{id}")
    public RecensioneResponseDTO modificaRecensione(
            @PathVariable Long id,
            @RequestBody Recensione request,
            @RequestHeader("Authorization") String authHeader) {

        String email = jwtService.extractUsername(authHeader.substring(7));
        return filmService.modificaRecensione(id, email, request.getStelle(), request.getCommento());
    }

    @DeleteMapping("/{id}")
    public void eliminaRecensione(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String email = jwtService.extractUsername(authHeader.substring(7));
        filmService.eliminaRecensione(id, email);
    }
}
