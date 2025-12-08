package lp.edu.fstats.model.competition;

import jakarta.persistence.*;
import lombok.Data;
import lp.edu.fstats.model.team.Team;

@Entity
@Table(name = "TB_TimeCompeticao")
@Data
public class CompetitionTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TimeCompeticao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Competicao")
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Time")
    private Team team;
}
