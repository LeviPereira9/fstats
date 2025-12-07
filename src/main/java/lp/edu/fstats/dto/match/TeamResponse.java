package lp.edu.fstats.dto.match;

import lp.edu.fstats.model.team.Team;

public record TeamResponse(
        Long id,
        String name,
        Integer goals,
        String emblem
) {
    public TeamResponse(Team source, Integer goals, String emblem){
        this(
                source.getId(),
                source.getShortName(),
                goals,
                emblem
        );
    }
}
