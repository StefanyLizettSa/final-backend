package com.Hernandez.Biblioteca1.config;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", 400);
        respuesta.put("error", "Error de validación");
        respuesta.put("campos", errores);

        return ResponseEntity.status(400).body(respuesta);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> manejarCredencialesInvalidas(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(
            Map.of("estado", 401, "error", "Credenciales incorrectas",
                   "mensaje", "Email o contraseña inválidos")
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> manejarUsuarioNoEncontrado(UsernameNotFoundException ex) {
        return ResponseEntity.status(401).body(
            Map.of("estado", 401, "error", "Usuario no existe",
                   "mensaje", ex.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> manejarArgumentoIlegal(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(
            Map.of("estado", 400, "error", "Solicitud inválida",
                   "mensaje", ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarErrorGeneral(Exception ex) {
        return ResponseEntity.status(500).body(
            Map.of("estado", 500, "error", "Error interno del servidor",
                   "mensaje", ex.getMessage())
        );
    }
}
