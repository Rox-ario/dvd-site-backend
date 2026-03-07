package it.progetto.backend.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Regista
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;
}
