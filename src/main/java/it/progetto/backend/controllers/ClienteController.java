package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.AggiornaAnagraficaRequestDTO;
import it.progetto.backend.DTOs.CambiaPasswordRequestDTO;
import it.progetto.backend.DTOs.ClienteProfileResponseDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.security.JwtService;
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
    private final JwtService jwtService;

    private String ottieniEmailDaToken(String authHeader)
    {
        String token = authHeader.substring(7); // Rimuove "Bearer "
        return jwtService.extractUsername(token);
    }

    @GetMapping("/me/profilo")
    public ClienteProfileResponseDTO visualizzaProfilo(
            @RequestHeader("Authorization") String authHeader) {

        String email = ottieniEmailDaToken(authHeader);
        return clienteService.ottieniProfilo(email);
    }

    @PutMapping("/me/profilo")
    public ClienteProfileResponseDTO aggiornaProfilo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AggiornaAnagraficaRequestDTO request) {

        String email = ottieniEmailDaToken(authHeader);
        return clienteService.aggiornaAnagrafica(email, request);
    }

    @PostMapping("/me/preferiti/{idFilm}")
    public ClienteProfileResponseDTO aggiungiPreferito(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long idFilm) {

        String email = ottieniEmailDaToken(authHeader);
        return clienteService.aggiungiFilmPreferito(email, idFilm);
    }

    @DeleteMapping("/me/preferiti/{idFilm}")
    public ClienteProfileResponseDTO rimuoviPreferito(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long idFilm) {

        String email = ottieniEmailDaToken(authHeader);
        return clienteService.rimuoviFilmPreferito(email, idFilm);
    }

    @GetMapping("/me/preferiti")
    public List<FilmResponseDTO> visualizzaDettaglioPreferiti(
            @RequestHeader("Authorization") String authHeader) {

        String email = ottieniEmailDaToken(authHeader);
        return clienteService.ottieniDettaglioFilmPreferiti(email);
    }

    @GetMapping("/me/preferiti/ids")
    public List<Long> visualizzaIdPreferiti(@RequestHeader("Authorization") String authHeader) {
        String email = ottieniEmailDaToken(authHeader);
        return clienteService.ottieniIdFilmPreferiti(email);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<String> modificaPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CambiaPasswordRequestDTO request) {

        String email = ottieniEmailDaToken(authHeader);
        clienteService.cambiaPassword(email, request);

        return ResponseEntity.ok("Password aggiornata con successo.");
    }

}
