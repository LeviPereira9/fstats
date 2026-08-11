package lp.edu.fstats.exception.custom;

public class CustomUnauthorizedException extends RuntimeException {

    public CustomUnauthorizedException(String message) {
        super(message);
    }

    public static CustomUnauthorizedException notAuthenticated() {
        return new CustomUnauthorizedException("Usuário não autenticado. Faça login para continuar.");
    }

    public static CustomUnauthorizedException wrongCredentials() {
        return new CustomUnauthorizedException("Login ou senha inválidos. Verifique seus dados e tente novamente.");
    }

}
