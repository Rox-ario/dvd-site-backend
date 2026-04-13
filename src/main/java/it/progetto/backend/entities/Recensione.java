package it.progetto.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recensioni")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recensione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer stelle; // 1-5

    @Column(columnDefinition = "TEXT")
    private String commento;

    @ManyToOne(optional = false)
    private Film film;

    @ManyToOne(optional = false)
    private Cliente cliente;

    private LocalDateTime dataCreazione;
}
