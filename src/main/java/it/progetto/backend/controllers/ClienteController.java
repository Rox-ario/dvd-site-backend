package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.AggiornaAnagraficaRequestDTO;
import it.progetto.backend.DTOs.ClienteProfileResponseDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.DTOs.RegistrazioneClienteRequestDTO;
import it.progetto.backend.services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clienti")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/register")
    public ClienteProfileResponseDTO registraCliente(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody(required = false) RegistrazioneClienteRequestDTO request) {
        if (request == null) {
            request = new RegistrazioneClienteRequestDTO();
        }
        return clienteService.registraCliente(jwt, request);
    }

    @GetMapping("/me/profilo")
    public ClienteProfileResponseDTO visualizzaProfilo(@AuthenticationPrincipal Jwt jwt) {
        return clienteService.ottieniProfilo(jwt.getClaimAsString("email"));
    }

    @PutMapping("/me/profilo")
    public ClienteProfileResponseDTO aggiornaProfilo(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AggiornaAnagraficaRequestDTO request) {
        return clienteService.aggiornaAnagrafica(jwt.getClaimAsString("email"), request);
    }

    @PostMapping("/me/preferiti/{idFilm}")
    public ClienteProfileResponseDTO aggiungiPreferito(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long idFilm) {
        return clienteService.aggiungiFilmPreferito(jwt.getClaimAsString("email"), idFilm);
    }

    @DeleteMapping("/me/preferiti/{idFilm}")
    public ClienteProfileResponseDTO rimuoviPreferito(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long idFilm) {
        return clienteService.rimuoviFilmPreferito(jwt.getClaimAsString("email"), idFilm);
    }

    @GetMapping("/me/preferiti")
    public List<FilmResponseDTO> visualizzaDettaglioPreferiti(@AuthenticationPrincipal Jwt jwt) {
        return clienteService.ottieniDettaglioFilmPreferiti(jwt.getClaimAsString("email"));
    }

    @GetMapping("/me/preferiti/ids")
    public List<Long> visualizzaIdPreferiti(@AuthenticationPrincipal Jwt jwt) {
        return clienteService.ottieniIdFilmPreferiti(jwt.getClaimAsString("email"));
    }

}
