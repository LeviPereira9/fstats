package lp.edu.fstats.service.team;

import lombok.RequiredArgsConstructor;
import lp.edu.fstats.model.team.Team;
import lp.edu.fstats.repository.team.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;

    @Override
    public Map<Long, Team> findAllByExternalId(List<Long> externalIds) {
        List<Team> teams = teamRepository.findAllByExternalIdIn(externalIds);

        return teams.stream().collect(
                Collectors.toMap(Team::getExternalId, Function.identity()));
    }

    @Override
    public void saveTeams(List<Team> teams) {
        teams = teamRepository.saveAll(teams);
    }
}
