package com.chrisnkl.cfbackend.infrastructure.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.chrisnkl.cfbackend.infrastructure.properties.BlobStorageProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlobStorageConfig {

    @Bean
    public BlobServiceClient blobServiceClient(BlobStorageProperties properties) {
        return new BlobServiceClientBuilder()
                .endpoint(properties.endpoint())
                .credential(new StorageSharedKeyCredential(properties.accountName(), properties.accountKey()))
                .buildClient();
    }
}
