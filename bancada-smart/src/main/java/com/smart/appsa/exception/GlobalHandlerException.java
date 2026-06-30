package com.smart.appsa.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalHandlerException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(RuntimeException ex){
        log.error(ex.getMessage());
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex){
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex){
        log.error(ex.getMessage());
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
    @ExceptionHandler(PedidoIsAlreadyConcluidoException.class)
    public ResponseEntity<?> handlePedidoIsAlreadyConcluidoException(PedidoIsAlreadyConcluidoException ex){
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(SeletorTampaException.class)
    public ResponseEntity<?> handleSeletorTampaException(SeletorTampaException ex){
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(BlocoQuantityException.class)
    public ResponseEntity<?> handleBlocoQuantityException(BlocoQuantityException ex){
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(DuplicatedAndarException.class)
    public ResponseEntity<?> handleDuplicatedAndarException(DuplicatedAndarException ex){
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(DuplicatedLaminaPosition.class)
    public ResponseEntity<?> handleDuplicatedLaminaPosition(DuplicatedLaminaPosition ex){
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
