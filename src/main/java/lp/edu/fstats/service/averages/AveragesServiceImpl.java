package lp.edu.fstats.service.averages;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.averages.AveragesResponse;
import lp.edu.fstats.model.avarages.Averages;
import lp.edu.fstats.repository.averages.AveragesRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AveragesServiceImpl implements AveragesService{
    private final AveragesRepository averagesRepository;

    @Override
    public Map<Long, Averages> findAllByCompetitionId(Long competitionId) {
        List<Averages> averages = averagesRepository.findAllByCompetition_Id(competitionId);

        return averages.stream().collect(Collectors.toMap(
                Averages::getTeamId,
                Function.identity()
        ));
    }

    @Override
    @Cacheable(value = "averages", key = "'competitionId:' + #competitionId")
    public AveragesResponse findAllByCompetition(Long competitionId){
        List<Averages> averages = averagesRepository.findAllByCompetition_Id(competitionId);

        return AveragesResponse.toResponse(averages);
    }

    @Override
    @CacheEvict(value = "averages", allEntries = true)
    public void saveAll(List<Averages> averagesToSave) {
        averagesRepository.saveAll(averagesToSave);
    }
}
