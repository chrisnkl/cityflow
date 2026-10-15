package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.command.report.UpdateSystemParametersCommand;
import com.chrisnkl.cfbackend.domain.model.report.SystemParameters;

public interface ManageSystemParametersUseCase {

    SystemParameters getSystemParameters();

    SystemParameters updateSystemParameters(UpdateSystemParametersCommand command);
}
