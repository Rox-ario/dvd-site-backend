package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.AggiornaAnagraficaRequestDTO;
import it.progetto.backend.DTOs.ClienteProfileResponseDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clienti")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/me/profilo")
    public ClienteProfileResponseDTO visualizzaProfilo(@AuthenticationPrincipal Jwt jwt) {
        // Sincronizza il cliente nel DB se accede per la prima volta
        clienteService.sincronizzaClienteDaToken(jwt);
        return clienteService.ottieniProfilo(jwt.getClaimAsString("email"));
    }

    @PutMapping("/me/profilo")
    public ClienteProfileResponseDTO aggiornaProfilo(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AggiornaAnagraficaRequestDTO request) {
        clienteService.sincronizzaClienteDaToken(jwt);
        return clienteService.aggiornaAnagrafica(jwt.getClaimAsString("email"), request);
    }

    @PostMapping("/me/preferiti/{idFilm}")
    public ClienteProfileResponseDTO aggiungiPreferito(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long idFilm) {
        clienteService.sincronizzaClienteDaToken(jwt);
        return clienteService.aggiungiFilmPreferito(jwt.getClaimAsString("email"), idFilm);
    }

    @DeleteMapping("/me/preferiti/{idFilm}")
    public ClienteProfileResponseDTO rimuoviPreferito(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long idFilm) {
        clienteService.sincronizzaClienteDaToken(jwt);
        return clienteService.rimuoviFilmPreferito(jwt.getClaimAsString("email"), idFilm);
    }

    @GetMapping("/me/preferiti")
    public List<FilmResponseDTO> visualizzaDettaglioPreferiti(@AuthenticationPrincipal Jwt jwt) {
        clienteService.sincronizzaClienteDaToken(jwt);
        return clienteService.ottieniDettaglioFilmPreferiti(jwt.getClaimAsString("email"));
    }

    @GetMapping("/me/preferiti/ids")
    public List<Long> visualizzaIdPreferiti(@AuthenticationPrincipal Jwt jwt) {
        clienteService.sincronizzaClienteDaToken(jwt);
        return clienteService.ottieniIdFilmPreferiti(jwt.getClaimAsString("email"));
    }

}
