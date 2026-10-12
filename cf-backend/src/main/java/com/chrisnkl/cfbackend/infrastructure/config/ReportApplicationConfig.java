package com.chrisnkl.cfbackend.infrastructure.config;

import com.chrisnkl.cfbackend.application.port.out.report.FindNearbyReportsPort;
import com.chrisnkl.cfbackend.application.service.report.ReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportApplicationConfig {

    @Bean
    public ReportService reportService(FindNearbyReportsPort findNearbyReportsPort) {
        return new ReportService(findNearbyReportsPort);
    }
}
