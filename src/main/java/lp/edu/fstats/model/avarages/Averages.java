package lp.edu.fstats.model.avarages;


import jakarta.persistence.*;
import lombok.Data;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.team.Team;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_MediasTime")
@Data
public class Averages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MediasTime")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Competicao")
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Time")
    private Team team;

    @Column(name = "Vl_MediaGolsProCasa")
    private BigDecimal avgGoalsForHome = BigDecimal.ZERO;
    @Column(name = "Vl_MediaGolsContraCasa")
    private BigDecimal avgGoalsAgainstHome = BigDecimal.ZERO;

    @Column(name = "Vl_MediaGolsProVisitante")
    private BigDecimal avgGoalsForAway = BigDecimal.ZERO;
    @Column(name = "Vl_MediaGolsContraVisitante")
    private BigDecimal avgGoalsAgainstAway = BigDecimal.ZERO;


    public Long getTeamId(){
        return team.getId();
    }
}

