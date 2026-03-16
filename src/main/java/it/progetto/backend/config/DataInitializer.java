package it.progetto.backend.config;

import it.progetto.backend.entities.Cliente;
import it.progetto.backend.enums.Ruolo;
import it.progetto.backend.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component // Diciamo a Spring: "Prendi in carico questa classe e gestiscila tu"
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception
    {
        String emailAdmin = "chprsr03h28d086a@studenti.unical.it";

        if (clienteRepository.findByEmail(emailAdmin).isEmpty()) {

            System.out.println("Nessun Admin trovato. Generazione SuperUtente in corso...");

            Cliente admin = new Cliente();
            admin.setNome("Rosario");
            admin.setCognome("Chiappetta");
            admin.setEmail(emailAdmin);

            admin.setPassword(passwordEncoder.encode("admin123"));

            admin.setPuntiFedelta(0);
            admin.setFilmPreferiti(new HashSet<>());

            admin.setRuolo(Ruolo.ADMIN);

            clienteRepository.save(admin);

            System.out.println("SuperUtente creato con successo!");
        }
        System.out.println("Bentornato "+emailAdmin+"! Il sistema è pronto per l'uso.");
    }
}
