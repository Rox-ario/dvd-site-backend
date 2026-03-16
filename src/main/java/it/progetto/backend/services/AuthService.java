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

    @Transactional
    public String registraCliente(RegistrazioneRequestDTO dto)
    {
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Errore: Un utente con questa email è già registrato.");
        }

        Cliente nuovoCliente = new Cliente();
        nuovoCliente.setNome(dto.getNome());
        nuovoCliente.setCognome(dto.getCognome());
        nuovoCliente.setEmail(dto.getEmail());

        String passwordCriptata = passwordEncoder.encode(dto.getPassword());
        nuovoCliente.setPassword(passwordCriptata);

        nuovoCliente.setPuntiFedelta(0);
        nuovoCliente.setFilmPreferiti(new HashSet<>());
        nuovoCliente.setRuolo(Ruolo.CLIENTE);

        clienteRepository.save(nuovoCliente);

        return "Registrazione completata con successo. Ora puoi effettuare il login.";
    }
}
