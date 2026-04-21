package it.progetto.backend.services;
import it.progetto.backend.DTOs.AggiornaAnagraficaRequestDTO;
import it.progetto.backend.DTOs.CambiaPasswordRequestDTO;
import it.progetto.backend.DTOs.ClienteProfileResponseDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.entities.Cliente;
import it.progetto.backend.entities.Film;
import it.progetto.backend.repositories.ClienteRepository;
import it.progetto.backend.repositories.FilmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService
{
    private final ClienteRepository clienteRepository;
    private final FilmRepository filmRepository;
    private final FilmService filmService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ClienteProfileResponseDTO ottieniProfilo(String emailCliente)
    {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato nel sistema."));

        ClienteProfileResponseDTO dto = new ClienteProfileResponseDTO();
        dto.setNome(cliente.getNome());
        dto.setCognome(cliente.getCognome());
        dto.setEmail(cliente.getEmail());
        dto.setRuolo(cliente.getRuolo().name());
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

    @Transactional
    public void cambiaPassword(String emailCliente, CambiaPasswordRequestDTO dto) {
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato."));

        if (!passwordEncoder.matches(dto.getVecchiaPassword(), cliente.getPassword())) {
            throw new IllegalArgumentException("La vecchia password inserita non è corretta.");
        }

        if (!dto.getNuovaPassword().equals(dto.getConfermaNuovaPassword())) {
            throw new IllegalArgumentException("La nuova password e la conferma non coincidono.");
        }

        if (dto.getNuovaPassword().length() < 8) {
            throw new IllegalArgumentException("La nuova password deve contenere almeno 8 caratteri.");
        }

        cliente.setPassword(passwordEncoder.encode(dto.getNuovaPassword()));
        clienteRepository.save(cliente);

        emailService.inviaNotificaCambioPassword(cliente.getEmail(), cliente.getNome());
    }
}
