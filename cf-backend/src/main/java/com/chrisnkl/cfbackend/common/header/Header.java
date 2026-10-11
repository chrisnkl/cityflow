package com.chrisnkl.cfbackend.common.header;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Header {

    REQUEST_ID("X-REQUEST-ID"),
    AUTHORIZATION("Authorization");

    private final String value;

}
