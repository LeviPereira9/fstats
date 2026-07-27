package lp.edu.fstats.dto.favorites;

import jakarta.validation.constraints.NotNull;

public record FavoriteRemoveRequest(
        @NotNull
        Long favoriteId
) {}
