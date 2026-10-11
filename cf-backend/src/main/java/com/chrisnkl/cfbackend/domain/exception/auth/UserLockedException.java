package com.chrisnkl.cfbackend.domain.exception.auth;

import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import lombok.Getter;

@Getter
public class UserLockedException extends HttpBackendException {

    public UserLockedException(String message) {
        super(423, message);
    }
}
