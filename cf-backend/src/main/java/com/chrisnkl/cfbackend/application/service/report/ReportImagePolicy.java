package com.chrisnkl.cfbackend.application.service.report;

import java.util.Set;

public final class ReportImagePolicy {

    public static final long MAX_SIZE_BYTES = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private ReportImagePolicy() { }

    public static boolean supportsContentType(String contentType) {
        return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType);
    }

    public static String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Unsupported image content type.");
        };
    }
}
