package it.progetto.backend.repositories;

import it.progetto.backend.entities.Genere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenereRepository extends JpaRepository<Genere, Long>
{
    boolean existsByNomeIgnoreCase(String nome);
    // Ricerca solo per nome, essendo l'unico campo disponibile
    List<Genere> findByNomeContainingIgnoreCase(String nome);
}
