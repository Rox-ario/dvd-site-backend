package it.progetto.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "righe_ordine")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RigaOrdine
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //molte righe possono appartenere a un solo Ordine
    @ManyToOne(optional = false)
    @JoinColumn(name = "ordine_id", nullable = false)
    private Ordine ordine;

    //molte righe (di ordini diversi) possono puntare allo stesso Film
    @ManyToOne(optional = false)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @Column(nullable = false)
    private Integer quantita;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoAcquisto;
    //se metto uno sconto, il prezzo di quest'ordine non cambia se è stato già effettuato
}
