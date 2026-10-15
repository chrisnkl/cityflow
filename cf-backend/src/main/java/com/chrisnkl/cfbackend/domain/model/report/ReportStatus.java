package com.chrisnkl.cfbackend.domain.model.report;

public enum ReportStatus {

    PENDING,
    UNDER_INVESTIGATION,
    IN_PROGRESS,
    REJECTED,
    RESOLVED;

    public boolean isFinal() {
        return this == REJECTED || this == RESOLVED;
    }
}
