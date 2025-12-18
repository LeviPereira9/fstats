package lp.edu.fstats.doc.annotations.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "Autentica o usuário",
        description = "Realiza a autenticação do usuário a partir de suas credenciais e retorna um token de acesso."
)
/*@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Usuário autenticado com sucesso"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Credenciais inválidas"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Usuário não encontrado"
        )
})*/
public @interface DocLoginUser {}
