package lp.edu.fstats.doc.annotations.verification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Reenviar e-mail de confirmação",
        description = "Reenvia o e-mail de confirmação de conta para um usuário."
)
@ApiResponse
public @interface DocResendConfirmationEmail {}
