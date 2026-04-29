package it.progetto.backend.services;
import it.progetto.backend.DTOs.AttoreDTO;
import it.progetto.backend.DTOs.CreaAttoreRequest;
import it.progetto.backend.entities.Attore;
import it.progetto.backend.repositories.AttoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//ha un ruolo di ricerca o creazione dell'entity Attore
@Service
@RequiredArgsConstructor
public class AttoreService
{
    private final AttoreRepository attoreRepository;

    @Transactional
    public AttoreDTO creaAttore(CreaAttoreRequest request)
    {
        String nomeNorm = request.getNome().trim();
        String cognomeNorm = request.getCognome().trim();

        if (attoreRepository.existsByNomeIgnoreCaseAndCognomeIgnoreCase(nomeNorm, cognomeNorm))
        {
            throw new IllegalArgumentException("L'attore " + nomeNorm + " " + cognomeNorm + " esiste già nel sistema.");
        }

        Attore nuovoAttore = new Attore();
        nuovoAttore.setNome(nomeNorm);
        nuovoAttore.setCognome(cognomeNorm);

        return convertiInDTO(attoreRepository.save(nuovoAttore));
    }

    public Page<AttoreDTO> ottieniTuttiGliAttori(Pageable pageable)
    {
        return attoreRepository.findAll(pageable)
                .map(this::convertiInDTO);
    }

    @Transactional
    public AttoreDTO aggiornaAttore(Long id, CreaAttoreRequest request)
    {
        Attore attore = attoreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attore non trovato."));

        attore.setNome(request.getNome().trim());
        attore.setCognome(request.getCognome().trim());

        return convertiInDTO(attoreRepository.save(attore));
    }

    public Page<AttoreDTO> ricercaAttori(String query, Pageable pageable)
    {
        return attoreRepository
                .findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(query, query, pageable)
                .map(this::convertiInDTO);
    }

    private AttoreDTO convertiInDTO(Attore attore)
    {
        AttoreDTO dto = new AttoreDTO();
        dto.setId(attore.getId());
        dto.setNome(attore.getNome());
        dto.setCognome(attore.getCognome());
        return dto;
    }
}
