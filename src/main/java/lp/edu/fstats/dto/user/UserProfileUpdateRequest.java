package lp.edu.fstats.dto.user;

import lp.edu.fstats.model.user.User;
import lp.edu.fstats.util.snippets.validator.Adult;

import java.time.LocalDate;

public record UserProfileUpdateRequest(
        String profilePicture,
        @Adult
        LocalDate dateOfBirth
) {
    public void updateUser(User target){
        target.setProfilePicture(profilePicture);
        target.setDateOfBirth(dateOfBirth);
        target.update();
    }
}
