package com.training.validation.demo.controllers;

import com.training.validation.demo.api.BankAccountNotFoundException;
import com.training.validation.demo.api.InsufficientFundsException;
import com.training.validation.demo.api.SavingsAccountException;
import com.training.validation.demo.transports.ErrorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@ControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BinaryExceptionClassifier transientClassifier = new BinaryExceptionClassifier(singletonMap(TransientDataAccessException.class, true), false);
    {
        transientClassifier.setTraverseCauses(true);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorModel> handle(ValidationException ex) {
        return ResponseEntity.badRequest()
                             .body(new ErrorModel(ex.getMessages()));
    }


    @ExceptionHandler
    public ResponseEntity<ErrorModel> handle(InsufficientFundsException ex) {

        //look how powerful are the contextual exceptions!!!
        String message = String.format("The bank account %s has a balance of $%.2f. Therefore you cannot withdraw $%.2f since you're short $%.2f",
                ex.getAccountNumber(), ex.getBalance(), ex.getWithdrawal(), ex.getWithdrawal() - ex.getBalance());

        logger.warn(message, ex);
        return ResponseEntity.badRequest()
                             .body(new ErrorModel(message));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorModel> handle(BankAccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ErrorModel(ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorModel> handle(SavingsAccountException ex) {
        if(isTransient(ex)) {
            //notice how logging level changes depending on whether the exception is transient or persistent
            logger.warn("Failure while processing operation on savings account: {}", ex.getAccountNumber(), ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                 .header("Retry-After", "5000")
                                 .body(new ErrorModel(ex.getMessage()));
        } else {
            logger.error("Failure while processing operation on savings account: {}", ex.getAccountNumber(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ErrorModel(ex.getMessage()));
        }
    }


    //since we add nullability and constraints checks to our DTOs in their constructors
    //these might fail even before reaching the Bean Validation phase, so by adding this
    //handler we make sure to respond with an appropriate error model when that occurs.
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable cause = ex.getCause();
        while (cause != null && !(cause instanceof NullPointerException || cause instanceof IllegalArgumentException)) {
            cause = cause.getCause();
        }
        if (cause != null) {
            return ResponseEntity.badRequest()
                                 .body(new ErrorModel(singletonList(cause.getMessage())));
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    private boolean isTransient(Throwable cause) {
        return transientClassifier.classify(cause);
    }

}
