package it.progetto.backend.services;

import it.progetto.backend.entities.Film;
import it.progetto.backend.repositories.FilmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

   @Transactional
    public Film salvaDvd(Film film)
   {
        if (film.getPrezzo() == null || film.getPrezzo().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new IllegalArgumentException("Errore Logico: Il prezzo del DVD deve essere maggiore di zero.");
        }

        if (film.getStock() == null || film.getStock() < 0)
        {
            throw new IllegalArgumentException("Errore Logico: Le scorte non possono essere negative.");
        }

        return filmRepository.save(film);
    }

    @Transactional
    public void ritiraDalCommercio(Long idFilm)
    {
        Film filmDaRitirare = filmRepository.findById(idFilm)
                .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + idFilm));

        filmDaRitirare.setIsAttivo(false);

        filmRepository.save(filmDaRitirare);
    }

    public List<Film> ottieniFilmAttivi()
    {
        return filmRepository.findByIsAttivoTrueOrderByAnnoDesc();
    }

    public List<Film> cercaFilmAttiviPerTitolo(String parolaChiave)
    {
        return filmRepository.findByTitoloContainingIgnoreCaseAndIsAttivoTrue(parolaChiave);
    }

    public List<Film> cercaFilmAttiviPerAnno(Integer anno)
    {
        return filmRepository.findByAnnoAndIsAttivoTrue(anno);
    }

    public List<Film> cercaFilmAttiviPerAttore(String nome, String cognome)
    {
        return filmRepository.findByAttoreAndIsAttivoTrue(nome, cognome);
    }

    public List<Film> cercaFilmAttiviPerRegista(String nome, String cognome)
    {
        return filmRepository.findByRegistaAndIsAttivoTrue(nome, cognome);
    }

    public List<Film> cercaFilmAttiviPerGenere(String nomeGenere)
    {
        return filmRepository.findByGenereAndIsAttivoTrue(nomeGenere);
    }

    public List<Film> cercaFilmPerTitolo(String parolaChiave)
    {
        return filmRepository.findByTitoloContainingIgnoreCase(parolaChiave);
    }

    public List<Film> cercaFilmPerAnno(Integer anno)
    {
        return filmRepository.findByAnno(anno);
    }

    public List<Film> cercaFilmPerAttore(String nome, String cognome)
    {
        return filmRepository.findByAttore(nome, cognome);
    }

    public List<Film> cercaFilmPerRegista(String nome, String cognome)
    {
        return filmRepository.findByRegista(nome, cognome);
    }

    public List<Film> cercaFilmPerGenere(String nomeGenere)
    {
        return filmRepository.findByGenere(nomeGenere);
    }

    public List<Film> generaReportScorteInEsaurimento(Integer soglia)
    {
        return filmRepository.findByStockLessThanEqual(soglia);
    }
}

