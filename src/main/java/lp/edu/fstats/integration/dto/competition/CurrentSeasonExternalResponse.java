package lp.edu.fstats.integration.dto.competition;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record CurrentSeasonExternalResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        @JsonProperty("currentMatchday")
        Integer currentMatchDay
) {}
