package com.afci.training.planning.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

// Exceptions custom si tu les utilises
import com.afci.training.planning.exception.NotFoundException;
import com.afci.training.planning.exception.ConflictException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // --- 404 : entité non trouvée (JPA) ---
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String,Object>> handleNotFoundJPA(EntityNotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
  }

  // --- 404 : custom NotFound ---
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String,Object>> handleNotFound(NotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
  }

  // --- 409 : conflit métier ---
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<Map<String,Object>> handleConflict(ConflictException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
  }

  // --- 400 : validation @Valid sur DTO @RequestBody ---
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .findFirst().orElse("Validation error");
    return ResponseEntity.badRequest().body(Map.of("error", msg));
  }

  // --- 400 : validation @Validated sur params (@RequestParam, @PathVariable, etc.) ---
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String,Object>> handleConstraint(ConstraintViolationException ex){
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  // --- 400 : mauvais format JSON / type incompatible ---
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String,Object>> handleBadJson(HttpMessageNotReadableException ex){
    return ResponseEntity.badRequest().body(Map.of("error", "Malformed JSON or invalid type"));
  }

  // --- 400 : mauvais type sur un paramètre (ex: id=abc au lieu d’un entier) ---
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String,Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex){
    String msg = "Parameter '" + ex.getName() + "' has invalid value";
    return ResponseEntity.badRequest().body(Map.of("error", msg));
  }

  // --- 400 : règle métier simple ---
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String,Object>> handleIllegalArgument(IllegalArgumentException ex){
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  // --- 403 : sécurité (Spring Security) ---
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String,Object>> handleAccessDenied(AccessDeniedException ex){
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
  }

  // --- Déjà mappées avec status dans l’exception ---
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String,Object>> handleResponseStatus(ResponseStatusException ex){
    return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()));
  }

  // --- 500 : fallback ---
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error"));
  }
}
