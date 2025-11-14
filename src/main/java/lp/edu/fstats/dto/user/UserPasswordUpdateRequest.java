package lp.edu.fstats.dto.user;

import jakarta.validation.constraints.Size;

public record UserPasswordUpdateRequest(
        @Size(min = 8, message = "A senha atual deve ter no mínimo 8 caracteres.")
        String currentPassword,
        @Size(min = 8, message = "A nova senha precisa ter no mínimo 8 caracteres.")
        String newPassword,
        @Size(min = 8, message = "A confirmação da nova senha deve ter no mínimo 8 caracteres")
        String confirmNewPassword
) {

    public boolean newPasswordsMatch() {
        return newPassword.equals(confirmNewPassword);
    }

    public boolean newPasswordIsEqualsToOld(){
        return newPassword.equals(currentPassword);
    }

}
