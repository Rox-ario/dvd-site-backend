package it.progetto.backend.repositories;

import it.progetto.backend.entities.Ordine;
import it.progetto.backend.enums.StatoOrdine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long>
{
    Page<Ordine> findByClienteEmailOrderByDataAcquistoDesc(String cliente_email, Pageable pageable);

    Page<Ordine> findByStato(StatoOrdine stato, Pageable pageable);

    @Query("SELECT COUNT(ro) > 0 FROM RigaOrdine ro WHERE ro.ordine.cliente.email = :email AND ro.film.id = :idFilm AND ro.ordine.stato = 'CONSEGNATO'")
    boolean hasClienteAcquistatoFilm(@Param("email") String email, @Param("idFilm") Long idFilm);
}
