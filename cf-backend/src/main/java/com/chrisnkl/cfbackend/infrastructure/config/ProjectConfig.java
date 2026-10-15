package com.chrisnkl.cfbackend.infrastructure.config;

import com.chrisnkl.cfbackend.application.port.out.report.FindNearbyReportsPort;
import com.chrisnkl.cfbackend.application.port.out.report.CreateReportPort;
import com.chrisnkl.cfbackend.application.port.out.report.GetCategoriesPort;
import com.chrisnkl.cfbackend.application.port.out.report.ReportImageStoragePort;
import com.chrisnkl.cfbackend.application.port.out.report.FindReportPort;
import com.chrisnkl.cfbackend.application.port.out.report.ManageCategoryPort;
import com.chrisnkl.cfbackend.application.port.out.report.ManageReportPort;
import com.chrisnkl.cfbackend.application.service.report.CategoryService;
import com.chrisnkl.cfbackend.application.service.notification.NotificationService;
import com.chrisnkl.cfbackend.application.service.report.ReportService;
import com.chrisnkl.cfbackend.application.port.out.notification.FindAndSaveNearbyNotificationsPort;
import com.chrisnkl.cfbackend.application.port.out.notification.ManageUserNotificationsPort;
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
    public ReportService reportService(
            FindNearbyReportsPort findNearbyReportsPort,
            CreateReportPort createReportPort,
            ReportImageStoragePort reportImageStoragePort,
            FindReportPort findReportPort,
            ManageReportPort manageReportPort,
            com.chrisnkl.cfbackend.application.port.out.report.GetUserStatsPort getUserStatsPort
    ) {
        return new ReportService(findNearbyReportsPort, createReportPort, reportImageStoragePort, findReportPort,
                manageReportPort, getUserStatsPort);
    }

    @Bean
    public CategoryService categoryService(GetCategoriesPort getCategoriesPort, ManageCategoryPort manageCategoryPort) {
        return new CategoryService(getCategoriesPort, manageCategoryPort);
    }

    @Bean
    public NotificationService notificationService(
            FindAndSaveNearbyNotificationsPort findAndSaveNearbyNotificationsPort,
            ManageUserNotificationsPort manageUserNotificationsPort
    ) {
        return new NotificationService(findAndSaveNearbyNotificationsPort, manageUserNotificationsPort);
    }

    @Bean
    public com.chrisnkl.cfbackend.application.service.report.SystemParametersService systemParametersService(
            com.chrisnkl.cfbackend.application.port.out.report.ManageSystemParametersPort manageSystemParametersPort
    ) {
        return new com.chrisnkl.cfbackend.application.service.report.SystemParametersService(manageSystemParametersPort);
    }

}
