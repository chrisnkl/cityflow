package com.chrisnkl.cfbackend.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "azure.storage.blob")
public record BlobStorageProperties(
        String endpoint,
        String accountName,
        String accountKey,
        String containerName
) { }
