package com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.core.util.BinaryData;
import com.chrisnkl.cfbackend.application.port.out.report.ReportImageStoragePort;
import com.chrisnkl.cfbackend.application.result.report.ReportImageResult;
import com.chrisnkl.cfbackend.application.service.report.ReportImagePolicy;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.infrastructure.properties.BlobStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AzureBlobUploadReportImageAdapter implements ReportImageStoragePort {

    private final BlobServiceClient blobServiceClient;
    private final BlobStorageProperties properties;

    @Override
    public String upload(UUID reportId, String contentType, byte[] imageBytes) {
        String blobName = reportId + "-photo-" + Instant.now()
                + ReportImagePolicy.extensionFor(contentType);
        var containerClient = blobServiceClient.getBlobContainerClient(properties.containerName());
        containerClient.createIfNotExists();
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.upload(BinaryData.fromBytes(imageBytes), true);
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
        return blobClient.getBlobUrl();
    }

    @Override
    public void delete(String photoUrl) {
        getBlobClient(photoUrl).deleteIfExists();
    }

    @Override
    public ReportImageResult download(String photoUrl) {
        BlobClient blobClient = getBlobClient(photoUrl);
        if (!blobClient.exists()) {
            throw new HttpBackendException(HttpStatus.NOT_FOUND.value(), "Report image not found.");
        }
        var blobProperties = blobClient.getProperties();
        if (blobProperties.getBlobSize() <= 0
                || blobProperties.getBlobSize() > ReportImagePolicy.MAX_SIZE_BYTES
                || !ReportImagePolicy.supportsContentType(blobProperties.getContentType())) {
            throw new HttpBackendException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Stored report image is not supported.");
        }
        return new ReportImageResult(blobClient.downloadContent().toBytes(), blobProperties.getContentType());
    }

    private BlobClient getBlobClient(String photoUrl) {
        var containerClient = blobServiceClient.getBlobContainerClient(properties.containerName());
        String containerUrlPrefix = containerClient.getBlobContainerUrl() + "/";
        if (photoUrl == null || !photoUrl.startsWith(containerUrlPrefix)
                || photoUrl.contains("?") || photoUrl.contains("#")) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Invalid report image URL.");
        }
        BlobClient blobClient = containerClient.getBlobClient(photoUrl.substring(containerUrlPrefix.length()));
        if (!blobClient.getBlobUrl().equals(photoUrl)) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Invalid report image URL.");
        }
        return blobClient;
    }

}
