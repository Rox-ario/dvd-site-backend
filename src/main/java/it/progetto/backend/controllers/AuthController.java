package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.*;
import it.progetto.backend.entities.Cliente;
import it.progetto.backend.repositories.ClienteRepository;
import it.progetto.backend.security.JwtService;
import it.progetto.backend.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ClienteRepository clienteRepository;

    @PostMapping("/registrazione")
    public ResponseEntity<String> registra(@RequestBody RegistrazioneRequestDTO request)
    {
        String messaggio = authService.registraCliente(request);
        return ResponseEntity.ok(messaggio);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request)
    {
        //controllo
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        String jwtToken = jwtService.generateToken(userDetails);

        Cliente cliente = clienteRepository.findByEmail(request.getEmail()).get();

        AuthResponseDTO response = new AuthResponseDTO(
                jwtToken,
                cliente.getNome(),
                cliente.getRuolo().name()
        );

        return ResponseEntity.ok(response);
    }
}
