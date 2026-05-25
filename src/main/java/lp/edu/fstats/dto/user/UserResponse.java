package lp.edu.fstats.dto.user;

import lp.edu.fstats.model.user.User;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String profilePicture,
        String username,
        String email,
        String bio,
        LocalDate dateOfBirth,
        LocalDate createdAt,
        boolean verified,
        String role

) {

    public UserResponse(User source){
        this(
                source.getId(),
                source.getProfilePicture(),
                source.getUsername(),
                source.getEmail(),
                source.getBio(),
                source.getDateOfBirth(),
                source.getCreatedAt(),
                source.isVerified(),
                source.getRole().getName()
        );

    }

}
