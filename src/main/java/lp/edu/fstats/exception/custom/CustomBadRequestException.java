package lp.edu.fstats.exception.custom;

public class CustomBadRequestException extends RuntimeException {
    public CustomBadRequestException(String message) {
        super(message);
    }

    public static CustomBadRequestException verificationTokenExpired() {
        return new CustomBadRequestException("O link de verificação expirou. Solicite um novo para continuar.");
    }

    public static CustomBadRequestException verificationTokenUsed() {
        return new CustomBadRequestException("Este link de verificação já foi utilizado. Solicite um novo se necessário.");
    }

    public static CustomBadRequestException userAlreadyVerified() {
        return new CustomBadRequestException("Esta conta já foi verificada anteriormente.");
    }

    public static CustomBadRequestException passwordDontMatch() {
        return new CustomBadRequestException("As senhas informadas não são semelhantes.");
    }

    public static CustomBadRequestException passwordDidntChange() {
        return new CustomBadRequestException("A nova senha não pode ser igual à senha atual.");
    }

    public static CustomBadRequestException invalidCurrentPassword() {
        return new CustomBadRequestException("A senha atual informada está incorreta.");
    }

    public static CustomBadRequestException verificationTokenType() {
        return new CustomBadRequestException("O token fornecido não corresponde ao tipo esperado para esta operação.");
    }
}
