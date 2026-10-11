package com.chrisnkl.cfbackend.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class HttpBackendException extends BackendException {

    private final int status;

    public HttpBackendException(int status, String message) {
        this.status = status;
        super(message);
    }

}
