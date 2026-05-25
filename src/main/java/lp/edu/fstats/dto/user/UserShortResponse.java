package lp.edu.fstats.dto.user;

import lp.edu.fstats.model.user.User;

import java.time.LocalDate;

public record UserShortResponse(
        Long id,
        String profilePicture,
        String username,
        String bio,
        LocalDate createdAt,
        String role
) {
    public UserShortResponse(User source){
        this(
                source.getId(),
                source.getProfilePicture(),
                source.getUsername(),
                source.getBio(),
                source.getCreatedAt(),
                source.getRole().getName()
        );
    }
}
