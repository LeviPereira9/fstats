package lp.edu.fstats.dto.competition;

import jakarta.persistence.Column;
import lp.edu.fstats.model.competition.Competition;

import java.time.LocalDate;

public record CompetitionResponse(
        Long id,
        String name,
        String code,
        String type,
        String emblem,
        Integer currentMatchDay,
        Integer count,
        LocalDate startDate,
        LocalDate endDate
) {

    public CompetitionResponse (Competition competition) {
        this(
                competition.getId(),
                competition.getName(),
                competition.getCode(),
                competition.getType(),
                competition.getEmblem(),
                competition.getCurrentMatchDay(),
                competition.getCount(),
                competition.getStartDate(),
                competition.getEndDate()
        );
    }

}