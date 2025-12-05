package lp.edu.fstats.exception.custom;

public class CustomNotFoundException extends RuntimeException {
    public CustomNotFoundException(String message) {
        super(message);
    }

    public static CustomNotFoundException user() {
        return new CustomNotFoundException("Usuário não encontrado. Verifique as informações e tente novamente.");
    }

    public static CustomNotFoundException role(){
        return new CustomNotFoundException("Cargo de usuário não encontrada. Verifique as configurações da conta.");
    }

    public static CustomNotFoundException jwtToken() {
        return new CustomNotFoundException("Token de autenticação não encontrado. Faça login novamente.");
    }

    public static CustomNotFoundException verificationTokenEmail() {
        return new CustomNotFoundException("Token de verificação de e-mail não encontrado. Solicite um novo envio.");
    }

    public static CustomNotFoundException verificationTokenPassword() {
        return new CustomNotFoundException("Token de redefinição de senha não encontrado. Solicite um novo envio.");
    }

    public static CustomNotFoundException competition(){
        return new CustomNotFoundException("Competição não encontrada. Solicite um novo envio.");
    }

    public static CustomNotFoundException match(){
        return new CustomNotFoundException("Nenhuma partida foi encontrada. Solicite um novo envio.");
    }

    public static CustomNotFoundException standings() {
        return new CustomNotFoundException("Classificação não encontrada. Solicite um novo envio.");
    }
}
