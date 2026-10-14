package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.domain.event.ReportCreatedEvent;

public interface ReportCreatedEventPort {

    void save(ReportCreatedEvent event);

}
