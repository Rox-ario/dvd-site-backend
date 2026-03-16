package it.progetto.backend.controllers;

import it.progetto.backend.DTOs.AuthResponseDTO;
import it.progetto.backend.DTOs.LoginRequestDTO;
import it.progetto.backend.DTOs.RegistrazioneRequestDTO;
import it.progetto.backend.entities.Cliente;
import it.progetto.backend.repositories.ClienteRepository;
import it.progetto.backend.security.JwtService;
import it.progetto.backend.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager; // Il "Giudice" di Spring Security
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ClienteRepository clienteRepository;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager,
                          JwtService jwtService, UserDetailsService userDetailsService,
                          ClienteRepository clienteRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.clienteRepository = clienteRepository;
    }

    // ROTTA 1: REGISTRAZIONE
    @PostMapping("/registrazione")
    public ResponseEntity<String> registra(@RequestBody RegistrazioneRequestDTO request) {
        // Deleghiamo tutta la logica (controlli, hashing, ruoli) al nostro AuthService!
        String messaggio = authService.registraCliente(request);
        return ResponseEntity.ok(messaggio);
    }

    // ROTTA 2: LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {

        // 1. IL TENTATIVO: Chiediamo a Spring Security di verificare email e password sul database.
        // Se la password è sbagliata, questo metodo lancerà un'eccezione in automatico bloccando tutto (BadCredentialsException).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. SE SIAMO QUI, LA PASSWORD ERA GIUSTA! Recuperiamo l'utente tradotto per Spring...
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // 3. GENERIAMO IL PASSAPORTO CRITTOGRAFICO
        String jwtToken = jwtService.generateToken(userDetails);

        // 4. RECUPERIAMO I DATI EXTRA PER IL FRONTEND
        // Peschiamo il cliente dal DB per prendere il suo nome reale e il ruolo
        Cliente cliente = clienteRepository.findByEmail(request.getEmail()).get();

        // 5. PACCHETTO DI RISPOSTA
        AuthResponseDTO response = new AuthResponseDTO(
                jwtToken,
                cliente.getNome(),
                cliente.getRuolo().name()
        );

        return ResponseEntity.ok(response);
    }
}
