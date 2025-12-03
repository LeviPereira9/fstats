package lp.edu.fstats.integration.dto.matches.match;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record SeasonExternalResponse(
        LocalDate startDate,
        LocalDate endDate,
        @JsonProperty("currentMatchday")
        Integer currentMatchDay
) {}
