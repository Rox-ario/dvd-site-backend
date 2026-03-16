package it.progetto.backend.services;
import it.progetto.backend.entities.Attore;
import it.progetto.backend.entities.Genere;
import it.progetto.backend.repositories.AttoreRepository;
import it.progetto.backend.repositories.GenereRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//ha un ruolo di ricerca o creazione dell'entity Attore
@Service
@AllArgsConstructor
public class AttoreService
{
    private final AttoreRepository attoreRepository;

    @Transactional
    public Attore ottieniOAggiungiAttore(String nomeAttore, String cognomeAttore)
    {
        if (nomeAttore == null || nomeAttore.trim().isEmpty() ||  cognomeAttore == null || cognomeAttore.trim().isEmpty())
        {
            throw new IllegalArgumentException("Il nome o cognome dell'attore non può essere vuoto.");
        }

        return attoreRepository.findByNomeIgnoreCaseAndCognomeIgnoreCase(nomeAttore, cognomeAttore)
                .orElseGet(() -> {
                    Attore nuovoGenere = Attore.builder()
                                    .nome(nomeAttore.trim())
                                    .cognome(cognomeAttore.trim())
                                    .build();
                    return attoreRepository.save(nuovoGenere);
                });
    }

    public List<Attore> ottieniTuttiGliAttori()
    {
        return attoreRepository.findAll();
    }
}
