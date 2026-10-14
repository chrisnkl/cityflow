package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.application.result.report.ReportImageResult;

import java.util.UUID;

public interface ReportImageStoragePort {

    String upload(UUID reportId, String contentType, byte[] imageBytes);

    void delete(String photoUrl);

    ReportImageResult download(String photoUrl);
}
