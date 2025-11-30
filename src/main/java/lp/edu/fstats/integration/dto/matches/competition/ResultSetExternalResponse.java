package lp.edu.fstats.integration.dto.matches.competition;

import java.time.LocalDate;

public record ResultSetExternalResponse(
        Integer count,
        LocalDate first,
        LocalDate last
) {}
