package com.chrisnkl.cfbackend.domain.exception.auth;

import com.chrisnkl.cfbackend.domain.exception.BackendException;
import lombok.Getter;

@Getter
public class FailedRegistrationException extends BackendException {

    public FailedRegistrationException(String message) {
        super(message);
    }
}
