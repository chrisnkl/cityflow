package com.chrisnkl.cfbackend.application.result.report;

import com.chrisnkl.cfbackend.domain.model.report.Category;

import java.util.List;

public record GetCategoriesResult(

        List<Category> categories

) {
}
