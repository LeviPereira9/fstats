package lp.edu.fstats.repository.team;

import lp.edu.fstats.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByExternalIdIn(List<Long> externalIds);

}
