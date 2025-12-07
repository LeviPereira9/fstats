package lp.edu.fstats.model.probability;

import jakarta.persistence.*;
import lombok.Data;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.match.Match;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_Probabilidade")
@Data
public class Probability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Probabilidade")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Partida")
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Competicao")
    private Competition competition;

    @Column(name = "Vl_Probabilidade_5")
    private BigDecimal probabilityOver05;
    @Column(name = "Vl_Probabilidade_15")
    private BigDecimal probabilityOver15;
    @Column(name = "Vl_Probabilidade_25")
    private BigDecimal probabilityOver25;

    @Column(name = "Nr_Rodada")
    private Integer matchDay;

}
