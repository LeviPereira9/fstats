package lp.edu.fstats.integration.dto.matches.competition;

public record CompetitionExternalResponse(
        Long id,
        String name,
        String code,
        String type,
        String emblem
) {}