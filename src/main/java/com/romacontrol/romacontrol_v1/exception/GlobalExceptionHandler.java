package com.romacontrol.romacontrol_v1.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Manejo global de excepciones -> devuelve JSON uniforme.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

@Override
protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        org.springframework.http.HttpStatusCode status,
        WebRequest request) {

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
    });

    body.put("errors", errors);
    return new ResponseEntity<>(body, headers, status);
}

  // === 2. IllegalArgumentException (bad request) ===
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  // === 3. IllegalStateException (conflictos, reglas de negocio) ===
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  // === 4. Default (errores no controlados) ===
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGeneral(Exception ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + ex.getMessage());
  }

  // === Utilitario ===
  private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    return new ResponseEntity<>(body, status);
  }
}
