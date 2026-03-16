package it.progetto.backend.services;
import it.progetto.backend.DTOs.AggiornaAnagraficaRequestDTO;
import it.progetto.backend.DTOs.ClienteProfileResponseDTO;
import it.progetto.backend.entities.Cliente;
import it.progetto.backend.entities.Film;
import it.progetto.backend.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClienteService
{
    private final ClienteRepository clienteRepository;
    //private final EmailService emailService;

    public ClienteService(ClienteRepository clienteRepository)
    {
        this.clienteRepository = clienteRepository;
    }

    public ClienteProfileResponseDTO ottieniProfilo(Long idCliente)
    {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato nel sistema."));

        ClienteProfileResponseDTO dto = new ClienteProfileResponseDTO();
        dto.setNome(cliente.getNome());
        dto.setCognome(cliente.getCognome());
        dto.setEmail(cliente.getEmail());
        dto.setPuntiFedeltà(cliente.getPuntiFedelta());
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
}
