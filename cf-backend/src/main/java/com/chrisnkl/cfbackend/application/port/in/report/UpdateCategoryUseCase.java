package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.command.report.UpdateCategoryCommand;
import com.chrisnkl.cfbackend.domain.model.report.Category;

public interface UpdateCategoryUseCase {

    Category updateCategory(UpdateCategoryCommand command);

}
