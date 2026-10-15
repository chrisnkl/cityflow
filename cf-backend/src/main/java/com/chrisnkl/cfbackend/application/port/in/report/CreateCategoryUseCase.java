package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.command.report.CreateCategoryCommand;

import java.util.UUID;

public interface CreateCategoryUseCase {

    UUID createCategory(CreateCategoryCommand command);

}
