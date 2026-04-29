package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.CreaGenereRequest;
import it.progetto.backend.DTOs.GenereDTO;
import it.progetto.backend.services.GenereService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/generi")
@RequiredArgsConstructor
public class GenereController
{
    private final GenereService genereService;

    @GetMapping
    public Page<GenereDTO> esploraGeneri(@RequestParam(required = false, name = "q") String query,
                                         @PageableDefault(size = 10) Pageable pageable)
    {
        if (query != null && !query.trim().isEmpty())
        {
            return genereService.ricercaGeneri(query.trim(), pageable);
        }
        return genereService.ottieniTutti(pageable);
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
