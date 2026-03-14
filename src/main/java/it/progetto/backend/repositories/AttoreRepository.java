package it.progetto.backend.repositories;

import it.progetto.backend.entities.Attore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AttoreRepository extends JpaRepository<Attore, Long>
{
    Optional<Attore> findByNomeIgnoreCaseAndCognomeIgnoreCase(String nome, String cognome);
}
