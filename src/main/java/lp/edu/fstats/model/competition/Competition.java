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
    private Integer storedMatchDay = 1;

    @Column(name = "Nr_ExternoRodadaAtual")
    private Integer apiCurrentMatchDay = 1;

    @Column(name = "St_Competicao")
    private String status = "Em andamento";

    @Column(name = "Nr_UltimaRodadaFinalizada")
    private Integer lastCompletedMatchDay = 0;

    public void incrementMatchDay() {
        boolean alreadyHasFutureMatches = storedMatchDay.equals(apiCurrentMatchDay + 2);

        if(!alreadyHasFutureMatches) storedMatchDay += 1;;
    }

    public void incrementLastFinishedMatchDay() {
        lastCompletedMatchDay += 1;
    }

    public void decrementStoredMatchDay(){
        boolean canDecrement = apiCurrentMatchDay.equals(storedMatchDay - 1);

        if(canDecrement) storedMatchDay -= 1;
    }

    public boolean isTwoStoredMatchDaysAhead() {

        return storedMatchDay.equals(apiCurrentMatchDay + 2);
    }

    public boolean isFinished() {
        return storedMatchDay.equals(lastCompletedMatchDay) && apiCurrentMatchDay.equals(lastCompletedMatchDay);
    }
}
