package lp.edu.fstats.service.averages;


import lp.edu.fstats.dto.averages.AveragesResponse;
import lp.edu.fstats.model.avarages.Averages;

import java.util.List;
import java.util.Map;

public interface AveragesService {
    Map<Long, Averages> findAllByCompetitionId(Long id);

    AveragesResponse findAllByCompetition(Long competitionId);

    void saveAll(List<Averages> averagesToSave);
}
