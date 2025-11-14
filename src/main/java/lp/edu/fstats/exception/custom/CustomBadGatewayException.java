package lp.edu.fstats.exception.custom;

public class CustomBadGatewayException extends RuntimeException
{
    public CustomBadGatewayException(String message) {
        super(message);
    }

    public static CustomBadGatewayException email(){
      return new CustomBadGatewayException("Não foi possível enviar o e-mail no momento, tente novamente mais tarde.");
    }
}
