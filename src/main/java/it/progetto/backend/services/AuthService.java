package it.progetto.backend.services;

import it.progetto.backend.DTOs.RegistrazioneRequestDTO;
import it.progetto.backend.entities.Cliente;
import it.progetto.backend.enums.Ruolo;
import it.progetto.backend.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder; //strumento di Spring Security
    private final EmailService emailService;

    @Transactional
    public String registraCliente(RegistrazioneRequestDTO dto)
    {
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent())
        {
            throw new IllegalArgumentException("Errore: Un utente con questa email è già registrato.");
        }

        if(dto.getEmail() == null || !dto.getEmail().matches("^[a-zA-Z0-9._%+-]{1,64}@gmail\\.com$"))
        {
            throw new IllegalArgumentException("Errore: formato email non valido.");
        }

        Cliente nuovoCliente = Cliente.builder()
                        .nome(dto.getNome())
                        .cognome(dto.getCognome())
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .puntiFedelta(0)
                        .filmPreferiti(new HashSet<>())
                        .ruolo(Ruolo.CLIENTE)
                        .build();

        clienteRepository.save(nuovoCliente);
        emailService.inviaNotificaRegistrazione(nuovoCliente.getEmail(), nuovoCliente.getNome());

        return "Registrazione completata con successo. Ora puoi effettuare il login.";
    }
}
