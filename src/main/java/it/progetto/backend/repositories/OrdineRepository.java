package it.progetto.backend.repositories;

import it.progetto.backend.entities.Ordine;
import it.progetto.backend.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long>
{
    List<Ordine> findByClienteEmailOrderByDataAcquistoDesc(String cliente_email);

    List<Ordine> findByStato(StatoOrdine stato);
}
