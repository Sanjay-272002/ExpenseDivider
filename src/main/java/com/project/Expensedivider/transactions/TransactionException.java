package com.project.Expensedivider.transactions;

import org.springframework.web.bind.annotation.RestControllerAdvice;


public class TransactionException extends Throwable {
    public TransactionException(String message) {
        super(message);
    }
}
