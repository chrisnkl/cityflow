package com.chrisnkl.cfbackend.common.header;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Header {

    REQUEST_ID("X-REQUEST-ID");

    private final String value;

}
