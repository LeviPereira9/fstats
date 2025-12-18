package lp.edu.fstats.doc.annotations.verification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Confirmar e-mail",
        description = "Confirma o e-mail de um usuário a partir de um token de verificação."
)
@ApiResponse
public @interface DocConfirmEmail {}
