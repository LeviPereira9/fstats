package lp.edu.fstats.exception.custom;

import lombok.Getter;

@Getter
public class CustomDuplicateFieldException extends RuntimeException {
    private final String fieldName;

    public CustomDuplicateFieldException(String fieldName, String message) {
      super(message);
      this.fieldName = fieldName;
    }

    public static CustomDuplicateFieldException email() {
      return new CustomDuplicateFieldException("email", "Este e-mail já está sendo utilizado por outra conta.");
    }

    public static CustomDuplicateFieldException username() {
      return new CustomDuplicateFieldException("username", "Este nome de usuário já está sendo utilizado por outra conta.");
    }

    public static CustomDuplicateFieldException code() {
        return new CustomDuplicateFieldException("code", "Esse código já existe.");
    }
}
