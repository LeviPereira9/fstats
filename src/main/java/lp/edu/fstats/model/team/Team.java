package lp.edu.fstats.model.team;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TB_Time")
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Time")
    private Long id;

    @Column(name = "ID_ExternoTime")
    private Long externalId;

    @Column(name = "Nm_Time")
    private String name;

    @Column(name = "Nm_TimeAbreviado")
    private String shortName;

    @Column(name = "Cd_SiglaTime")
    private String tla;

    @Column(name = "Ur_Emblema")
    private String crest;

    @Column(name = "St_Ativo")
    private boolean active = true;
}
