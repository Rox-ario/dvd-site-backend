package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.CreaFilmRequestDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.DTOs.FunFactDTO;
import it.progetto.backend.DTOs.RecensioneResponseDTO;
import it.progetto.backend.entities.Recensione;
import it.progetto.backend.services.ClienteService;
import it.progetto.backend.services.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

@RequestMapping("/api/film")
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final ClienteService clienteService;

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
            @AuthenticationPrincipal Jwt jwt) {

        String emailUtente = null;
        try {
                emailUtente = jwt.getClaimAsString("email");
            } catch (Exception e) {
                // Se il token è scaduto o invalido, lo trattiamo semplicemente come un utente non loggato
            }

        return filmService.ottieniFilmPerId(id, emailUtente);
    }

    @PostMapping("/{id}/recensioni")
    public RecensioneResponseDTO scriviRecensione(
            @PathVariable Long id,
            @RequestBody Recensione request,
            @AuthenticationPrincipal Jwt jwt) throws ParseException {

        // Sincronizza il cliente nel DB se accede per la prima volta
        clienteService.sincronizzaClienteDaToken(jwt);
        String email = jwt.getClaimAsString("email");
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
