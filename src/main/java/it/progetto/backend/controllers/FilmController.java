package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.CreaFilmRequestDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
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
    public FilmResponseDTO ottieniDettaglioFilm(@PathVariable Long id)
    {
        return filmService.ottieniFilmPerId(id);
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
}
