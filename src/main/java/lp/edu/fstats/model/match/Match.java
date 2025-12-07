package lp.edu.fstats.model.match;

import jakarta.persistence.*;
import lombok.Data;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.probability.Probability;
import lp.edu.fstats.model.team.Team;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_Partida")
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Partida")
    private Long id;

    @Column(name = "ID_ExternoPartida")
    private Long externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Competicao")
    private Competition competition;

    @Column(name = "Dt_Partida")
    private LocalDateTime utcDate;

    @Column(name = "St_Partida")
    private String status;

    @Column(name = "Nr_Rodada")
    private Integer matchDay;

    @Column(name = "Ds_Estagio")
    private String stage;

    @ManyToOne
    @JoinColumn(name = "ID_TimeCasa")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "ID_TimeVisitante")
    private Team awayTeam;

    @Column(name = "Ds_Ganhador")
    private String winner;

    @Column(name = "Qt_GolTimeCasa")
    private Integer homeGoals = 0;
    @Column(name = "Qt_GolTimeVisitante")
    private Integer awayGoals = 0;

    @Column(name = "St_Ativo")
    private boolean active = true;

    @OneToOne(mappedBy = "match", fetch = FetchType.EAGER)
    private Probability probability;


}
