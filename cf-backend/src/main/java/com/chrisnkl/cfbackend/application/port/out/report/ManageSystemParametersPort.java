package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.domain.model.report.SystemParameters;

public interface ManageSystemParametersPort {

    SystemParameters getParameters();

    SystemParameters saveParameters(SystemParameters parameters);
}
