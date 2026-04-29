package it.progetto.backend.repositories;

import it.progetto.backend.entities.Recensione;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    Page<Recensione> findByFilmIdOrderByDataCreazioneDesc(Long filmId, Pageable pageable);

    boolean existsByFilmIdAndClienteEmail(Long filmId, String email);
}
