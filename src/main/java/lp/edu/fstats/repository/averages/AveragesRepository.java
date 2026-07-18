package lp.edu.fstats.repository.averages;

import lp.edu.fstats.model.avarages.Averages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AveragesRepository extends JpaRepository<Averages, Long> {
    List<Averages> findAllByCompetition_Id(Long competitionId);
}
