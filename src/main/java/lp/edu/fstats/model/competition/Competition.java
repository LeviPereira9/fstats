package lp.edu.fstats.model.competition;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "TB_Competicao")
@Data
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Competicao")
    private Long id;

    @Column(name = "ID_ExternoCompeticao")
    private Long externalId;

    @Column(name = "Nm_Competicao")
    private String name;
    @Column(name = "Ds_Codigo")
    private String code;
    @Column(name = "Ds_Tipo")
    private String type;
    @Column(name = "Ur_Emblema")
    private String emblem;

    @Column(name = "Qt_Partidas")
    private Integer count = 0;
    @Column(name = "Dt_Inicio")
    private LocalDate startDate;
    @Column(name = "Dt_Fim")
    private LocalDate endDate;

    @Column(name = "St_Ativo")
    private boolean active = true;

    @Column(name = "Nr_RodadaAtual")
    private Integer currentMatchDay = 1;

    @Column(name = "Nr_ExternoRodadaAtual")
    private Integer externalCurrentMatchDay = 1;

    @Column(name = "St_Competicao")
    private String status = "Em andamento";

    @Column(name = "Nr_UltimaRodadaFinalizada")
    private Integer lastFinishedMatchDay = 0;

    public void incrementMatchDay() {
        boolean alreadyHasFutureMatches = currentMatchDay.equals(externalCurrentMatchDay + 2);

        if(!alreadyHasFutureMatches) currentMatchDay += 1;;
    }

    public void incrementLastFinishedMatchDay() {
        boolean canIncrement = externalCurrentMatchDay.equals(lastFinishedMatchDay + 1);

        if(!canIncrement) lastFinishedMatchDay+= 1;
    }

    public boolean isAheadByTwoMatchDays() {

        return currentMatchDay.equals(externalCurrentMatchDay + 2);
    }
}
