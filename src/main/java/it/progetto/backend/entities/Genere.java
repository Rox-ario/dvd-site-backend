package it.progetto.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "generi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genere
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;
}
