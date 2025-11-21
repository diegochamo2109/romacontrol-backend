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

import com.romacontrol.romacontrol_v1.dto.ErrorSimpleResponse;

/**
 * Manejo global de excepciones → devuelve JSON uniforme para el frontend.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // =========================================================
    // 1️⃣ VALIDACIONES @Valid (campos obligatorios, formatos, etc.)
    // =========================================================
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

            // 🎯 PERSONALIZAR MENSAJE SOLO PARA FECHA DE VENCIMIENTO
            if (fieldName.equals("fechaVencimiento")) {
                errorMessage = "La fecha de vencimiento no puede ser anterior al día actual.";
            }

            errors.put(fieldName, errorMessage);
        });

        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    // =========================================================
    // 2️⃣ IllegalArgumentException → errores de negocio directos
    // =========================================================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                new ErrorSimpleResponse(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // =========================================================
    // 3️⃣ IllegalStateException → conflictos del negocio
    // =========================================================
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        return new ResponseEntity<>(
                new ErrorSimpleResponse(ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    // =========================================================
    // 4️⃣ Otros errores NO controlados → error genérico
    // =========================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex) {
        return new ResponseEntity<>(
                new ErrorSimpleResponse("Error interno: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
