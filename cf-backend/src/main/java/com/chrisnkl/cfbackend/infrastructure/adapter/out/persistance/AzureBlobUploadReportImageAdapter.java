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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
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
        boolean exists = blobClient.exists();
        log.info("Downloading blob for photoUrl='{}' -> container='{}', blobName='{}', exists={}",
                photoUrl, blobClient.getContainerName(), blobClient.getBlobName(), exists);
        if (!exists) {
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
        if (photoUrl == null || photoUrl.isBlank()) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Invalid report image URL.");
        }

        String cleaned = photoUrl;
        int queryIdx = cleaned.indexOf('?');
        if (queryIdx != -1) {
            cleaned = cleaned.substring(0, queryIdx);
        }
        int fragmentIdx = cleaned.indexOf('#');
        if (fragmentIdx != -1) {
            cleaned = cleaned.substring(0, fragmentIdx);
        }

        int lastSlashIdx = cleaned.lastIndexOf('/');
        String rawBlobName = (lastSlashIdx != -1) ? cleaned.substring(lastSlashIdx + 1) : cleaned;

        // Try raw name first, then URL-decoded name if encoded (e.g. %3A for colons in timestamp)
        String decodedBlobName = rawBlobName;
        try {
            decodedBlobName = java.net.URLDecoder.decode(rawBlobName, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }

        if (decodedBlobName.isBlank() || decodedBlobName.contains("..")) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Invalid report image URL.");
        }

        var containerClient = blobServiceClient.getBlobContainerClient(properties.containerName());
        BlobClient client = containerClient.getBlobClient(rawBlobName);
        if (!client.exists() && !rawBlobName.equals(decodedBlobName)) {
            BlobClient decodedClient = containerClient.getBlobClient(decodedBlobName);
            if (decodedClient.exists()) {
                return decodedClient;
            }
        }
        return client;
    }

}
