package it.progetto.backend.services;

import it.progetto.backend.DTOs.CreaFilmRequestDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.entities.Attore;
import it.progetto.backend.entities.Film;
import it.progetto.backend.entities.Genere;
import it.progetto.backend.entities.Regista;
import it.progetto.backend.repositories.AttoreRepository;
import it.progetto.backend.repositories.FilmRepository;
import it.progetto.backend.repositories.GenereRepository;
import it.progetto.backend.repositories.RegistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService
{
    private final FilmRepository filmRepository;
    private final AttoreRepository attoreRepository;
    private final GenereRepository genereRepository;
    private final RegistaRepository registaRepository;

    public List<FilmResponseDTO> ricercaAvanzata(String titolo, String nomeGenere, String nomeAttore, String nomeRegista)
    {
        List<Film> filmTrovati = filmRepository.ricercaAvanzataParametrica(titolo, nomeGenere, nomeAttore, nomeRegista);
        return filmTrovati.stream().map(this::convertiInDTO).collect(Collectors.toList());
    }

    public FilmResponseDTO ottieniFilmPerId(Long id)
    {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + id));
        return convertiInDTO(film);
    }

    public FilmResponseDTO creaFilm(CreaFilmRequestDTO request)
    {
        if (filmRepository.existsByTitoloIgnoreCase(request.getTitolo()))
        {
            throw new RuntimeException("Errore logico: Un film con questo titolo esiste già nel catalogo.");
        }

        Film nuovoFilm = new Film();
        nuovoFilm.setTitolo(request.getTitolo());
        nuovoFilm.setAnno(request.getAnno());
        nuovoFilm.setDurataMinuti(request.getDurataMinuti());
        nuovoFilm.setTrama(request.getTrama());
        nuovoFilm.setPrezzo(request.getPrezzo());
        nuovoFilm.setStock(request.getStock());
        nuovoFilm.setIsAttivo(true);

        //GENERI
        nuovoFilm.setGeneri(request.getIdGeneri() != null ?
                request.getIdGeneri().stream()
                .map(id -> genereRepository.findById(id)
                        /*se la scatola Optional<Attore> contiene il genere, lo estrae.
                        se è vuota, blocca tutto e lancia un'eccezione chiara.*/
                        .orElseThrow(() -> new RuntimeException("Impossibile creare il film: Genere con ID " + id + " non trovato!")))
                .collect(Collectors.toSet())
                : new HashSet<>());

        //ATTORI
        nuovoFilm.setAttori(request.getIdAttori() != null ?
                request.getIdAttori().stream()
                        .map(id -> attoreRepository.findById(id)
                                /*se la scatola Optional<Attore> contiene l'attore, lo estrae.
                                se è vuota, blocca tutto e lancia un'eccezione chiara.*/
                                .orElseThrow(() -> new RuntimeException("Impossibile creare il film: Attore con ID " + id + " non trovato!")))
                        .collect(Collectors.toSet())
                : new HashSet<>());

        //Regista
        nuovoFilm.setRegisti(request.getIdRegisti() != null ?
                request.getIdRegisti().stream()
                        .map(id -> registaRepository.findById(id)
                                /*se la scatola Optional<Regista> contiene il regista, lo estrae.
                                se è vuota, blocca tutto e lancia un'eccezione chiara.*/
                                .orElseThrow(() -> new RuntimeException("Impossibile creare il film: Regista con ID " + id + " non trovato!")))
                        .collect(Collectors.toSet())
                : new HashSet<>());

        Film salvato = filmRepository.save(nuovoFilm);
        return convertiInDTO(salvato);
    }

    public FilmResponseDTO aggiornaFilm(Long id, CreaFilmRequestDTO request)
    {
        Film filmEsistente = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film non trovato."));

        filmEsistente.setTitolo(request.getTitolo());
        filmEsistente.setAnno(request.getAnno());
        filmEsistente.setDurataMinuti(request.getDurataMinuti());
        filmEsistente.setTrama(request.getTrama());
        filmEsistente.setPrezzo(request.getPrezzo());
        filmEsistente.setStock(request.getStock());
        filmEsistente.setIsAttivo(true);

        //GENERI
        filmEsistente.setGeneri(request.getIdGeneri() != null ?
                request.getIdGeneri().stream()
                        .map(ID -> genereRepository.findById(ID)
                                /*se la scatola Optional<Attore> contiene il genere, lo estrae.
                                se è vuota, blocca tutto e lancia un'eccezione chiara.*/
                                .orElseThrow(() -> new RuntimeException("Impossibile creare il film: Genere con ID " + id + " non trovato!")))
                        .collect(Collectors.toSet())
                : new HashSet<>());

        //ATTORI
        filmEsistente.setAttori(request.getIdAttori() != null ?
                request.getIdAttori().stream()
                        .map(ID -> attoreRepository.findById(ID)
                                /*se la scatola Optional<Attore> contiene l'attore, lo estrae.
                                se è vuota, blocca tutto e lancia un'eccezione chiara.*/
                                .orElseThrow(() -> new RuntimeException("Impossibile creare il film: Attore con ID " + id + " non trovato!")))
                        .collect(Collectors.toSet())
                : new HashSet<>());

        //Regista
        filmEsistente.setRegisti(request.getIdRegisti() != null ?
                request.getIdRegisti().stream()
                        .map(ID -> registaRepository.findById(ID)
                                /*se la scatola Optional<Regista> contiene il regista, lo estrae.
                                se è vuota, blocca tutto e lancia un'eccezione chiara.*/
                                .orElseThrow(() -> new RuntimeException("Impossibile creare il film: Regista con ID " + id + " non trovato!")))
                        .collect(Collectors.toSet())
                : new HashSet<>());

        return convertiInDTO(filmRepository.save(filmEsistente));
    }

    public void eliminaFilm(Long id)
    {
        Film filmDaEliminare = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Impossibile eliminare: Film non trovato."));

        filmDaEliminare.setIsAttivo(false);

        filmRepository.save(filmDaEliminare);
    }

    public FilmResponseDTO convertiInDTO(Film film) //public così lo utilizzo anche in altri service
    {
        FilmResponseDTO dto = new FilmResponseDTO();
        dto.setIdFilm(film.getId());
        dto.setTitolo(film.getTitolo());
        dto.setTrama(film.getTrama());
        dto.setPrezzo(film.getPrezzo());
        dto.setAnno(film.getAnno());
        dto.setDurataMinuti(film.getDurataMinuti());
        dto.setStock(film.getStock());
        dto.setAttivo(film.getIsAttivo());

        Set<Genere> generi = film.getGeneri();
        if (generi != null)
        {
            dto.setGenere(generi.stream().map(Genere::getNome).collect(Collectors.toList()));
        }
        Set<Regista> registi = film.getRegisti();
        if (registi != null)
        {
            dto.setRegisti(registi.stream()
                    .map(r -> r.getNome() + " " + r.getCognome())
                    .collect(Collectors.toList()));
        }
        Set<Attore> attori = film.getAttori();
        if (attori != null)        {
            dto.setAttori(attori.stream()
                    .map(a -> a.getNome() + " " + a.getCognome())
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}

