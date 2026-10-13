package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;

public interface GetCategoriesUseCase {

    GetCategoriesResult getCategories(boolean enabled, double radius);

}
