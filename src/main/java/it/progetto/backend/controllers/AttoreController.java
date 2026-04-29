package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.AttoreDTO;
import it.progetto.backend.DTOs.CreaAttoreRequest;
import it.progetto.backend.services.AttoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attori")
@RequiredArgsConstructor
public class AttoreController
{
    private final AttoreService attoreService;

    @GetMapping
    public Page<AttoreDTO> esploraAttori(@RequestParam(required = false, name = "q") String query,
                                         @PageableDefault(size = 10) Pageable pageable)
    {
        if (query != null && !query.trim().isEmpty())
        {
            return attoreService.ricercaAttori(query.trim(), pageable);
        }
        return attoreService.ottieniTuttiGliAttori(pageable);
    }

    @PostMapping
    public AttoreDTO aggiungiAttore(@RequestBody CreaAttoreRequest request) {
        return attoreService.creaAttore(request);
    }

    @PutMapping("/{id}")
    public AttoreDTO modificaAttore(@PathVariable Long id, @RequestBody CreaAttoreRequest request) {
        return attoreService.aggiornaAttore(id, request);
    }
}
