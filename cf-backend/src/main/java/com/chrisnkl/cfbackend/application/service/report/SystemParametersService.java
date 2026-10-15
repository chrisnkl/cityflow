package com.chrisnkl.cfbackend.application.service.report;

import com.chrisnkl.cfbackend.application.command.report.UpdateSystemParametersCommand;
import com.chrisnkl.cfbackend.application.port.in.report.ManageSystemParametersUseCase;
import com.chrisnkl.cfbackend.application.port.out.report.ManageSystemParametersPort;
import com.chrisnkl.cfbackend.domain.exception.HttpBackendException;
import com.chrisnkl.cfbackend.domain.model.report.SystemParameters;
import org.springframework.http.HttpStatus;

public class SystemParametersService implements ManageSystemParametersUseCase {

    private final ManageSystemParametersPort manageSystemParametersPort;

    public SystemParametersService(ManageSystemParametersPort manageSystemParametersPort) {
        this.manageSystemParametersPort = manageSystemParametersPort;
    }

    @Override
    public SystemParameters getSystemParameters() {
        return manageSystemParametersPort.getParameters();
    }

    @Override
    public SystemParameters updateSystemParameters(UpdateSystemParametersCommand command) {
        if (command == null) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Parameters cannot be null");
        }
        if (command.defaultRadiusMeters() <= 0) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Default radius must be greater than 0");
        }
        if (command.weightCount() < 0 || command.weightProximity() < 0 || command.weightSeverity() < 0) {
            throw new HttpBackendException(HttpStatus.BAD_REQUEST.value(), "Weights cannot be negative");
        }

        SystemParameters parameters = SystemParameters.builder()
                .defaultRadiusMeters(command.defaultRadiusMeters())
                .weightCount(command.weightCount())
                .weightProximity(command.weightProximity())
                .weightSeverity(command.weightSeverity())
                .build();

        return manageSystemParametersPort.saveParameters(parameters);
    }
}
