package lp.edu.fstats.controller.verification;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lp.edu.fstats.dto.verification.VerificationPasswordResetRequest;
import lp.edu.fstats.response.normal.Response;
import lp.edu.fstats.service.verification.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.prefix}/verify")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    @PostMapping("/email")
    public ResponseEntity<Response<Void>> confirmEmailChange(
            @RequestParam String username,
            @RequestParam String token) {

        verificationService.confirmEmailChange(username, token);

        Response<Void> response = Response.<Void>builder()
                .operation("Verification.ConfirmEmailChange")
                .code(HttpStatus.OK.value())
                .message("E-mail trocado com sucesso.")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend")
    public ResponseEntity<Response<Void>> resendConfirmationEmail(@RequestParam String username){
        verificationService.resendConfirmationEmail(username);

        int code = HttpStatus.OK.value();

        Response<Void> response = Response.<Void>builder()
                .operation("Verification.ResendConfirmationEmail")
                .code(code)
                .message("E-mail de confirmação enviado com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Response<Void>> confirmEmail(
            @RequestParam String username,
            @RequestParam String token){
        verificationService.confirmEmail(username, token);

        int code = HttpStatus.OK.value();

        Response<Void> response = Response.<Void>builder()
                .operation("Verification.ConfirmEmail")
                .code(code)
                .message("Verificação de e-mail concluída com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Response<Void>> sendForgotPasswordEmail(@RequestParam String username){
        verificationService.sendForgotPasswordEmail(username);

        int code = HttpStatus.OK.value();

        Response<Void> response = Response.<Void>builder()
                .operation("Verification.sendForgotPasswordEmail")
                .code(code)
                .message("E-mail de redefinição de senha enviado com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Response<Void>> resetPassword(
            @RequestParam String username,
            @RequestParam String token,
            @RequestBody @Valid VerificationPasswordResetRequest request){

        verificationService.resetPassword(username, token, request);

        int code = HttpStatus.OK.value();

        Response<Void> response = Response.<Void>builder()
                .operation("Verification.passwordReset")
                .code(code)
                .message("Senha alterada com sucesso.")
                .build();

        return ResponseEntity.status(code).body(response);
    }
}
