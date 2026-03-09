package it.progetto.backend.repositories;

import it.progetto.backend.entities.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long>
{
    List<Film> findByAnnoAndIsAttivoTrue(Integer anno);

    List<Film> findByAnno(Integer anno);

    //per restituire tutti i film con un dato attore, anche quelli non disponibili
    @Query("SELECT f FROM Film f JOIN f.attori a WHERE a.nome = :nome AND a.cognome = :cognome")
    List<Film> findByAttore(@Param("nome") String nomeAttore, @Param("cognome") String cognomeAttore);

    @Query("SELECT f FROM Film f JOIN f.attori a WHERE a.nome = :nome AND a.cognome = :cognome AND f.isAttivo = true")
    List<Film> findByAttoreAndIsAttivoTrue(@Param("nome") String nome, @Param("cognome") String cognome);

    @Query("SELECT f FROM Film f JOIN f.registi r WHERE r.nome = :nome AND r.cognome = :cognome")
    List<Film> findByRegista(@Param("nome") String nomeRegista, @Param("cognome") String cognomeRegista);

    @Query("SELECT f FROM Film f JOIN f.registi r WHERE r.nome = :nome AND r.cognome = :cognome AND f.isAttivo = true")
    List<Film> findByRegistaAndIsAttivoTrue(@Param("nome") String nomeRegista, @Param("cognome") String cognomeRegista);

    List<Film> findByTitoloContainingIgnoreCase(String parolaChiave);

    List<Film> findByTitoloContainingIgnoreCaseAndIsAttivoTrue(String parolaChiave);

    List<Film> findByIsAttivoTrueOrderByAnnoDesc();

    List<Film> findByStockLessThanEqual(Integer sogliaStock);

    @Query("SELECT f FROM Film f JOIN f.generi g WHERE g.nome = :nomeGenere AND f.isAttivo = true")
    List<Film> findByGenereAndIsAttivoTrue(@Param("nomeGenere") String nomeGenere);

    @Query("SELECT f FROM Film f JOIN f.generi g WHERE g.nome = :nomeGenere")
    List<Film> findByGenere(@Param("nomeGenere") String nomeGenere);
}
