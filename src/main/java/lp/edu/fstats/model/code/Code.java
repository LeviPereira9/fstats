package lp.edu.fstats.model.code;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TB_Codigo")
@Data
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Codigo")
    private Integer id;

    @Column(name = "Ds_Codigo")
    private String code;

}
