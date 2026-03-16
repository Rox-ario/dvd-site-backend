package it.progetto.backend.services;
import it.progetto.backend.entities.Genere;
import it.progetto.backend.repositories.GenereRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//ha un ruolo di ricerca o creazione dell'entity Genere
@Service
public class GenereService
{
    private final GenereRepository genereRepository;

    public GenereService(GenereRepository genereRepository)
    {
        this.genereRepository = genereRepository;
    }

    @Transactional
    public Genere ottieniOCreaGenere(String nomeGenere)
    {
        if (nomeGenere == null || nomeGenere.trim().isEmpty())
        {
            throw new IllegalArgumentException("Il nome del genere non può essere vuoto.");
        }

        return genereRepository.findByNomeIgnoreCase(nomeGenere.trim())
                .orElseGet(() -> {
                    Genere nuovoGenere = new Genere();
                    nuovoGenere.setNome(nomeGenere.trim());
                    return genereRepository.save(nuovoGenere);
                });
    }

    public List<Genere> ottieniTuttiIGeneri()
    {
        return genereRepository.findAll();
    }
}
