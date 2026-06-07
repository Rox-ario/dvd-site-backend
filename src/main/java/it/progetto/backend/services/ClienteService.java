package it.progetto.backend.services;
import it.progetto.backend.DTOs.AggiornaAnagraficaRequestDTO;
import it.progetto.backend.DTOs.ClienteProfileResponseDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.DTOs.RegistrazioneClienteRequestDTO;
import it.progetto.backend.entities.Cliente;
import it.progetto.backend.entities.Film;
import it.progetto.backend.repositories.ClienteRepository;
import it.progetto.backend.repositories.FilmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService
{
    private final ClienteRepository clienteRepository;
    private final FilmRepository filmRepository;
    private final FilmService filmService;
    private final EmailService emailService;

    public ClienteProfileResponseDTO ottieniProfilo(String emailCliente)
    {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato nel sistema."));

        ClienteProfileResponseDTO dto = new ClienteProfileResponseDTO();
        dto.setNome(cliente.getNome());
        dto.setCognome(cliente.getCognome());
        dto.setEmail(cliente.getEmail());
        if (cliente.getFilmPreferiti() != null && !cliente.getFilmPreferiti().isEmpty())
        {
            Set<String> titoliPreferiti = cliente.getFilmPreferiti().stream()
                    .map(Film::getTitolo) //estraggo solo il titolo da ogni film
                    .collect(Collectors.toSet());  //li raggruppo in un Set univoco
            dto.setFilmPreferiti(titoliPreferiti);
        } else {
            dto.setFilmPreferiti(Collections.emptySet());
        }

        return dto;
    }

    @Transactional
    public ClienteProfileResponseDTO aggiornaAnagrafica(String emailCliente, AggiornaAnagraficaRequestDTO dto)
    {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato nel sistema."));

        if (dto.getNome() != null && !dto.getNome().trim().isEmpty()) {
            cliente.setNome(dto.getNome());
        }
        if (dto.getCognome() != null && !dto.getCognome().trim().isEmpty()) {
            cliente.setCognome(dto.getCognome());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty() && dto.getEmail().matches("^[a-zA-Z0-9._%+-]{1,64}@gmail\\.com$"))
        {
            cliente.setEmail(dto.getEmail());
        }
        clienteRepository.save(cliente);
        emailService.inviaNotificaAggiornamento(cliente.getEmail(), cliente.getNome());
        return ottieniProfilo(cliente.getEmail());
    }

    @Transactional
    public ClienteProfileResponseDTO aggiungiFilmPreferito(String emailCliente, Long idFilm)
    {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato."));

        Film film = filmRepository.findById(idFilm)
                .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + idFilm));

        if (!film.getIsAttivo()) {
            throw new IllegalStateException("Impossibile aggiungere: il film non è più disponibile nel catalogo.");
        }

        if (cliente.getFilmPreferiti().contains(film)) {
            throw new IllegalStateException("Questo film è già presente nella tua lista dei preferiti.");
        }

        cliente.getFilmPreferiti().add(film);

        clienteRepository.save(cliente);

        //restituisco il profilo già aggiornato
        return ottieniProfilo(emailCliente);
    }

    @Transactional
    public ClienteProfileResponseDTO rimuoviFilmPreferito(String emailCliente, Long idFilm)
    {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato nel sistema."));

        Film film = filmRepository.findById(idFilm)
                .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + idFilm));

        if (!cliente.getFilmPreferiti().contains(film)) {
            throw new IllegalStateException("Errore: Il film non è presente nei tuoi preferiti.");
        }

        cliente.getFilmPreferiti().remove(film);
        clienteRepository.save(cliente);

        return ottieniProfilo(emailCliente);
    }

    @Transactional(readOnly = true)
    public List<FilmResponseDTO> ottieniDettaglioFilmPreferiti(String emailCliente)
    {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato nel sistema."));

        return cliente.getFilmPreferiti().stream()
                .map(filmService::convertiInDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Long> ottieniIdFilmPreferiti(String emailCliente) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato."));

        return cliente.getFilmPreferiti().stream()
                .map(Film::getId)
                .collect(Collectors.toList());
    }

    /**
     * Registra un nuovo Cliente nel sistema al primo login da Keycloak.
     * Idempotente: se il cliente esiste già restituisce il profilo senza errori.
     * I campi nome/cognome/email vengono presi dal DTO se valorizzati,
     * altrimenti si utilizzano i claim del JWT come fallback.
     */
    @Transactional
    public ClienteProfileResponseDTO registraCliente(Jwt jwt, RegistrazioneClienteRequestDTO dto) {

        // Log dei claim disponibili nel JWT per facilitare il debug
        log.info("[registraCliente] Claims JWT disponibili: sub={}, email={}, preferred_username={}, given_name={}, family_name={}",
                jwt.getSubject(),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"));

        // -- Risolvi email: DTO → claim "email" → claim "preferred_username" come fallback --
        String email = resolvi(dto.getEmail());
        if (email == null) email = jwt.getClaimAsString("email");
        if (email == null) email = jwt.getClaimAsString("preferred_username"); // fallback Keycloak

        String nome = resolvi(dto.getNome());
        if (nome == null) nome = jwt.getClaimAsString("given_name");
        if (nome == null) nome = "Utente";

        String cognome = resolvi(dto.getCognome());
        if (cognome == null) cognome = jwt.getClaimAsString("family_name");
        if (cognome == null) cognome = "Sconosciuto";

        log.info("[registraCliente] Dati risolti — email: {}, nome: {}, cognome: {}", email, nome, cognome);

        // -- Validazione anti-injection (con null check esplicito) --
        if (email == null || !email.matches("^[a-zA-Z0-9._%+\\-]{1,64}@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) {
            log.error("[registraCliente] Email non valida o assente: '{}'", email);
            throw new IllegalArgumentException("Email non valida o non presente nel token.");
        }
        if (!nome.matches("^[a-zA-Z\u00C0-\u00FF' \\-]{1,100}$")) {
            log.warn("[registraCliente] Nome non valido: '{}', uso fallback 'Utente'", nome);
            nome = "Utente";
        }
        if (!cognome.matches("^[a-zA-Z\u00C0-\u00FF' \\-]{1,100}$")) {
            log.warn("[registraCliente] Cognome non valido: '{}', uso fallback 'Sconosciuto'", cognome);
            cognome = "Sconosciuto";
        }

        // -- Idempotenza: se esiste già, restituisce il profilo esistente --
        if (clienteRepository.existsByEmail(email)) {
            log.info("[registraCliente] Cliente già esistente per email: {}", email);
            return ottieniProfilo(email);
        }

        // -- Crea il nuovo cliente --
        Cliente nuovoCliente = new Cliente();
        nuovoCliente.setEmail(email);
        nuovoCliente.setNome(nome);
        nuovoCliente.setCognome(cognome);
        clienteRepository.save(nuovoCliente);
        log.info("[registraCliente] Nuovo cliente creato: {}", email);

        return ottieniProfilo(email);
    }

    /** Ritorna il valore stringa se non nullo e non vuoto, altrimenti null. */
    private static String resolvi(String valore) {
        return (valore != null && !valore.trim().isEmpty()) ? valore.trim() : null;
    }
}
