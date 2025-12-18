package lp.edu.fstats.doc.annotations.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Desativar usuário",
        description = "Desativa o usuário no sistema, impedindo novos acessos."
)
@ApiResponse
public @interface DocSoftDeleteUser {}
