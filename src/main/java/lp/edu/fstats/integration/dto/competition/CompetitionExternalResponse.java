package lp.edu.fstats.integration.dto.competition;

import lp.edu.fstats.model.competition.Competition;

public record CompetitionExternalResponse(
    Long id,
    String name,
    String code,
    String type,
    String emblem,
    CurrentSeasonExternalResponse currentSeason
) {
    public Competition toModel(){
        Competition competition = new Competition();

        competition.setExternalId(currentSeason().id());
        competition.setName(name);
        competition.setCode(code);
        competition.setType(type);
        competition.setEmblem(emblem);

        competition.setStartDate(currentSeason.startDate());
        competition.setEndDate(currentSeason.endDate());
        competition.setApiCurrentMatchDay(currentSeason.currentMatchDay());

        return competition;
    }

    public Competition update(Competition target) {
        target.setStartDate(currentSeason.startDate());
        target.setEndDate(currentSeason.endDate());
        target.setApiCurrentMatchDay(currentSeason.currentMatchDay());

        return target;
    }
}
