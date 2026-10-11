package com.chrisnkl.cfbackend.domain.exception.auth;

import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends HttpBackendException {

    public UserAlreadyExistsException(String message) {
        super(409, message);
    }
}
