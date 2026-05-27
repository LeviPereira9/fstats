package lp.edu.fstats.model.favorites;

import jakarta.persistence.*;
import lombok.Data;
import lp.edu.fstats.model.code.Code;
import lp.edu.fstats.model.competition.Competition;
import lp.edu.fstats.model.user.User;

@Entity
@Table(name = "TB_CompeticoesFavoritas")
@Data
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Favorito")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Competicao")
    private Code competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_Usuario")
    private User user;

    public String getCompetitionCode(){
        return competition.getCode();
    }

    public String getCompetitionName(){
        return competition.getName();
    }

}
