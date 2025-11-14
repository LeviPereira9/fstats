package lp.edu.fstats.dto.verification;

import jakarta.validation.constraints.Size;

public record VerificationPasswordResetRequest(
        @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres.")
        String newPassword,
        @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres.")
        String confirmNewPassword
) {
    public boolean passwordMatches() {
        return newPassword.equals(confirmNewPassword);
    }
}