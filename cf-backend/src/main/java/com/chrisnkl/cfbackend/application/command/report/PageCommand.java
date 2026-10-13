package com.chrisnkl.cfbackend.application.command.report;

public record PageCommand(int page, int size) {

    public static final int MAX_PAGE_SIZE = 100;

    public PageCommand {
        if (page < 0) {
            throw new IllegalArgumentException("Page index cannot be negative.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE + ".");
        }
    }
}
