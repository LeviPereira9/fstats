package lp.edu.fstats.repository.averages;

import lp.edu.fstats.model.avarages.Averages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AveragesRepository extends JpaRepository<Averages, Long> {
    List<Averages> findAllByCompetition_Id(Long competitionId);
}
