package it.progetto.backend.repositories;

import it.progetto.backend.entities.Genere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GenereRepository extends JpaRepository<Genere, Long>
{
    Optional<Genere> findByNomeIgnoreCase(String nome);
}
