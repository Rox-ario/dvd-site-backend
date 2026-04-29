package it.progetto.backend.repositories;

import it.progetto.backend.entities.Attore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttoreRepository extends JpaRepository<Attore, Long>
{
    boolean existsByNomeIgnoreCaseAndCognomeIgnoreCase(String nome, String cognome);

    Page<Attore> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome, Pageable pageable);
}
