package lp.edu.fstats.exception.custom;

public class CustomInternalServerError extends RuntimeException {
    public CustomInternalServerError(String message) {
        super(message);
    }

    public static CustomInternalServerError tokenCreation(){
        return new CustomInternalServerError("Falha ao gerar o token de autenticação. Tente novamente mais tarde.");
    }

    public static CustomInternalServerError tokenValidation(){
        return new CustomInternalServerError("O token de autenticação é inválido ou expirou. Solicite um novo acesso.");
    }
}
