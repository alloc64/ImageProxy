package com.alloc64.imageproxy.controller.advice;

import com.alloc64.imageproxy.img.exceptions.ImageProxyException;
import io.minio.errors.ErrorResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class DefaultControllerAdvice {
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public final ResponseEntity<String> handle(SQLIntegrityConstraintViolationException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Item already exists.");
    }
    @ExceptionHandler({NoSuchElementException.class})
    public final ResponseEntity<String> handle(NoSuchElementException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Item does not exists.");
    }

    @ExceptionHandler({ImageProxyException.class})
    public final ResponseEntity<String> handle(ImageProxyException ex, WebRequest request) {
        Throwable cause = ex.getCause();

        if(cause instanceof ErrorResponseException errorResponseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorResponseException.getMessage());
        }

        return ResponseEntity.status(ex.getStatus())
                .body(ex.getReason());
    }
}
