package it.progetto.backend.repositories;

import it.progetto.backend.entities.Attore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttoreRepository extends JpaRepository<Attore, Long>
{
    //ritorna un booleano per il controllo esatto dei duplicati
    boolean existsByNomeIgnoreCaseAndCognomeIgnoreCase(String nome, String cognome);

    //ritorna una LISTA per le ricerche parziali
    List<Attore> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome);
}
