package com.yurdan.ascService.exception;

public class ProhibitionCreateOrderBasedOnClosedOOrOnPaymentRequestException extends RuntimeException{
    public ProhibitionCreateOrderBasedOnClosedOOrOnPaymentRequestException(String message) {
        super(message);
    }
}
