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

    @Column(name = "Nr_AtualRodada")
    private Integer currentMatchDay = 1;

    @Column(name = "St_Competicao")
    private String status = "Em andamento";


    public void incrementMatchDay() {
        currentMatchDay += 1;
    }
}
