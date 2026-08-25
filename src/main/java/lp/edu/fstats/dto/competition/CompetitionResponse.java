package lp.edu.fstats.dto.competition;

import lp.edu.fstats.dto.code.CodeResponse;
import lp.edu.fstats.model.competition.Competition;

import java.time.LocalDate;

public record CompetitionResponse(
        Long id,
        String name,
        CodeResponse code,
        String type,
        String emblem,
        Integer currentMatchDay,
        Integer limitMatchDay,
        Integer lastFinishedMatchDay,
        Integer count,
        LocalDate startDate,
        LocalDate endDate
) {

    public CompetitionResponse (Competition competition) {
        this(
                competition.getId(),
                competition.getName(),
                new CodeResponse(competition.getCode()),
                competition.getType(),
                competition.getEmblem(),
                competition.getApiCurrentMatchDay(),
                competition.getStoredMatchDay(),
                competition.getLastCompletedMatchDay(),
                competition.getCount(),
                competition.getStartDate(),
                competition.getEndDate()
        );
    }

}