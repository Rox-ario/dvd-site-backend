package it.progetto.backend.services;
import it.progetto.backend.entities.Regista;
import it.progetto.backend.repositories.RegistaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//ha un ruolo di ricerca o creazione dell'entity regista
@Service
@AllArgsConstructor
public class RegistaService
{
    private final RegistaRepository registaRepository;

    @Transactional
    public Regista ottieniOAggiungiRegista(String nomeRegista, String cognomeRegista)
    {
        if (nomeRegista == null || nomeRegista.trim().isEmpty() ||  cognomeRegista == null || cognomeRegista.trim().isEmpty())
        {
            throw new IllegalArgumentException("Il nome o cognome del regista non può essere vuoto.");
        }

        return registaRepository.findByNomeIgnoreCaseAndCognomeIgnoreCase(nomeRegista, cognomeRegista)
                .orElseGet(() -> {
                    Regista nuovoRegista = Regista.builder()
                            .nome(nomeRegista.trim())
                            .cognome(cognomeRegista.trim())
                            .build();
                    return registaRepository.save(nuovoRegista);
                });
    }

    public List<Regista> ottieniTuttiIRegisti()
    {
        return registaRepository.findAll();
    }
}
