package com.smart.appsa.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex){
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
    @ExceptionHandler(PedidoIsAlreadyConcluidoException.class)
    public ResponseEntity<?> handlePedidoIsAlreadyConcluidoException(PedidoIsAlreadyConcluidoException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(SeletorTampaException.class)
    public ResponseEntity<?> handleSeletorTampaException(SeletorTampaException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(BlocoQuantityException.class)
    public ResponseEntity<?> handleBlocoQuantityException(BlocoQuantityException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(DuplicatedAndarException.class)
    public ResponseEntity<?> handleDuplicatedAndarException(DuplicatedAndarException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(DuplicatedLaminaPosition.class)
    public ResponseEntity<?> handleDuplicatedLaminaPosition(DuplicatedLaminaPosition ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
