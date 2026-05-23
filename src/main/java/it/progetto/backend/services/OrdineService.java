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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdineService
{

    private final OrdineRepository ordineRepository;
    private final FilmRepository filmRepository;
    private final ClienteRepository clienteRepository;
    private final EmailService emailService;

    @Transactional
    public OrdineResponseDTO elaboraAcquisto(String email, CreaOrdineRequest richiesta)
    {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Cliente non riconosciuto. Impossibile procedere."));

        if (richiesta.getArticoli() == null || richiesta.getArticoli().isEmpty())
        {
            throw new IllegalArgumentException("Il carrello è vuoto.");
        }

        if (richiesta.getIndirizzoSpedizione() == null || richiesta.getIndirizzoSpedizione().isBlank())
        {
            throw new IllegalArgumentException("L'indirizzo di spedizione è obbligatorio per procedere.");
        }

        Ordine nuovoOrdine = Ordine.builder()
                .cliente(cliente)
                .dataAcquisto(LocalDateTime.now())
                .stato(StatoOrdine.IN_ELABORAZIONE)
                .totale(BigDecimal.ZERO)
                .indirizzoSpedizione(richiesta.getIndirizzoSpedizione())
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
                    .indirizzoSpedizione(richiesta.getIndirizzoSpedizione())
                    .build();

            nuovoOrdine.getRighe().add(rigaReale);

            film.setStock(film.getStock() - rigaDto.getQuantita());
            //Optimistic Lock
            try {
                filmRepository.saveAndFlush(film);
            } catch (ObjectOptimisticLockingFailureException e) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Spiacenti! L'ultima copia di '" + film.getTitolo() + "' è stata appena acquistata da un altro utente."
                );
            }

            BigDecimal costoParziale = rigaReale.getPrezzoAcquisto().multiply(new BigDecimal(rigaDto.getQuantita()));
            totaleScontrino = totaleScontrino.add(costoParziale);
        }

        nuovoOrdine.setTotale(totaleScontrino);

        Ordine nuovoOrdineSalvato = ordineRepository.save(nuovoOrdine);
        return convertiInDTO(nuovoOrdineSalvato);
    }

    public Page<OrdineResponseDTO> ottieniStoricoCliente(String email, Pageable pageable)
    {
        Page<Ordine> ordiniReali = ordineRepository.findByClienteEmailOrderByDataAcquistoDesc(email, pageable);
        return ordiniReali.map(this::convertiInDTO);
    }

    public Page<OrdineResponseDTO> ottieniTuttiGliOrdini(String stato, Pageable pageable)
    {
        Page<Ordine> ordini;

        if (stato != null && !stato.isBlank()) {
            try {
                //Tenta la conversione sicura della stringa nell'Enum
                StatoOrdine statoEnum = StatoOrdine.valueOf(stato.toUpperCase());
                ordini = ordineRepository.findByStato(statoEnum, pageable);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Stato ordine non valido: " + stato);
            }
        } else {
            //Se non passo nessuno stato, li recupero tutti
            ordini = ordineRepository.findAll(pageable);
        }

        return ordini.map(this::convertiInDTO);
    }

    @Transactional
    public OrdineResponseDTO aggiornaStatoOrdine(Long idOrdine, String nuovoStato)
    {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new IllegalArgumentException("Ordine con ID " + idOrdine + " non trovato."));

        try {
            StatoOrdine nuovoStatoEnum = StatoOrdine.valueOf(nuovoStato.toUpperCase());
            StatoOrdine vecchioStatoEnum = ordine.getStato();

            if (nuovoStatoEnum == StatoOrdine.ANNULLATO && vecchioStatoEnum != StatoOrdine.ANNULLATO) {

                for (RigaOrdine riga : ordine.getRighe()) {
                    Film film = riga.getFilm();
                    film.setStock(film.getStock() + riga.getQuantita());
                    filmRepository.save(film);
                }

                emailService.inviaNotificaRimborso(
                        ordine.getCliente().getEmail(),
                        ordine.getCliente().getNome(),
                        ordine.getId(),
                        ordine.getTotale()
                );
            }

            ordine.setStato(nuovoStatoEnum);
            Ordine ordineAggiornato = ordineRepository.save(ordine);

            if(nuovoStatoEnum == StatoOrdine.SPEDITO)
            {
                emailService.inviaNotificaSpedizione(
                        ordine.getCliente().getEmail(),
                        ordine.getCliente().getNome(),
                        ordine.getId(),
                        ordine.getIndirizzoSpedizione()
                );
            }
            if(nuovoStatoEnum == StatoOrdine.CONSEGNATO)
            {
                emailService.inviaNotificaConsegna(
                        ordine.getCliente().getEmail(),
                        ordine.getCliente().getNome(),
                        ordine.getId(),
                        ordine.getIndirizzoSpedizione()
                );
            }
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

        dto.setIndirizzoSpedizione(ordine.getIndirizzoSpedizione());
        dto.setRighe(righeDto);
        return dto;
    }

    @Transactional
    public OrdineResponseDTO annullaMioOrdine(String emailCliente, Long idOrdine)
    {
        Ordine ordine = ordineRepository.findById(idOrdine)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato."));

        if (!ordine.getCliente().getEmail().equals(emailCliente)) {
            throw new IllegalStateException("Non sei autorizzato a modificare questo ordine.");
        }

        if (ordine.getStato() != StatoOrdine.IN_ELABORAZIONE) {
            throw new IllegalStateException("Impossibile annullare l'ordine: è già stato spedito o elaborato. Contatta l'assistenza per un reso.");
        }

        for (RigaOrdine riga : ordine.getRighe()) {
            Film film = riga.getFilm();
            film.setStock(film.getStock() + riga.getQuantita());
            filmRepository.save(film);
        }

        ordine.setStato(StatoOrdine.ANNULLATO);
        Ordine ordineAnnullato = ordineRepository.save(ordine);

        emailService.inviaNotificaRimborso(
                ordine.getCliente().getEmail(),
                ordine.getCliente().getNome(),
                ordine.getId(),
                ordine.getTotale()
        );

        return convertiInDTO(ordineAnnullato);
    }
}
