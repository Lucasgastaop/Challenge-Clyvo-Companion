package br.com.fiap.clyvo_companion.exception;

import br.com.fiap.clyvo_companion.service.LogSistemaAuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LogSistemaAuditoriaService logSistemaAuditoriaService;

    public GlobalExceptionHandler(LogSistemaAuditoriaService logSistemaAuditoriaService) {
        this.logSistemaAuditoriaService = logSistemaAuditoriaService;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex, request, ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex, request, ex.getMessage(), null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex, request, ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex, request, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldError)
                .toList();

        return build(HttpStatus.BAD_REQUEST, ex, request, "Dados inválidos", fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex, request, ex.getMessage(), null);
    }

    private ErrorResponse.FieldErrorDetail toFieldError(FieldError error) {
        ErrorResponse.FieldErrorDetail detail = new ErrorResponse.FieldErrorDetail();
        detail.setField(error.getField());
        detail.setMessage(error.getDefaultMessage());
        return detail;
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            Exception ex,
            HttpServletRequest request,
            String message,
            List<ErrorResponse.FieldErrorDetail> fieldErrors) {

        logSistemaAuditoriaService.registrar(request, status, ex);

        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(LocalDateTime.now());
        body.setStatus(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage(message);
        body.setPath(request.getRequestURI());
        body.setFieldErrors(fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
