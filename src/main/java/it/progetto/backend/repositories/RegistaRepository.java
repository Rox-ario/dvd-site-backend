package it.progetto.backend.repositories;

import it.progetto.backend.entities.Regista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistaRepository extends JpaRepository<Regista, Long>
{
    boolean existsByNomeIgnoreCaseAndCognomeIgnoreCase(String nome, String cognome);
    List<Regista> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome);
}
