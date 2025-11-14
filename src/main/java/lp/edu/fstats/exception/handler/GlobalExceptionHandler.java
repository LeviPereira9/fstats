package lp.edu.fstats.exception.handler;

import lp.edu.fstats.exception.custom.CustomBadRequestException;
import lp.edu.fstats.exception.custom.CustomDuplicateFieldException;
import lp.edu.fstats.exception.custom.CustomForbiddenActionException;
import lp.edu.fstats.exception.custom.CustomNotFoundException;
import lp.edu.fstats.response.normal.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                fieldError -> fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Response<Void> response = Response
                .<Void>builder()
                .operation("Error.Validation")
                .code(status.value())
                .message("Erro de validação")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(CustomForbiddenActionException.class)
    public ResponseEntity<Response<Void>> handleCustomForbiddenActionException(
            CustomForbiddenActionException ex) {

        HttpStatus status = HttpStatus.FORBIDDEN;

        Response<Void> response = Response
                .<Void>builder()
                .operation("Error.ForbiddenAction")
                .code(status.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<Response<Void>> handleCustomNotFoundException(
            CustomNotFoundException ex) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        Response<Void> response = Response
                .<Void>builder()
                .operation("Error.NotFound")
                .code(status.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(CustomDuplicateFieldException.class)
    public ResponseEntity<Response<Void>> handleCustomDuplicateFieldException(
            CustomDuplicateFieldException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> fieldErrors = Map.of(ex.getFieldName(), ex.getMessage());

        Response<Void> response = Response
                .<Void>builder()
                .operation("Error.DuplicateField")
                .code(status.value())
                .message("Recurso duplicado.")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<Response<Void>> handleBadRequestException(
            CustomBadRequestException ex
    ){
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Response<Void> response = Response
                .<Void>builder()
                .operation("Error.BadRequest")
                .code(status.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(response);
    }

}
