package it.progetto.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "film")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titolo;

    private Integer anno;

    private Integer durataMinuti;

    @Column(columnDefinition = "TEXT") //per inserire trame più lunghe
    private String trama;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo;

    @Column(nullable = false)
    private Integer stock;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isAttivo = true;

    @ManyToMany
    @JoinTable(
            name = "film_generi",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genere_id")
    )
    @Builder.Default
    private Set<Genere> generi = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "film_attori",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "attore_id")
    )
    @Builder.Default
    private Set<Attore> attori = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "film_registi",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "regista_id")
    )
    @Builder.Default
    private Set<Regista> registi = new HashSet<>();

    private String urlImmagine;
}
