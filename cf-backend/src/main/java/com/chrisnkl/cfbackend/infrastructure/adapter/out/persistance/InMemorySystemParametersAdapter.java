package com.chrisnkl.cfbackend.infrastructure.adapter.out.persistance;

import com.chrisnkl.cfbackend.application.port.out.report.ManageSystemParametersPort;
import com.chrisnkl.cfbackend.domain.model.report.SystemParameters;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class InMemorySystemParametersAdapter implements ManageSystemParametersPort {

    private final AtomicReference<SystemParameters> parameters =
            new AtomicReference<>(SystemParameters.defaults());

    @Override
    public SystemParameters getParameters() {
        return parameters.get();
    }

    @Override
    public SystemParameters saveParameters(SystemParameters newParams) {
        parameters.set(newParams);
        return newParams;
    }
}
