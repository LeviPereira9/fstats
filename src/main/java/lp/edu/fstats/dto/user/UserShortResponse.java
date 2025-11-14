package lp.edu.fstats.dto.user;

import lp.edu.fstats.model.user.User;

public record UserShortResponse(
        Long id,
        String profilePicture,
        String username,
        String role
) {
    public UserShortResponse(User source){
        this(
                source.getId(),
                source.getProfilePicture(),
                source.getUsername(),
                source.getRole().getName()
        );
    }
}
