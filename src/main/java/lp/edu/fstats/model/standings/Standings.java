package lp.edu.fstats.model.standings;

import jakarta.persistence.*;
import lombok.Data;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.team.Team;

@Entity
@Table(name = "TB_Classificacao")
@Data
public class Standings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Classificacao")
    private Long id;

    @Column(name = "Ds_Tipo")
    private String type = "TOTAL";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Time")
    private Team team;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Competicao")
    private Competition competition;

    @Column(name = "Nr_Posicao")
    private Integer position;
    @Column(name = "Qt_PartidasJogadas")
    private Integer playedGames;
    @Column(name = "Ds_FormaAtual")
    private String form;
    @Column(name = "Qt_Vitoria")
    private Integer won;
    @Column(name = "Qt_Empate")
    private Integer draw;
    @Column(name = "Qt_Derrota")
    private Integer lost;
    @Column(name = "Qt_Pontos")
    private Integer points;
    @Column(name = "Qt_GolsPro")
    private Integer goalsFor;
    @Column(name = "Qt_GolsContra")
    private Integer goalsAgainst;
    @Column(name = "Qt_SaldoGols")
    private Integer goalDifference;

    public Long getTeamExternalId(){
        return team.getExternalId();
    }
}
