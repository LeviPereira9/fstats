package lp.edu.fstats.doc.annotations.verification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Solicitar redefinição de senha",
        description = "Envia um e-mail com instruções para redefinição de senha de um usuário."
)
@ApiResponse
public @interface DocSendForgotPasswordEmail {}
