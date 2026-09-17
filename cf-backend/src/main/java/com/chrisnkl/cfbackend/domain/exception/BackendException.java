package com.chrisnkl.cfbackend.domain.exception;

import lombok.Getter;

@Getter
public class BackendException extends RuntimeException {

    public BackendException() {
        super();
    }

    public BackendException(String message) {
        super(message);
    }
}
