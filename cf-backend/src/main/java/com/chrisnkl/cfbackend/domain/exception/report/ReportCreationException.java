package com.chrisnkl.cfbackend.domain.exception.report;

import com.chrisnkl.cfbackend.domain.exception.BackendException;
import lombok.Getter;

@Getter
public class ReportCreationException extends BackendException {

    public ReportCreationException() {
        super();
    }

    public ReportCreationException(String message) {
        super(message);
    }
}
