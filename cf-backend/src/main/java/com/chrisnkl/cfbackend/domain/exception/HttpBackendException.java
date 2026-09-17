package com.chrisnkl.cfbackend.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HttpBackendException extends BackendException {

    private final int status;

}
