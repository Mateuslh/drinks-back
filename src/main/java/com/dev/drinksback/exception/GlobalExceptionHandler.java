package com.dev.drinksback.exception;

import com.dev.drinksback.dto.PaginatedResponseDto;
import com.dev.drinksback.dto.ResponseEntityDto;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;

import java.util.Collections;

@Order(1)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<?> handlePropertyValueException(PropertyValueException ex, WebRequest request) {
        String message = "Erro: A propriedade '" + ex.getPropertyName() + "' da entidade '" + ex.getEntityName() + "' não pode ser nula.";
        return handleExceptionResponse(ex, request, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        Throwable cause = ex.getCause();
        String message = "Erro de integridade no banco de dados: " + ex.getMessage();

        if (cause instanceof ConstraintViolationException) {
            message = "Erro de integridade: Violação de restrição de integridade.";
        } else if (cause instanceof PropertyValueException) {
            message = "Erro: A propriedade não pode ser nula.";
        }

        return handleExceptionResponse(ex, request, message);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<?> handlePropertyReferenceException(PropertyReferenceException ex, WebRequest request) {
        String entityName;
        String propertyName = ex.getPropertyName();

        Throwable cause = ex.getCause();
        if (cause instanceof PropertyReferenceException propertyExCause) {
            propertyName = propertyExCause.getPropertyName();
        }

        if (ex.getType() != null && ex.getType().getType() != null) {
            entityName = ex.getType().getType().getSimpleName();
        } else {
            entityName = "Entidade desconhecida";
        }

        String message = "Erro de ordenação ou filtragem: A propriedade '" + propertyName +
                "' não foi encontrada na entidade '" + entityName + "'.";

        return handleExceptionResponse(ex, request, message);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleException(Exception ex, WebRequest request) {
        String message = "Erro: " + ex.getMessage() + " - " + ex.getClass().getSimpleName();
        return handleExceptionResponse(ex, request, message);
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<?> handleException(EntidadeNaoEncontradaException ex, WebRequest request) {
        return handleExceptionResponse(ex, request, ex.getMessage());
    }

    private ResponseEntity<?> handleExceptionResponse(Exception ex, WebRequest request, String message) {
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler", 0);

        if (handlerMethod != null && isPaginatedController(handlerMethod)) {
            PaginatedResponseDto<Void> paginatedResponse = new PaginatedResponseDto<>();
            paginatedResponse.setMessage(message);
            paginatedResponse.setSuccess(false);
            paginatedResponse.setTotal(0);
            paginatedResponse.setHasNext(false);
            paginatedResponse.setContent(Collections.emptyList());

            return new ResponseEntity<>(paginatedResponse, HttpStatus.BAD_REQUEST);
        } else {
            ResponseEntityDto<Void> responseEntityDto = new ResponseEntityDto<>();
            responseEntityDto.setMessage(message);
            responseEntityDto.setSuccess(false);

            return new ResponseEntity<>(responseEntityDto, HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isPaginatedController(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().getReturnType().equals(PaginatedResponseDto.class);
    }
}
