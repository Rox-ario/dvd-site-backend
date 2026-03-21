package it.progetto.backend.services;

import it.progetto.backend.DTOs.CreaOrdineRequest;
import it.progetto.backend.DTOs.OrdineResponseDTO;
import it.progetto.backend.DTOs.RigaOrdineDTO;
import it.progetto.backend.DTOs.RigaOrdineResponseDTO;
import it.progetto.backend.entities.*;
import it.progetto.backend.enums.StatoOrdine;
import it.progetto.backend.repositories.ClienteRepository;
import it.progetto.backend.repositories.FilmRepository;
import it.progetto.backend.repositories.OrdineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdineService
{

    private final OrdineRepository ordineRepository;
    private final FilmRepository filmRepository;
    private final ClienteRepository clienteRepository;

    public OrdineService(OrdineRepository ordineRepository, FilmRepository filmRepository, ClienteRepository clienteRepository) {
        this.ordineRepository = ordineRepository;
        this.filmRepository = filmRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Ordine elaboraAcquisto(CreaOrdineRequest richiesta)
    {
        Cliente cliente = clienteRepository.findById(richiesta.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente non riconosciuto. Impossibile procedere."));

        if (richiesta.getArticoli() == null || richiesta.getArticoli().isEmpty())
        {
            throw new IllegalArgumentException("Il carrello è vuoto.");
        }

        Ordine nuovoOrdine = Ordine.builder()
                .cliente(cliente)
                .dataAcquisto(LocalDateTime.now())
                .stato(StatoOrdine.IN_ELABORAZIONE)
                .totale(BigDecimal.ZERO)
                .build();

        BigDecimal totaleScontrino = BigDecimal.ZERO;

        for (RigaOrdineDTO rigaDto : richiesta.getArticoli())
        {
            Long idFilm = rigaDto.getIdFilm();
            Film film = filmRepository.findById(idFilm)
                    .orElseThrow(() -> new IllegalArgumentException("Film con ID " + idFilm + " inesistente."));

            if (!film.getIsAttivo())
            {
                throw new IllegalStateException("Il film '" + film.getTitolo() + "' non è più disponibile per l'acquisto.");
            }

            if (film.getStock() < rigaDto.getQuantita())
            {
                throw new IllegalStateException("Copie insufficienti per '" + film.getTitolo());
            }

            RigaOrdine rigaReale = RigaOrdine.builder()
                    .ordine(nuovoOrdine) //collego la riga all'ordine
                    .film(film)
                    .quantita(rigaDto.getQuantita())
                    .prezzoAcquisto(film.getPrezzo()) //fotografo il prezzo così non cambia
                    .build();

            nuovoOrdine.getRighe().add(rigaReale);

            film.setStock(film.getStock() - rigaDto.getQuantita());
            filmRepository.save(film);

            BigDecimal costoParziale = rigaReale.getPrezzoAcquisto().multiply(new BigDecimal(rigaDto.getQuantita()));
            totaleScontrino = totaleScontrino.add(costoParziale);
        }

        nuovoOrdine.setTotale(totaleScontrino);

        return ordineRepository.save(nuovoOrdine);
    }

    public List<OrdineResponseDTO> ottieniStoricoCliente(Long idCliente)
    {
        List<Ordine> ordiniReali = ordineRepository.findByClienteIdOrderByDataAcquistoDesc(idCliente);
        return ordiniReali.stream().map(this::convertiInDTO).toList();
    }

    public List<OrdineResponseDTO> ottieniTuttiGliOrdini(String stato)
    {
        List<Ordine> ordini;

        if (stato != null && !stato.isBlank()) {
            try {
                //Tenta la conversione sicura della stringa nell'Enum
                StatoOrdine statoEnum = StatoOrdine.valueOf(stato.toUpperCase());
                ordini = ordineRepository.findByStato(statoEnum);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Stato ordine non valido: " + stato);
            }
        } else {
            //Se non passo nessuno stato, li recupero tutti
            ordini = ordineRepository.findAll();
        }

        return ordini.stream().map(this::convertiInDTO).toList();
    }

    public OrdineResponseDTO aggiornaStatoOrdine(Long idOrdine, String nuovoStato)
    {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new IllegalArgumentException("Ordine con ID " + idOrdine + " non trovato."));

        try {
            StatoOrdine statoEnum = StatoOrdine.valueOf(nuovoStato.toUpperCase());
            ordine.setStato(statoEnum);
            Ordine ordineAggiornato = ordineRepository.save(ordine);
            return convertiInDTO(ordineAggiornato);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Stato inesistente. Valori ammessi: IN_ELABORAZIONE, SPEDITO, CONSEGNATO, ANNULLATO");
        }
    }

    private OrdineResponseDTO convertiInDTO(Ordine ordine) {
        OrdineResponseDTO dto = new OrdineResponseDTO();
        dto.setNumeroOrdine(ordine.getId());
        dto.setDataAcquisto(ordine.getDataAcquisto());
        dto.setTotale(ordine.getTotale());
        dto.setStato(ordine.getStato().name());

        List<RigaOrdineResponseDTO> righeDto = ordine.getRighe().stream().map(riga -> {
            RigaOrdineResponseDTO rigaDto = new RigaOrdineResponseDTO();
            rigaDto.setTitoloFilm(riga.getFilm().getTitolo());
            rigaDto.setQuantita(riga.getQuantita());
            rigaDto.setPrezzoAcquisto(riga.getPrezzoAcquisto());
            return rigaDto;
        }).toList();

        dto.setRighe(righeDto);
        return dto;
    }


}
