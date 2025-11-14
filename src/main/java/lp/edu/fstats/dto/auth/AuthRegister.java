package lp.edu.fstats.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lp.edu.fstats.model.user.User;
import lp.edu.fstats.snippets.validator.Adult;

import java.time.LocalDate;

public record AuthRegister(
        String profilePicture,
        @NotBlank(message = "Informe o nome de usuário.")
        String username,
        @Email(message = "Informe um e-mail válido.")
        String email,
        @Size(min = 8, message = "A senha precisa conter no mínimo 8 caracteres.")
        String password,
        @Size(min = 8, message = "A senha precisa conter no mínimo 8 caracteres.")
        String confirmPassword,
        @Adult LocalDate dateOfBirth
) {

    public boolean passwordMatch(){
        return password.equals(confirmPassword);
    }

    public User toModel(String encryptedPassword){
        User user = new User();
        user.setProfilePicture(this.profilePicture);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setDateOfBirth(this.dateOfBirth);
        user.setPassword(encryptedPassword);

        return user;
    }

}
