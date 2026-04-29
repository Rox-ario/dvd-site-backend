package it.progetto.backend.repositories;

import it.progetto.backend.entities.Genere;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenereRepository extends JpaRepository<Genere, Long>
{
    boolean existsByNomeIgnoreCase(String nome);
    // Ricerca solo per nome, essendo l'unico campo disponibile
    Page<Genere> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
