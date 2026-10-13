package com.chrisnkl.cfbackend.application.service.report;

import com.chrisnkl.cfbackend.application.port.in.report.GetCategoriesUseCase;
import com.chrisnkl.cfbackend.application.port.out.report.GetCategoriesPort;
import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;

public class CategoryService implements GetCategoriesUseCase {

    private final GetCategoriesPort getCategoriesPort;

    public CategoryService(GetCategoriesPort getCategoriesPort) {
        this.getCategoriesPort = getCategoriesPort;
    }

    @Override
    public GetCategoriesResult getCategories(boolean enabled, double radius) {
        return getCategoriesPort.getCategories(enabled, radius);
    }
}
