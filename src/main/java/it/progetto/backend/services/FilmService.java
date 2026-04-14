package it.progetto.backend.services;

import it.progetto.backend.DTOs.CreaFilmRequestDTO;
import it.progetto.backend.DTOs.FilmResponseDTO;
import it.progetto.backend.DTOs.RecensioneResponseDTO;
import it.progetto.backend.entities.*;
import it.progetto.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
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
    private final OrdineRepository ordineRepository;
    private final RecensioneRepository recensioneRepository;
    private final ClienteRepository clienteRepository;

    private static final String DEFAULT_COVER_URL = "https://via.placeholder.com/300x450.png?text=Copertina+Non+Disponibile";

    public List<FilmResponseDTO> ricercaAvanzata(String titolo, String nomeGenere, String nomeAttore, String nomeRegista, Integer anno, BigDecimal prezzoMax)
    {
        List<Film> filmTrovati = filmRepository.ricercaAvanzataParametrica(titolo, nomeGenere, nomeAttore, nomeRegista, anno, prezzoMax);
        return filmTrovati.stream().map(this::convertiInDTO).collect(Collectors.toList());
    }

    public FilmResponseDTO ottieniFilmPerId(Long id, String emailUtente)
    {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + id));

        FilmResponseDTO dto = convertiInDTO(film);

        // Di default è false. Verifichiamo se possiamo abilitarlo.
        dto.setPuoRecensire(false);

        if (emailUtente != null) {
            // 1. Verifichiamo se il cliente ha comprato il film e se gli è stato consegnato
            boolean haRicevutoIlFilm = ordineRepository.hasClienteAcquistatoFilm(emailUtente, id);

            // 2. Verifichiamo che non abbia GIA' lasciato una recensione
            boolean haGiaRecensito = film.getRecensioni().stream()
                    .anyMatch(r -> r.getCliente().getEmail().equals(emailUtente));

            // Può recensire solo se lo ha ricevuto e non lo ha mai recensito
            if (haRicevutoIlFilm && !haGiaRecensito) {
                dto.setPuoRecensire(true);
            }
        }

        return dto;
    }

    @Transactional
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
        nuovoFilm.setUrlImmagine(request.getUrlImmagine());

        assegnaUrlImmagine(nuovoFilm, request.getUrlImmagine());

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

        return convertiInDTO(filmRepository.save(nuovoFilm));
    }

    @Transactional
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
        filmEsistente.setUrlImmagine(request.getUrlImmagine());

        //GENERI
        filmEsistente.setGeneri(request.getIdGeneri() != null ?
                request.getIdGeneri().stream()
                        .map(idGenere -> genereRepository.findById(idGenere)
                                .orElseThrow(() -> new RuntimeException("Impossibile aggiornare: Genere con ID " + idGenere + " non trovato!")))
                        .collect(Collectors.toSet())
                : new HashSet<>());

        //ATTORI
        filmEsistente.setAttori(request.getIdAttori() != null ?
                request.getIdAttori().stream()
                        .map(idAttore -> attoreRepository.findById(idAttore)
                                /*se la scatola Optional<Attore> contiene l'attore, lo estrae.
                                se è vuota, blocca tutto e lancia un'eccezione chiara.*/
                                .orElseThrow(() -> new RuntimeException("Impossibile creare il film: Attore con ID " + idAttore + " non trovato!")))
                        .collect(Collectors.toSet())
                : new HashSet<>());

        //Regista
        filmEsistente.setRegisti(request.getIdRegisti() != null ?
                request.getIdRegisti().stream()
                        .map(idRegista -> registaRepository.findById(idRegista)
                                /*se la scatola Optional<Regista> contiene il regista, lo estrae.
                                se è vuota, blocca tutto e lancia un'eccezione chiara.*/
                                .orElseThrow(() -> new RuntimeException("Impossibile creare il film: Regista con ID " + idRegista + " non trovato!")))
                        .collect(Collectors.toSet())
                : new HashSet<>());

        return convertiInDTO(filmRepository.save(filmEsistente));
    }

    @Transactional
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
        dto.setUrlImmagine(film.getUrlImmagine());

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

        List<Recensione> recensioni = film.getRecensioni();
        if (recensioni != null && !recensioni.isEmpty())
        {
            dto.setRecensioni(recensioni.stream()
                    .map(this::getRecensioneResponseDTO)
                    .collect(Collectors.toList()));
        }
        else
        {
            dto.setRecensioni(Collections.emptyList());
        }
        return dto;
    }

    private void assegnaUrlImmagine(Film film, String urlFornito)
    {
        if (urlFornito == null || urlFornito.trim().isEmpty())
        {
            film.setUrlImmagine(DEFAULT_COVER_URL);
        }
        else
        {
            film.setUrlImmagine(urlFornito.trim());
        }
    }

    @Transactional(readOnly = true)
    public List<FilmResponseDTO> ottieniFilmSimili(Long id) {
        Film filmPrincipale = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film non trovato."));

        Set<Genere> generiDelFilm = filmPrincipale.getGeneri();

        if (generiDelFilm == null || generiDelFilm.isEmpty()) {
            return Collections.emptyList();
        }

        //PageRequest.of(0, 5) applica il limite SQL (LIMIT 5) direttamente al database
        List<Film> filmSimili = filmRepository.trovaFilmSimiliPerGeneri(generiDelFilm, id, PageRequest.of(0, 5));

        return filmSimili.stream().map(this::convertiInDTO).collect(Collectors.toList());
    }

    @Transactional
    public RecensioneResponseDTO aggiungiRecensione(Long idFilm, String emailCliente, int stelle, String commento) {
        Film film = filmRepository.findById(idFilm)
                .orElseThrow(() -> new RuntimeException("Film non trovato"));
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new RuntimeException("Cliente non trovato"));

        Recensione nuova = Recensione.builder()
                .stelle(stelle)
                .commento(commento)
                .film(film)
                .cliente(cliente)
                .dataCreazione(LocalDateTime.now())
                .build();

        recensioneRepository.save(nuova);
        return getRecensioneResponseDTO(nuova);
    }

    @Transactional
    public RecensioneResponseDTO modificaRecensione(Long idRecensione, String emailCliente, int stelle, String commento) {
        Recensione recensione = recensioneRepository.findById(idRecensione)
                .orElseThrow(() -> new RuntimeException("Recensione non trovata."));

        if (!recensione.getCliente().getEmail().equals(emailCliente)) {
            throw new RuntimeException("Non sei autorizzato a modificare questa recensione.");
        }

        recensione.setStelle(stelle);
        recensione.setCommento(commento);

        return getRecensioneResponseDTO(recensione);
    }

    @NonNull
    private RecensioneResponseDTO getRecensioneResponseDTO(Recensione recensione) {
        RecensioneResponseDTO dto = new RecensioneResponseDTO();
        dto.setId(recensione.getId());
        dto.setEmailCliente(recensione.getCliente().getEmail());
        dto.setNomeCliente(recensione.getCliente().getNome());
        dto.setStelle(recensione.getStelle());
        dto.setCommento(recensione.getCommento());
        dto.setData(recensione.getDataCreazione());
        return dto;
    }

    @Transactional
    public void eliminaRecensione(Long idRecensione, String emailRichiedente)
    {
        Recensione recensione = recensioneRepository.findById(idRecensione)
                .orElseThrow(() -> new RuntimeException("Impossibile eliminare: recensione non trovata."));

        //Controllo se l'utente loggato ha il ruolo di amministratore
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        //Se non è admin e l'email non coincide con quella del creatore, blocchiamo l'operazione
        if (!isAdmin && !recensione.getCliente().getEmail().equals(emailRichiedente)) {
            throw new RuntimeException("Azione non consentita: puoi eliminare solo le tue recensioni.");
        }

        recensioneRepository.delete(recensione);
    }
}

