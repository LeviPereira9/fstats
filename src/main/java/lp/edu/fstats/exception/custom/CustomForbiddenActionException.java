package lp.edu.fstats.exception.custom;

public class CustomForbiddenActionException extends RuntimeException {
    public CustomForbiddenActionException(String message) {
        super(message);
    }

    public static CustomForbiddenActionException notAuthenticated() {
        return new CustomForbiddenActionException("Usuário não autenticado. Faça login para continuar.");
    }

    public static CustomForbiddenActionException notAuthorized() {
        return new CustomForbiddenActionException("Ação não permitida. Apenas o próprio usuário ou um moderador pode realizar esta operação.");
    }

    public static CustomForbiddenActionException emailNotVerified() {
        return new CustomForbiddenActionException("Ação não permitida. Apenas usuários com e-mails verificados podem realizar esta operação.");
    }
}
