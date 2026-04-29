package it.progetto.backend.repositories;

import it.progetto.backend.entities.Film;
import it.progetto.backend.entities.Genere;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long>
{
    boolean existsByTitoloIgnoreCase(String titolo);

    @Query("SELECT DISTINCT f FROM Film f " +
            "LEFT JOIN f.generi g " +
            "LEFT JOIN f.attori a " +
            "LEFT JOIN f.registi r " +
            "WHERE f.isAttivo = true " +
            "AND (:titolo IS NULL OR LOWER(f.titolo) LIKE LOWER(CONCAT('%', :titolo, '%'))) " +
            "AND (:nomeGenere IS NULL OR LOWER(g.nome) = LOWER(:nomeGenere)) " +
            "AND (:nomeAttore IS NULL OR LOWER(CONCAT(a.nome, ' ', a.cognome)) = LOWER(:nomeAttore)) " +
            "AND (:nomeRegista IS NULL OR LOWER(CONCAT(r.nome, ' ', r.cognome)) = LOWER(:nomeRegista)) " +
            "AND (:anno IS NULL OR f.anno = :anno) " +
            "AND (:prezzoMax IS NULL OR f.prezzo <= :prezzoMax)")
    Page<Film> ricercaAvanzataParametrica(
            @Param("titolo") String titolo,
            @Param("nomeGenere") String nomeGenere,
            @Param("nomeAttore") String nomeAttore,
            @Param("nomeRegista") String nomeRegista,
            @Param("anno") Integer anno,
            @Param("prezzoMax") BigDecimal prezzoMax,
            Pageable pageable
    );

    @Query("SELECT DISTINCT f FROM Film f JOIN f.generi g WHERE g IN :generi AND f.id <> :filmId AND f.isAttivo = true")
    List<Film> trovaFilmSimiliPerGeneri(@Param("generi") Set<Genere> generi, @Param("filmId") Long filmId, Pageable pageable);
}
