package com.chrisnkl.cfbackend.application.result.report;

public record ReportImageResult(byte[] content, String contentType) {

    public ReportImageResult {
        content = content.clone();
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
