package com.chrisnkl.cfbackend.infrastructure.config;

import com.chrisnkl.cfbackend.application.port.out.report.FindNearbyReportsPort;
import com.chrisnkl.cfbackend.application.port.out.report.CreateReportPort;
import com.chrisnkl.cfbackend.application.port.out.report.GetCategoriesPort;
import com.chrisnkl.cfbackend.application.service.report.CategoryService;
import com.chrisnkl.cfbackend.application.service.report.ReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaAuditing
public class ProjectConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.usePathSegment(1).addSupportedVersions("v1").setDefaultVersion("v1");
    }

    @Bean
    public ReportService reportService(FindNearbyReportsPort findNearbyReportsPort, CreateReportPort createReportPort) {
        return new ReportService(findNearbyReportsPort, createReportPort);
    }

    @Bean
    public CategoryService categoryService(GetCategoriesPort getCategoriesPort) {
        return new CategoryService(getCategoriesPort);
    }

}
