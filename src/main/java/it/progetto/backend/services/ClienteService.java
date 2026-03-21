package it.progetto.backend.services;
import it.progetto.backend.DTOs.AggiornaAnagraficaRequestDTO;
import it.progetto.backend.DTOs.ClienteProfileResponseDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.entities.Cliente;
import it.progetto.backend.entities.Film;
import it.progetto.backend.repositories.ClienteRepository;
import it.progetto.backend.repositories.FilmRepository;
import lombok.RequiredArgsConstructor;
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
    //private final EmailService emailService;

    public ClienteProfileResponseDTO ottieniProfilo(Long idCliente)
    {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato nel sistema."));

        ClienteProfileResponseDTO dto = new ClienteProfileResponseDTO();
        dto.setNome(cliente.getNome());
        dto.setCognome(cliente.getCognome());
        dto.setEmail(cliente.getEmail());
        dto.setPuntiFedeltà(cliente.getPuntiFedelta());
        dto.setRuolo(cliente.getRuolo().name());
        if (cliente.getFilmPreferiti() != null && !cliente.getFilmPreferiti().isEmpty())
        {
            Set<String> titoliPreferiti = cliente.getFilmPreferiti().stream()
                    .map(Film::getTitolo) //estraggo solo il titolo da ogni film
                    .collect(Collectors.toSet());  //li raggruppo in un Set univoco
            dto.setFilmPreferiti(titoliPreferiti);
        } else {
            dto.setFilmPreferiti(Collections.emptySet());
            //TODO: il frontend deve mostrare "nessun film preferito, aggiungi alla lista!"
        }

        return dto;
    }

    //TODO: Metodi di modifica e aggiornamento

    @Transactional
    public ClienteProfileResponseDTO aggiornaAnagrafica(Long idCliente, AggiornaAnagraficaRequestDTO dto)
    {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato."));

        if (dto.getNome() != null && !dto.getNome().trim().isEmpty()) {
            cliente.setNome(dto.getNome());
        }
        if (dto.getCognome() != null && !dto.getCognome().trim().isEmpty()) {
            cliente.setCognome(dto.getCognome());
        }
        clienteRepository.save(cliente);
        //emailService.inviaNotificaAggiornamento(cliente.getEmail(), cliente.getNome());
        return ottieniProfilo(idCliente);
    }

    @Transactional
    public ClienteProfileResponseDTO aggiungiFilmPreferito(Long idCliente, Long idFilm)
    {
        Cliente cliente = clienteRepository.findById(idCliente)
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
        return ottieniProfilo(idCliente);
    }

    @Transactional
    public ClienteProfileResponseDTO rimuoviFilmPreferito(Long idCliente, Long idFilm)
    {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato."));

        Film film = filmRepository.findById(idFilm)
                .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + idFilm));

        if (!cliente.getFilmPreferiti().contains(film)) {
            throw new IllegalStateException("Errore: Il film non è presente nei tuoi preferiti.");
        }

        cliente.getFilmPreferiti().remove(film);
        clienteRepository.save(cliente);

        return ottieniProfilo(idCliente);
    }

    @Transactional(readOnly = true)
    public List<FilmResponseDTO> ottieniDettaglioFilmPreferiti(Long idCliente)
    {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato."));

        return cliente.getFilmPreferiti().stream()
                .map(filmService::convertiInDTO)
                .collect(Collectors.toList());
    }
}
