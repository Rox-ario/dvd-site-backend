package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.AggiornaAnagraficaRequestDTO;
import it.progetto.backend.DTOs.ClienteProfileResponseDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clienti")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/{idCliente}/profilo")
    public ClienteProfileResponseDTO visualizzaProfilo(@PathVariable Long idCliente)
    {
        return clienteService.ottieniProfilo(idCliente);
    }

    @PutMapping("/{idCliente}/profilo")
    public ClienteProfileResponseDTO aggiornaProfilo(
            @PathVariable Long idCliente,
            @RequestBody AggiornaAnagraficaRequestDTO request)
    {

        return clienteService.aggiornaAnagrafica(idCliente, request);
    }

    @PostMapping("/{idCliente}/preferiti/{idFilm}")
    public ClienteProfileResponseDTO aggiungiPreferito(
            @PathVariable Long idCliente,
            @PathVariable Long idFilm)
    {

        return clienteService.aggiungiFilmPreferito(idCliente, idFilm);
    }

    @DeleteMapping("/{idCliente}/preferiti/{idFilm}")
    public ClienteProfileResponseDTO rimuoviPreferito(
            @PathVariable Long idCliente,
            @PathVariable Long idFilm)
    {

        return clienteService.rimuoviFilmPreferito(idCliente, idFilm);
    }

    @GetMapping("/{idCliente}/preferiti")
    public List<FilmResponseDTO> visualizzaDettaglioPreferiti(@PathVariable Long idCliente)
    {
        return clienteService.ottieniDettaglioFilmPreferiti(idCliente);
    }

}
