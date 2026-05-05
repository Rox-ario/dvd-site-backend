package it.progetto.backend.services;

import it.progetto.backend.DTOs.*;
import it.progetto.backend.entities.*;
import it.progetto.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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

    public Page<FilmResponseDTO> ricercaAvanzata(String titolo, String nomeGenere, String nomeAttore, String nomeRegista, Integer anno, BigDecimal prezzoMax, Pageable pageable)
    {
        Page<Film> filmTrovati = filmRepository.ricercaAvanzataParametrica(titolo, nomeGenere, nomeAttore, nomeRegista, anno, prezzoMax, pageable);
        return filmTrovati.map(this::convertiInDTO);
    }

    public FilmResponseDTO ottieniFilmPerId(Long id, String emailUtente)
    {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + id));

        FilmResponseDTO dto = convertiInDTO(film);

        //Di default è false. Verifichiamo se possiamo abilitarlo.
        dto.setPuoRecensire(false);

        if (emailUtente != null) {
            boolean haRicevutoIlFilm = ordineRepository.hasClienteAcquistatoFilm(emailUtente, id);

            boolean haGiaRecensito = film.getRecensioni().stream()
                    .anyMatch(r -> r.getCliente().getEmail().equals(emailUtente));

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

        if (request.getCuriosita() != null && !request.getCuriosita().isEmpty()) {
            List<FunFact> facts = request.getCuriosita().stream()
                    .map(testo -> {
                        FunFact f = new FunFact();
                        f.setTesto(testo);
                        f.setFilm(nuovoFilm);
                        return f;
                    }).collect(Collectors.toList());
            nuovoFilm.setCuriosita(facts);
        }

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

        filmEsistente.getCuriosita().clear();
        if (request.getCuriosita() != null && !request.getCuriosita().isEmpty()) {
            List<FunFact> facts = request.getCuriosita().stream()
                    .map(testo -> {
                        FunFact f = new FunFact();
                        f.setTesto(testo);
                        f.setFilm(filmEsistente);
                        return f;
                    }).toList();
            filmEsistente.getCuriosita().addAll(facts);
        }

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
        if (film.getCuriosita() != null && !film.getCuriosita().isEmpty()) {
            dto.setCuriosita(film.getCuriosita().stream()
                    .map(FunFact::getTesto)
                    .collect(Collectors.toList()));
        } else {
            dto.setCuriosita(Collections.emptyList());
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

        boolean haRicevutoIlFilm = ordineRepository.hasClienteAcquistatoFilm(emailCliente, idFilm);
        if (!haRicevutoIlFilm) {
            throw new RuntimeException("Azione non consentita: devi aver acquistato e ricevuto il film per poterlo recensire.");
        }

        boolean haGiaRecensito = recensioneRepository.existsByFilmIdAndClienteEmail(idFilm, emailCliente);
        if (haGiaRecensito) {
            throw new RuntimeException("Hai già recensito questo film.");
        }

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
        recensioneRepository.save(recensione);

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

    @Transactional
    public void aggiungiFunFact(Long id, FunFactDTO funFact) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film non trovato."));

        FunFact nuovo = new FunFact();
        nuovo.setTesto(funFact.getTesto());
        nuovo.setFilm(film);

        film.getCuriosita().add(nuovo);

        filmRepository.save(film);
    }

    @Transactional(readOnly = true)
    public Page<RecensioneResponseDTO> getRecensioni(Long idFilm, Pageable pageable)
    {
        if (!filmRepository.existsById(idFilm)) {
            throw new RuntimeException("Film non trovato.");
        }

        Page<Recensione> recensioniPage = recensioneRepository.findByFilmIdOrderByDataCreazioneDesc(idFilm, pageable);

        return recensioniPage.map(this::getRecensioneResponseDTO);
    }

    public StatisticheRecensioniDTO ottieniStatisticheRecensioni(Long idFilm)
    {
        Map<Integer, Long> dist = new HashMap<>();
        // Inizializzo la mappa a 0 per tutte le stelle da 1 a 5
        for(int i = 1; i <= 5; i++) {
            dist.put(i, 0L);
        }

        List<Object[]> results = recensioneRepository.countDistribuzioneStelle(idFilm);
        long totale = 0;
        double somma = 0;

        for (Object[] row : results) {
            Integer stelle = (Integer) row[0];
            Long count = (Long) row[1];
            dist.put(stelle, count);
            totale += count;
            somma += (stelle * count);
        }

        StatisticheRecensioniDTO dto = new StatisticheRecensioniDTO();
        dto.setDistribuzione(dist);
        dto.setTotaleRecensioni((int) totale);
        dto.setMediaStelle(totale > 0 ? (somma / (double) totale) : 0.0);

        return dto;
    }
}

