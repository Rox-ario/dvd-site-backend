package it.progetto.backend.entities;

import it.progetto.backend.enums.StatoOrdine;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordini")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordine
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //molti ordini possono essere fatti da un solo Cliente
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    //un Ordine ha molte RigheOrdine.
    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RigaOrdine> righe = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime dataAcquisto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoOrdine stato;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totale;
}
