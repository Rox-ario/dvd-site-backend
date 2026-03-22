package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.CreaGenereRequest;
import it.progetto.backend.DTOs.GenereDTO;
import it.progetto.backend.services.GenereService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generi")
@RequiredArgsConstructor
public class GenereController
{
    private final GenereService genereService;

    @GetMapping
    public List<GenereDTO> esploraGeneri(@RequestParam(required = false, name = "q") String query) {
        if (query != null && !query.trim().isEmpty())
        {
            return genereService.ricercaGeneri(query.trim());
        }
        return genereService.ottieniTutti();
    }

    @PostMapping
    public GenereDTO aggiungiGenere(@RequestBody CreaGenereRequest request) {
        return genereService.creaGenere(request);
    }

    @PutMapping("/{id}")
    public GenereDTO modificaGenere(@PathVariable Long id, @RequestBody CreaGenereRequest request)
    {
        return genereService.aggiornaGenere(id, request);
    }

    @DeleteMapping("/{id}")
    public Void rimuoviGenere(@PathVariable Long id)
    {
        genereService.eliminaGenere(id);
        return null;
    }
}
