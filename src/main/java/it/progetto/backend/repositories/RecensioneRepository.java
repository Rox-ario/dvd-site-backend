package it.progetto.backend.repositories;

import it.progetto.backend.entities.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {
    List<Recensione> findByFilmIdOrderByDataCreazioneDesc(Long filmId);
}
