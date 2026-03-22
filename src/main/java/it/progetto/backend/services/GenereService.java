package it.progetto.backend.services;
import it.progetto.backend.DTOs.CreaGenereRequest;
import it.progetto.backend.DTOs.GenereDTO;
import it.progetto.backend.entities.Genere;
import it.progetto.backend.repositories.GenereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//ha un ruolo di ricerca o creazione dell'entity Genere
@Service
@RequiredArgsConstructor
public class GenereService
{
    private final GenereRepository genereRepository;

    @Transactional
    public GenereDTO creaGenere(CreaGenereRequest request)
    {
        String nomeNorm = request.getNome().trim();

        if (genereRepository.existsByNomeIgnoreCase(nomeNorm)) {
            throw new IllegalArgumentException("Il genere '" + nomeNorm + "' è già presente in catalogo.");
        }

        Genere genere = new Genere();
        genere.setNome(nomeNorm);
        return convertiInDTO(genereRepository.save(genere));
    }

    public List<GenereDTO> ottieniTutti() {
        return genereRepository.findAll().stream().map(this::convertiInDTO).toList();
    }

    public List<GenereDTO> ricercaGeneri(String query) {
        return genereRepository.findByNomeContainingIgnoreCase(query)
                .stream().map(this::convertiInDTO).toList();
    }

    @Transactional
    public GenereDTO aggiornaGenere(Long id, CreaGenereRequest request) {
        Genere genere = genereRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genere non trovato."));
        genere.setNome(request.getNome().trim());
        return convertiInDTO(genereRepository.save(genere));
    }

    public void eliminaGenere(Long id) {
        if (!genereRepository.existsById(id)) {
            throw new IllegalArgumentException("Genere non trovato.");
        }
        try {
            genereRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Impossibile eliminare: il genere è associato a dei film in catalogo.");
        }
    }

    private GenereDTO convertiInDTO(Genere genere) {
        GenereDTO dto = new GenereDTO();
        dto.setId(genere.getId());
        dto.setNome(genere.getNome());
        return dto;
    }
}
