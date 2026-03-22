package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.CreaRegistaRequest;
import it.progetto.backend.DTOs.RegistaDTO;
import it.progetto.backend.services.RegistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registi")
@RequiredArgsConstructor
public class RegistaController
{
    private final RegistaService registaService;

    @GetMapping
    public List<RegistaDTO> esploraRegisti(@RequestParam(required = false, name = "q") String query) {
        if (query != null && !query.trim().isEmpty()) {
            return registaService.ricercaRegisti(query.trim());
        }
        return registaService.ottieniTutti();
    }

    @PostMapping
    public RegistaDTO aggiungiRegista(@RequestBody CreaRegistaRequest request) {
        return registaService.creaRegista(request);
    }

    @PutMapping("/{id}")
    public RegistaDTO modificaRegista(@PathVariable Long id, @RequestBody CreaRegistaRequest request) {
        return registaService.aggiornaRegista(id, request);
    }

    @DeleteMapping("/{id}")
    public Void rimuoviRegista(@PathVariable Long id) {
        registaService.eliminaRegista(id);
        return null;
    }
}
