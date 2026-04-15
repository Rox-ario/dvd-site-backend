package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.CreaFilmRequestDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.DTOs.FunFactDTO;
import it.progetto.backend.DTOs.RecensioneResponseDTO;
import it.progetto.backend.entities.Recensione;
import it.progetto.backend.security.JwtService;
import it.progetto.backend.services.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/film")
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final JwtService jwtService;

    @GetMapping
    public List<FilmResponseDTO> esploraCatalogo(
            @RequestParam(required = false) String titolo,
            @RequestParam(required = false) String nomeGenere,
            @RequestParam(required = false) String nomeAttore,
            @RequestParam(required = false) String nomeRegista,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) BigDecimal prezzoMax
            )
    {
        return filmService.ricercaAvanzata(titolo, nomeGenere, nomeAttore, nomeRegista, anno, prezzoMax);
    }

    @GetMapping("/{id}")
    public FilmResponseDTO ottieniDettaglioFilm(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String emailUtente = null;
        // Se c'è il token, cerchiamo di capire chi è l'utente
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                emailUtente = jwtService.extractUsername(authHeader.substring(7));
            } catch (Exception e) {
                // Se il token è scaduto o invalido, lo trattiamo semplicemente come un utente non loggato
            }
        }

        return filmService.ottieniFilmPerId(id, emailUtente);
    }

    @PostMapping("/{id}/recensioni")
    public RecensioneResponseDTO scriviRecensione(
            @PathVariable Long id,
            @RequestBody Recensione request, // Puoi usare un DTO dedicato qui
            @RequestHeader("Authorization") String authHeader) {

        String email = jwtService.extractUsername(authHeader.substring(7));
        return filmService.aggiungiRecensione(id, email, request.getStelle(), request.getCommento());
    }

    @PostMapping
    public FilmResponseDTO aggiungiFilm(@RequestBody CreaFilmRequestDTO request) {
        return filmService.creaFilm(request);
    }

    @PutMapping("/{id}")
    public FilmResponseDTO aggiornaFilm(@PathVariable Long id, @RequestBody CreaFilmRequestDTO request) {
        return filmService.aggiornaFilm(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminaFilm(@PathVariable Long id)
    {
        filmService.eliminaFilm(id);
    }

    @GetMapping("/{id}/simili")
    public List<FilmResponseDTO> ottieniFilmSimili(@PathVariable Long id)
    {
        return filmService.ottieniFilmSimili(id);
    }

    @PostMapping("/{id}/curiosita")
    public void aggiungiFunFact(@PathVariable Long id, @RequestBody FunFactDTO funFact) {
        filmService.aggiungiFunFact(id, funFact);
    }
}
