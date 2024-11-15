package com.federico.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED,"No code"),
    INCORRECT_CURRENT_PASSWORD(300,BAD_REQUEST,"Password corrente incorretta"),
    NEW_PASSWORD_DOES_NOT_MATCH(301,BAD_REQUEST,"La nuova password non corrisponde"),
    ACCOUNT_LOCKED(302,FORBIDDEN,"Utente bloccato"),
    ACCOUNT_DISABLED(303,FORBIDDEN,"Utente disabilitato"),
    BAD_CREDENTIALS(304,FORBIDDEN,"Email o password non corretta");

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(final int code, final HttpStatus httpStatus, final String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
