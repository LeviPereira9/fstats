package lp.edu.fstats.service.team;

import lp.edu.fstats.model.team.Team;

import java.util.List;
import java.util.Map;

public interface TeamService {
    Map<Integer, Team> findAllByExternalId(List<Integer> externalIds);
}
