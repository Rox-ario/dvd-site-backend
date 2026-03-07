package it.progetto.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attori")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attore
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;
}
