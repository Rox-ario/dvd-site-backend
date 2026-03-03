package it.progetto.backend.entities;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "punti_fedelta", nullable = false)
    @Builder.Default
    private Integer puntiFedelta = 0;
}
