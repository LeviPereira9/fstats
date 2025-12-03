package lp.edu.fstats.dto.match;

public record TeamResponse(
        Long id,
        String name,
        Integer goals,
        String emblem
) {}
