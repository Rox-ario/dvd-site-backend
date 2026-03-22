package it.progetto.backend.services;
import it.progetto.backend.DTOs.CreaRegistaRequest;
import it.progetto.backend.DTOs.RegistaDTO;
import it.progetto.backend.entities.Regista;
import it.progetto.backend.repositories.RegistaRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//ha un ruolo di ricerca o creazione dell'entity regista
@Service
@RequiredArgsConstructor
public class RegistaService
{
    private final RegistaRepository registaRepository;

    @Transactional
    public RegistaDTO creaRegista(CreaRegistaRequest request)
    {
        String nomeNorm = request.getNome().trim();
        String cognomeNorm = request.getCognome().trim();

        if (registaRepository.existsByNomeIgnoreCaseAndCognomeIgnoreCase(nomeNorm, cognomeNorm)) {
            throw new IllegalArgumentException("Il regista " + nomeNorm + " " + cognomeNorm + " esiste già.");
        }

        Regista regista = new Regista();
        regista.setNome(nomeNorm);
        regista.setCognome(cognomeNorm);
        return convertiInDTO(registaRepository.save(regista));
    }

    public List<RegistaDTO> ottieniTutti() {
        return registaRepository.findAll().stream().map(this::convertiInDTO).toList();
    }

    public List<RegistaDTO> ricercaRegisti(String query) {
        return registaRepository.findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(query, query)
                .stream().map(this::convertiInDTO).toList();
    }

    @Transactional
    public RegistaDTO aggiornaRegista(Long id, CreaRegistaRequest request) {
        Regista regista = registaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regista non trovato."));
        regista.setNome(request.getNome().trim());
        regista.setCognome(request.getCognome().trim());
        return convertiInDTO(registaRepository.save(regista));
    }

    public void eliminaRegista(Long id)
    {
        if (!registaRepository.existsById(id)) {
            throw new IllegalArgumentException("Regista non trovato.");
        }
        try {
            registaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e)
        {
            throw new IllegalStateException("Impossibile eliminare: il regista è associato a dei film.");
        }
    }

    private RegistaDTO convertiInDTO(Regista regista) {
        RegistaDTO dto = new RegistaDTO();
        dto.setId(regista.getId());
        dto.setNome(regista.getNome());
        dto.setCognome(regista.getCognome());
        return dto;
    }
}