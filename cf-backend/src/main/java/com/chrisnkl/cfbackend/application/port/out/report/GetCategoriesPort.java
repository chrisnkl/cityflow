package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.application.result.report.GetCategoriesResult;

public interface GetCategoriesPort {

    GetCategoriesResult getCategories(boolean enabled, double radius);


}
