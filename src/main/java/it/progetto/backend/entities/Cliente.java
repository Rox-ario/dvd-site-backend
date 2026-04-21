package it.progetto.backend.entities;

import it.progetto.backend.enums.Ruolo;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clienti")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "film_preferiti",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id")
    )
    @Builder.Default
    private Set<Film> filmPreferiti = new HashSet<>();

    @Column(name = "indirizzo_spedizione")
    private String indirizzoSpedizione;
}
